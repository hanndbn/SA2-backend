/*
 * by thanhpk
 * THIS IS THE HTTP(s) HANDLER FOR THE SYSTEM
 * 
 * THE PURPOSE OF THIS MODULE IS TO TRANSPARENTIZE (OR DECOUPING) THE JAVA SERVLET
 * ARCHITECTURE TO THE HRWebSytem SO THAT HRWEBSYSTEM CAN DEVELOP ONLY BY THE JAVA
 * CORE WITHOUT KNOWING HOW SERVLET WORK
 * 
 * SECURITY PROBLEM
 * this is a uncompleted module, it still have some security vulna need to patch
 * imediately
 * 1. DoS (https://www.owasp.org/index.php/Denial_of_Service)
 * 
 * 2. maybe, just maybe path traverse
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.tvi.apply;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tvi.CaptchaProvider.CaptchaProvider;
import com.tvi.GmailWrapper.GmailWrapper;
import com.tvi.HttpLogger.HttpLogger;
import com.tvi.MySqlDb.MySqlDatabase;
import com.tvi.apply.commontype.*;
import com.tvi.common.ICaptchaProvider;
import com.tvi.common.IDatabase;
import com.tvi.common.ILogger;
import com.tvi.common.entity.*;
import com.tvi.common.manager.*;
import com.tvi.common.type.AccessDeny;
import com.tvi.common.util.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class HRWeb extends HttpServlet
{

	private final String domain = "http://apply.vuonuom.tv";
	//private final String domain = "http://localhost:8084/HRWeb";
	private final String filepath = "/hr-files";
	private final int cookieAge = 86400; // one day
	private final String cookieName = "authcode";
	private final String servletName = "HRWeb";
	private Gson gson = null;
	private final long maxFileSize = 25 * 1024L * 1024L;
	private final int maxMemSize = 20 * 1024;
	private ILogger logger = null;
	private ICaptchaProvider captchapr = null;
	private IHRSystem hr = null;
	private PrivateFileSaveUtil filesaver = null;
	PolicyFactory sanitizer;
	private IHtmlImageBase64Converter htmlconverter;
	private ID2HtmlConverter d2converter;
	private IDatabase db;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		sanitizer = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS).and(Sanitizers.STYLES).and(Sanitizers.LINKS).and(Sanitizers.IMAGES);

		GsonBuilder builder = new GsonBuilder();
		builder.disableHtmlEscaping();
		this.gson = builder.create();

		MySqlDatabase database = new MySqlDatabase();

		//database.connect("127.0.0.1", "root", "", "sao");
		database.connect("127.0.0.1", "root", "", "apply2");
		//database.connect("127.0.0.1", "hrteam", "YFbBw9dBeuWyQDE2", "hrteam");
		//database.connect("103.20.144.12", "hrteam", "YFbBw9dBeuWyQDE2", "hrteam");
		//database.connect("123.30.169.209", "apply", "root", "vdc1102");
		//database.connect("127.0.0.1", "root", "vdc1102", "apply");

		captchapr = new CaptchaProvider(database);
		logger = new HttpLogger(database);
		this.hr = new HRSystem(this.domain + "/", new Auth(database), new RequiredFieldMgt(database), new RequestMgrV2(database), new TestMgtV2(database), new UnitMgtV2(database), new EmailFormMgt(database), new GmailWrapper());
		// new FakeEmail());
	}

	private void setUp(HttpServletRequest request)
	{
		if (this.filesaver == null)
		{
			String basePath = new File(getServletContext().getRealPath("/")).getParentFile().getAbsolutePath() + this.filepath + "/";
			basePath = basePath.replace('\\', '/');
			this.filesaver = new PrivateFileSaveUtil(basePath, this.domain + "/HRWeb/file");
			this.d2converter = new D2HtmlConverter(this.filesaver);
			//IFileSaveHelper publicfilesaver = new PublicFileSaveUtil(basePath, this.domain + "/HRWeb/file");
			this.htmlconverter = new HtmlImageBase64Converter(filesaver);
		}
	}

	private boolean isLoggedIn(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
		{
			cookies = new Cookie[0];
		}
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(this.cookieName) && hr.matchAuth(cookie.getValue()) != -1)
			{
				return true;
			}
		}
		return false;
	}

	private void at_user_isloggedin(HttpServletRequest request, PrintWriter out) throws IOException
	{
		if (isLoggedIn(request))
		{
			out.print(gson.toJson(new Result<Boolean>(true)));
		} else
		{
			out.print(gson.toJson(new Result<Boolean>(false)));
		}
	}

	private long getUser(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
		{
			return -1;
		}
		long uid;
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(this.cookieName) && (uid = hr.matchAuth(cookie.getValue())) != -1)
			{
				return uid;
			}
		}
		//không tìm thấy ai
		return -1;
	}

	private void at_user_logout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid;
		if ((uid = getUser(request)) != -1)
		{

			Cookie[] cookies = request.getCookies();

			for (Cookie cookie : cookies)
			{
				if (cookie.getName().equals(this.cookieName) && (uid = hr.matchAuth(cookie.getValue())) != -1)
				{

					//remove the cookie
					Cookie c = new Cookie("authcode", "");
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
					hr.logout(uid, uid, cookie.getValue());
					break;
				}
			}

		} else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("You have not logged in yet");
		}
	}

	private void at_request_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		String jobdesc = request.getParameter("jobdesc");
		String position = request.getParameter("position");
		String interest = request.getParameter("interest");
		String requirement = request.getParameter("requirement");
		int unit = Integer.parseInt(request.getParameter("unit"));
		int quantity = 0;
		try
		{
			quantity = Integer.parseInt(request.getParameter("quantity"));
		} catch (NumberFormatException ignored)
		{
		}
		String title = request.getParameter("title");

		RequestEntityV2 req = new RequestEntityV2(0, jobdesc, title, position, interest, requirement, uid, null, quantity, 0);
		long id = hr.createRequest(uid, unit, req);
		out.print(gson.toJson(new Result<Long>(id)));
	}

	private void at_cv_count(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long bagid = Long.parseLong(request.getParameter("bag"));
		long uid = getUser(request);

		long n = hr.countCV(uid, bagid);
		out.print(gson.toJson(new Result<Long>(n)));
	}

	private void at_question_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		int p = Integer.parseInt(request.getParameter("p"));
		int ps = Integer.parseInt(request.getParameter("ps"));
		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));
		long uid = getUser(request);
		List<QuestionEntityV2> qs = hr.listQuestion(uid, unit, type, p, ps, null);
		List<QuestionEntityV2> goodqs = new ArrayList<QuestionEntityV2>();
		for (QuestionEntityV2 q : qs)
		{
			goodqs.add(new QuestionEntityV2(q.qid, q.type, q.ctime, q.state, sanitize(q.content), sanitize(q.choose), q.answer, q.weight, q.creator, q.isdraf));
		}
		out.print(gson.toJson(goodqs));
	}

	private void at_question_count(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));
		long n = hr.countQuestion(uid, unit, type);
		out.print(gson.toJson(new Result<Long>(n)));
	}

	private void at_question_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Integer answer = Integer.parseInt(request.getParameter("answer"));
		Integer weight = Integer.parseInt(request.getParameter("weight"));
		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));
		boolean draf = Boolean.parseBoolean(request.getParameter("isdraf"));
		String content = request.getParameter("content");
		String choose = request.getParameter("choose");

		QuestionEntityV2 q = new QuestionEntityV2(0, type, null, 1, content, choose, answer, weight, uid, draf);

		long id = hr.createQuestion(uid, unit, q);
		out.print(gson.toJson(new Result<Long>(id)));
	}

	private void at_cvfield_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String title = request.getParameter("title");
		int state = Integer.parseInt(request.getParameter("state"));
		int unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);
		RequiredField field = new RequiredField(0, title, state, null, uid);
		long id = hr.createField(uid, unit, field);
		out.print(gson.toJson(new Result<Long>(id)));
	}

	private void at_cvfield_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		int unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);
		List<RequiredField> fs = hr.listField(uid, unit);

		List<RequiredField> goodfs = new ArrayList<RequiredField>();

		//không cho dùng javascript
		for (RequiredField f : fs)
		{
			goodfs.add(new RequiredField(f.id, sanitizer.sanitize(f.title), f.state, f.ctime, f.creator));
		}

		out.print(gson.toJson(goodfs));
	}

	private void at_cvfield_list2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		Long requestid = Long.parseLong(request.getParameter("request"));
		long uid = getUser(request);
		List<RequiredField> fs = hr.listField2(uid, requestid);
		List<RequiredField> goodfs = new ArrayList<RequiredField>();

		//không cho dùng javascript
		for (RequiredField f : fs)
		{
			goodfs.add(new RequiredField(f.id, sanitizer.sanitize(f.title), f.state, f.ctime, f.creator));
		}

		out.print(gson.toJson(goodfs));
	}

	private void at_cvfield_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));
		RequiredField field = hr.getRequiredField(uid, id);
		String title = request.getParameter("title");
		if (title == null)
		{
			title = field.title;
		}

		int state;
		try
		{
			state = Integer.parseInt(request.getParameter("state"));
		} catch (NumberFormatException e)
		{
			state = field.state;
		}

		RequiredField rfield = new RequiredField(0, title, state, null, uid);
		hr.editField(uid, id, rfield);
	}

	private void at_cvfield_delete(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long id = Long.parseLong(request.getParameter("id"));
		long uid = getUser(request);
		hr.deleteField(uid, id);
	}

	private void at_user_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String fullname = request.getParameter("fullname");
		String[] unit = request.getParameterValues("unit");
		long uid = getUser(request);

		UserEntityV2 user = new UserEntityV2(0, fullname, null, 0);
		long newuserid = hr.createUserUP(uid, username, password, user);
		for (String u : unit)
		{
			hr.getInUnit(uid, newuserid, Integer.parseInt(u));
		}
		out.print(gson.toJson(new Result<Long>(newuserid)));
	}

	private void at_user_resetpassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		Long userid = Long.parseLong(request.getParameter("id"));
		long uid = getUser(request);
		String newpassword = hr.resetPassword(uid, userid);

		out.print(gson.toJson(new Result<String>(newpassword)));
	}

	private void at_user_editUP(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long userid;
		try
		{
			userid = Long.parseLong(request.getParameter("uid"));
		} catch (NumberFormatException e)
		{
			userid = getUser(request);
		}
		//String cap = request.getParameter("cap");
		//Long capid = Long.parseLong (request.getParameter("capid"));
		//if(!captchapr.validate(capid, cap, getIp(request)))
		//{
		//	throw  new RuntimeException("Wrong captcha");
		//}

		//String username = request.getParameter("username");
		String password = request.getParameter("password");
		String oldpassword = request.getParameter("oldpassword");
		long uid = getUser(request);
		UserEntityV2 u = hr.getUser(uid, uid);

		hr.editUserUP(uid, userid, hr.matchUsername(uid, uid), password, oldpassword);
	}

	private void at_cv_comment(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long cvid = Long.parseLong(request.getParameter("id"));
		String com = request.getParameter("comment");
		long uid = getUser(request);

		CVCommentEntity comment = new CVCommentEntity(0, null, uid, 0, com);
		hr.comment(uid, cvid, comment);
	}

	private void at_cv_listcomment(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		Long cvid = Long.parseLong(request.getParameter("id"));
		List<Comment> cs = hr.listComment(uid, cvid);

		//không cho dùng javascript
		List<Comment> goodcs = new ArrayList<Comment>();
		for (Comment c : cs)
		{
			goodcs.add(new Comment(escapeHtml(c.author), c.ctime, escapeHtml(c.comment)));
		}
		out.print(gson.toJson(goodcs));
	}

	private String getIp(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Real-IP");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip))
		{

			int index = ip.indexOf(',');
			if (index != -1)
			{
				return ip.substring(0, index);
			} else
			{
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	private boolean isGoodMail(String hostName)
	{
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx;
		try
		{
			ictx = new InitialDirContext(env);
			Attributes attrs = ictx.getAttributes(hostName, new String[]{"MX"});
			Attribute attr = attrs.get("MX");
			if (attr == null)
			{
				return false;
			}
		} catch (NamingException ex)
		{
			Logger.getLogger(HRWeb.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

		return true;
	}

	private String uploadfile(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("No file uploaded");
			return null;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(maxMemSize);

		factory.setRepository(new File(request.getServletContext().getRealPath("") + "/file"));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxFileSize);
		List fileItems;
		try
		{
			fileItems = upload.parseRequest(request);
		} catch (FileUploadException ex)
		{
			throw new RuntimeException("canot upload file");
		}

		Iterator i = fileItems.iterator();
		if (i.hasNext() == false)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("No file uploaded");
			return null;
		}

		FileItem fi = (FileItem) i.next();
		setUp(request);

		//String filename = this.dconverter.convert(fi.getInputStream());
		//String filename = this.filesaver.saveHTML(htmldata);
		String filename = this.filesaver.saveFile(fi.get(), FilenameUtils.getExtension(fi.getName()));
		fi.delete();
		return filename;
	}

	private void at_cv_upload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long capid = Long.parseLong(request.getParameter("capid"));
		String cap = request.getParameter("cap");

		if (captchapr.validate(capid, cap, getIp(request)) == false)
		{
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			out.print("Wrong captcha");
			return;
		}

		String[] sfieldids = request.getParameterValues("field");
		String[] sinfos = request.getParameterValues("info");

		if (sfieldids == null || sinfos == null)
		{
			String field = request.getParameter("field");
			String info = request.getParameter("info");
			if (field == null || info == null || field.equals("") || info.equals(""))
			{
				sfieldids = new String[]{};
				sinfos = new String[]{};
			} else
			{
				sfieldids = new String[]{field};
				sinfos = new String[]{info};
			}
		}

		if (sfieldids.length != sinfos.length)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("Wrong parameter");
			return;
		}
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		Long requestid = Long.parseLong(request.getParameter("request"));

		List<InfoEntity> infos = new ArrayList<InfoEntity>();
		for (int i = 0; i < sinfos.length; i++)
		{
			infos.add(new InfoEntity(0, Long.parseLong(sfieldids[i]), sinfos[i]));
		}
		String type = request.getParameter("type");

		if ("file".equals(type))
		{
			String filename = uploadfile(request, response, out);

			CVEntityV2 cv = new CVEntityV2(0, null, 0, 0, "_" + filename, name, email, false, false, false, false, -1, -1, -1, -1, -1, -1, -1, false, false, false);

			try
			{
				hr.createCV(getUser(request), requestid, cv, "", infos);
			} catch (Exception e)
			{
				this.filesaver.deleteRel(filename);
				throw new RuntimeException(e.toString());
			}
		} else
		{
			String link = request.getParameter("link");
			if (link == null || link == "")
			{
				throw new RuntimeException("INVALID LINK");
			}
			CVEntityV2 cv = new CVEntityV2(0, null, 0, 0, link, name, email, false, false, false, false, -1, -1, -1, -1, -1, -1, -1, false, false, false);

			hr.createCV(getUser(request), requestid, cv, "", infos);
		}
	}

	private void at_cv_listfield(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		Long cvid;
		cvid = Long.parseLong(request.getParameter("id"));
		CVEntityV2 cv = hr.getCV(uid, cvid);
		List<CCVField> cvfs = hr.listCVField(uid, cvid);
		List<CCVField> goodfs = new ArrayList<CCVField>();
		goodfs.add(new CCVField("Tên đầy đủ", escapeHtml(cv.name)));
		goodfs.add(new CCVField("Email", escapeHtml(cv.email)));
		for (CCVField f : cvfs)
		{
			goodfs.add(new CCVField(escapeHtml(f.name), escapeHtml(f.data)));
		}

		out.print(gson.toJson(goodfs));
	}

	private void at_captcha_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		CaptchaHead ch = captchapr.create(getIp(request));
		out.print(gson.toJson(ch));
	}

	private void at_cv_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long cvid = Long.parseLong(request.getParameter("id"));
		CVEntityV2 oldcv = hr.getCV(uid, cvid);
		int pres;
		int arch;
		int sum;
		int total;
		int pote;
		int eng;
		Integer level;
		String filename;
		String name;
		String email;
		boolean cantesteng;
		boolean cantestiq;
		try
		{
			level = Integer.parseInt(request.getParameter("level"));
		} catch (NumberFormatException e)
		{
			level = oldcv.level;
		}

		filename = oldcv.filename;

		name = request.getParameter("name");
		if (name == null)
		{
			name = oldcv.name;
		}

		email = request.getParameter("email");
		if (email == null)
		{
			email = oldcv.email;
		}

		if (request.getParameter("cantesteng") == null)
		{
			cantesteng = oldcv.cantesteng;
		} else
		{
			try
			{
				cantesteng = Boolean.parseBoolean(request.getParameter("cantesteng"));
			} catch (Exception e)
			{
				cantesteng = oldcv.cantesteng;
			}
		}

		if (request.getParameter("cantestiq") == null)
		{
			cantestiq = oldcv.cantestiq;
		} else
		{
			try
			{
				cantestiq = Boolean.parseBoolean(request.getParameter("cantestiq"));
			} catch (Exception e)
			{
				cantestiq = oldcv.cantestiq;
			}
		}

		//	try
		//		{
		//		iq = Integer.parseInt(request.getParameter("iq"));
		//			if(iq < -1 || iq > 100)

		//		} catch (NumberFormatException e)
		//		{
		//			iq = oldcv.iq;
		//		}

		try
		{
			pres = Integer.parseInt(request.getParameter("pres"));
			if (pres < -1 || pres > 100)
			{
				throw new RuntimeException("invalid value for PRES");
			}
		} catch (NumberFormatException e)
		{

			pres = oldcv.pres;
		}

		try
		{
			arch = Integer.parseInt(request.getParameter("arch"));
			if (arch < -1 || arch > 100)
			{
				throw new RuntimeException("invalid value for ARCH");
			}
		} catch (NumberFormatException e)
		{
			arch = oldcv.arch;
		}

		try
		{
			sum = Integer.parseInt(request.getParameter("sum"));
			if (sum < -1 || sum > 100)
			{
				throw new RuntimeException("invalid value for SUM");
			}
		} catch (NumberFormatException e)
		{
			sum = oldcv.sum;
		}

		total = oldcv.total;

		try
		{
			pote = Integer.parseInt(request.getParameter("pote"));
			if (pote < -1 || pote > 100)
			{
				throw new RuntimeException("invalid value for POTE");
			}
		} catch (NumberFormatException e)
		{
			pote = oldcv.pote;
		}

		//	try
		//	{
		//		eng = Integer.parseInt(request.getParameter("eng"));
		//	} catch (NumberFormatException e)
		//	{
		//		eng = oldcv.eng;
		//	}

		CVEntityV2 cv = new CVEntityV2(cvid, null, 1, level, filename, name, email, cantesteng, cantestiq, oldcv.iqtested, oldcv.engtested, oldcv.iq, pres, arch, sum, total, pote, oldcv.eng, oldcv.receivedmail, oldcv.iqtestresultmail, oldcv.receivedmail);
		hr.editCV(uid, cvid, cv);
	}

	private void at_test_start(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		String code = request.getParameter("code");
		Long capid = Long.parseLong(request.getParameter("capid"));
		String cap = request.getParameter("cap");
		long uid = getUser(request);

		if (captchapr.validate(capid, cap, getIp(request)) == false)
		{
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			out.print("Wrong captcha");
			return;
		}
		CTest2 ctest = hr.startTest2(uid, code);
		CTest2 goddctest = ctest;
		if (uid != -1)
		{

			List<Question> qs = ctest.question;
			List<Question> goodqs = new ArrayList<Question>();
			for (Question q : qs)
			{
				goodqs.add(new Question(q.id, sanitize(q.title), sanitize(q.choose)));
			}
			CTest t = new CTest(ctest.answercode, goodqs, ctest.sec);
			goddctest = new CTest2(t, ctest.fullname, ctest.time, ctest.requestid, ctest.requesttitle);
		}

		out.print(gson.toJson(goddctest));
	}

	private void at_request_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));
		RequestEntityV2 oldrequest = hr.getRequest(uid, id);

		int state = oldrequest.state;
		try
		{
			state = Integer.parseInt(request.getParameter("state"));
		} catch (NumberFormatException e)
		{
		}

		String jobdesc = request.getParameter("jobdesc");
		if (jobdesc == null)
		{
			jobdesc = oldrequest.jobdesc;
		}

		String position = request.getParameter("position");
		if (position == null)
		{
			position = oldrequest.position;
		}

		String interest = request.getParameter("interest");
		if (interest == null)
		{
			interest = oldrequest.interest;
		}

		String requirement = request.getParameter("requirement");
		if (requirement == null)
		{
			requirement = oldrequest.requirement;
		}

		String title = request.getParameter("title");
		if (title == null)
		{
			title = oldrequest.title;
		}

		int quantity = oldrequest.quantity;
		try
		{
			quantity = Integer.parseInt(request.getParameter("quantity"));
		} catch (NumberFormatException e)
		{
		}

		RequestEntityV2 req = new RequestEntityV2(id, jobdesc, title, position, interest, requirement, uid, null, quantity, state);
		hr.editRequest(uid, id, req);
	}

	private void at_test_submit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String code = request.getParameter("code");
		String[] qs = request.getParameterValues("question[]");
		String[] cs = request.getParameterValues("choose[]");
		String[] as = request.getParameterValues("answer[]");

		if (qs == null || cs == null || as == null)
		{
			qs = new String[0];
			cs = new String[0];
			as = new String[0];
		}

		long uid = getUser(request);

		List<Long> iqs = new ArrayList<Long>();
		for (String q : qs)
		{
			iqs.add(Long.parseLong(q));
		}

		List<Integer> ias = new ArrayList<Integer>();
		for (String a : as)
		{
			ias.add(Integer.parseInt(a));
		}

		List<String> scs = new ArrayList<String>();
		for (String c : cs)
		{
			scs.add((c));
		}
		hr.submitTest(uid, code, iqs, scs, ias);
	}

	private void at_request_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		int p = Integer.parseInt(request.getParameter("p"));
		int ps = Integer.parseInt(request.getParameter("ps"));
		String[] s = request.getParameterValues("s[]");
		int unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);

		List<RequestEntityV2> requests = hr.listRequestByUnit(uid, unit, p, ps, s);

		List<CRequest> goodrequests = new ArrayList<CRequest>();

		for (RequestEntityV2 req : requests)
		{
			long pb = hr.getPrimaryBag(uid, req.id);
			long newcv = hr.countCV(uid, pb);
			long totalcv = hr.countCVRequest(uid, req.id);
			goodrequests.add(new CRequest(req.id, sanitizer.sanitize(req.jobdesc), sanitizer.sanitize(req.title), sanitizer.sanitize(req.position), sanitizer.sanitize(req.interest), sanitizer.sanitize(req.requirement), req.creator, req.ctime, req.quantity, req.state, totalcv, newcv));
		}
		out.print(gson.toJson(goodrequests));
	}

	private void at_request_count(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		int unit = Integer.parseInt(request.getParameter("unit"));
		long n = hr.countRequest(uid, unit);
		out.print(gson.toJson(new Result<Long>(n)));
	}

	private void at_user_login(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if (getUser(request) != -1)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print(gson.toJson(new Result<Integer>(-1)));
			return;
		}

		String authcode = hr.loginUP(username, password);

		if (authcode != null)
		{
			Cookie cookie = new Cookie("authcode", authcode);
			cookie.setPath("/");
			cookie.setMaxAge(this.cookieAge); //1 ngày
			response.addCookie(cookie);
			out.print(gson.toJson(new Result<Integer>(0x0)));
		} else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print(gson.toJson(new Result<Integer>(-1)));
		}

	}

	private void at_user_get(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		long userid;
		try
		{
			userid = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e)
		{
			userid = uid;
		}
		UserEntityV2 user = hr.getUser(uid, userid);
		List<UnitEntity> units = hr.getUnitbyUser(uid, userid);
		CUserEntity retuser = new CUserEntity(sanitizer.sanitize(user.fullname), user.id, units, user.ctime);
		out.print(gson.toJson(retuser));
	}

	private void at_file(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws ServletException, IOException
	{
		boolean secret;
		String p = request.getParameter("path");
		if (p == null || p.length() < 2)
		{
			throw new RuntimeException("invalid path");
		}
		final String path = p.substring(2);

		secret = p.startsWith("x_");

		RequestDispatcher rd = request.getRequestDispatcher("/file");
		HttpServletRequest wrapped;

		if (secret)
		{
			if (/*isLoggedIn(request)*/true == true)
			{
				wrapped = new HttpServletRequestWrapper(request)
				{
					@Override
					public String getPathInfo()
					{
						return "/private/" + path;
					}

					@Override
					public String getServletPath()
					{
						return "/file";
					}
				};

			} else
			{
				throw new AccessDeny("THIS RESOURCE IS PROTECTED");
			}
		} else
		{
			wrapped = new HttpServletRequestWrapper(request)
			{
				@Override
				public String getPathInfo()
				{
					return "/public/" + path;
				}

				@Override
				public String getServletPath()
				{
					return "/file";
				}
			};
		}
		rd.forward(wrapped, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		response.setStatus(HttpServletResponse.SC_OK);
		String path = request.getPathInfo();

		String contentType = "text/html;charset=UTF-8";
		response.setContentType(contentType);
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		//SERVICE THAT DOEN'T NEED TO BE LOGGED
		if (path.equals("/user/isloggedin"))
		{
			at_user_isloggedin(request, out);
			out.close();
			return;
		}

		Watch w = new Watch();
		try
		{
			if (path.equals("/play"))
			{
				at_play(request, response, out);
			}
			if (path.equals("/defaultparam"))
			{
				at_defaultparam(request, response, out);
			} else if (path.equals("/file"))
			{
				at_file(request, response, out);
			} else if (path.equals("/user/logout"))
			{
				at_user_logout(request, response, out);
			} else if (path.equals("/user/login"))
			{
				at_user_login(request, response, out);
			} else if (path.equals("/user/get"))
			{
				at_user_get(request, response, out);
			} else if (path.equals("/request/list"))
			{
				at_request_list(request, response, out);
			} else if (path.equals("/request/count"))
			{
				at_request_count(request, response, out);
			} else if (path.equals("/request/count2"))
			{
				at_request_count2(request, response, out);
			} else if (path.equals("/request/create"))
			{
				at_request_create(request, response, out);
			} else if (path.equals("/request/list2"))
			{
				at_request_list2(request, response, out);
			} else if (path.equals("/cv/count"))
			{
				at_cv_count(request, response, out);
			} else if (path.equals("/question/list"))
			{
				at_question_list(request, response, out);
			} else if (path.equals("/question/count"))
			{
				at_question_count(request, response, out);
			} else if (path.equals("/question/import"))
			{
				at_question_import(request, response, out);
			} else if (path.equals("/question/create"))
			{
				at_question_create(request, response, out);
			} else if (path.equals("/question/edit"))
			{
				at_question_edit(request, response, out);
			} else if (path.equals("/question/delete"))
			{
				at_question_delete(request, response, out);
			} else if (path.equals("/question/get"))
			{
				at_question_get(request, response, out);
			} else if (path.equals("/requiredfield/create"))
			{
				at_cvfield_create(request, response, out);
			} else if (path.equals("/requiredfield/list"))
			{
				at_cvfield_list(request, response, out);
			} else if (path.equals("/requiredfield/list2"))
			{
				at_cvfield_list2(request, response, out);
			} else if (path.equals("/request/get2"))
			{
				at_request_get2(request, response, out);
			} else if (path.equals("/requiredfield/edit"))
			{
				at_cvfield_edit(request, response, out);
			} else if (path.equals("/requiredfield/delete"))
			{
				at_cvfield_delete(request, response, out);
			} else if (path.equals("/emailform/create"))
			{
				at_emailform_create(request, response, out);
			} else if (path.equals("/emailform/edit"))
			{
				at_emailform_edit(request, response, out);
			} else if (path.equals("/emailform/list"))
			{
				at_emailform_list(request, response, out);
			} else if (path.equals("/user/create"))
			{
				at_user_create(request, response, out);
			} else if (path.equals("/user/resetpassword"))
			{
				at_user_resetpassword(request, response, out);
			} else if (path.equals("/user/editUP"))
			{
				at_user_editUP(request, response, out);
			} else if (path.equals("/cv/comment"))
			{
				at_cv_comment(request, response, out);
			} else if (path.equals("/cv/listcomment"))
			{
				at_cv_listcomment(request, response, out);
			} else if (path.equals("/test/start"))
			{
				at_test_start(request, response, out);
			} else if (path.equals("/cv/delete"))
			{
				at_cv_delete(request, response, out);
			} else if (path.equals("/cv/upload"))
			{
				at_cv_upload(request, response, out);
			} else if (path.equals("/question/export"))
			{
				at_question_export(request, response, out);
			} else if (path.equals("/cv/listfield"))
			{
				at_cv_listfield(request, response, out);
			} else if (path.equals("/captcha/create"))
			{
				at_captcha_create(request, response, out);
			} else if (path.equals("/cv/list"))
			{
				at_cv_list(request, response, out);
			} else if (path.equals("/cv/edit"))
			{
				at_cv_edit(request, response, out);
			} else if (path.equals("/request/edit"))
			{
				at_request_edit(request, response, out);
			} else if (path.equals("/test/submit"))
			{
				at_test_submit(request, response, out);
			} else if (path.equals("/bag/create"))
			{
				at_bag_create(request, response, out);
			} else if (path.equals("/bag/list"))
			{
				at_bag_list(request, response, out);
			} else if (path.equals("/bag/edit"))
			{
				at_bag_edit(request, response, out);
			} else if (path.equals("/bag/delete"))
			{
				at_bag_delete(request, response, out);
			} else if (path.equals("/cv/move"))
			{
				at_cv_move(request, response, out);
			} else if (path.equals("/cv/export"))
			{
				at_cv_export(request, response, out);
			} else if (path.equals("/cv/quickview"))
			{
				at_cv_quickview(request, response, out);
			} else if (path.equals("/config/get"))
			{
				at_config_get(request, response, out);
			} else if (path.equals("/requiredfield/get"))
			{
				at_requiredfiled_get(request, response, out);
			} else if (path.equals("/config/set"))
			{
				at_config_set(request, response, out);
			} else if (path.equals("/user/create"))
			{
				at_user_create(request, response, out);
			} else if (path.equals("/request/get"))
			{
				at_request_get(request, response, out);
			} else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				out.println("path: " + path);
			}
		}// catch (AccessDeny e)
		//{
	//		Logger.getLogger(HRWeb.class.getName()).log(Level.SEVERE, null, e);
//			if (response.getStatus() == HttpServletResponse.SC_OK)
//			{
//				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//			}
//
//			out.println("<p style='color:yellow;background-color:red'>ACCESS DENY</p>");
//			out.println("Message: " + e.());

//		}
	catch (IOException e)
		{
			if (response.getStatus() == HttpServletResponse.SC_OK)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			try
			{
				out.println("<p style='color:yellow;background-color:red'>INTERNAL ERROR</p>");
				out.println("Message: " + e.getMessage());

			} catch (Exception ex)
			{
				Logger.getLogger(HRWeb.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ServletException e)
		{
			if (response.getStatus() == HttpServletResponse.SC_OK)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			try
			{
				out.println("<p style='color:yellow;background-color:red'>INTERNAL ERROR</p>");
				out.println("Message: " + e.getMessage());

			} catch (Exception ex)
			{
				Logger.getLogger(HRWeb.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		out.close();

		w.stop();
		logger.log(request, response, w.starttime, w.delta, getUser(request));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		processRequest(request, response);
	}

	@Override
	public String getServletInfo()
	{
		return "Hệ thống quản lý CV, Testonline và nhân sự TVi";
	}// </editor-fold>

	private void at_emailform_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		String signature = request.getParameter("signature");
		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));
		long uid = getUser(request);

		setUp(request);
		EmailFormEntity ef = new EmailFormEntity(0, title, htmlconverter.convert(body), htmlconverter.convert(signature), type, uid, null, 0);
		hr.createEmailForm(uid, unit, null);
	}

	private void at_emailform_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));

		EmailFormEntity oldef = hr.getEmailForm(uid, id);

		String title = request.getParameter("title");
		if (title == null)
		{
			title = oldef.title;
		}
		String body = request.getParameter("body");
		if (body == null)
		{
			body = oldef.body;
		}
		String sign = request.getParameter("signature");
		if (sign == null)
		{
			sign = oldef.signature;
		}

		setUp(request);

		EmailFormEntity ef = new EmailFormEntity(oldef.id, title, htmlconverter.convert(body), htmlconverter.convert(sign), oldef.type, uid, null, oldef.state);
		hr.editEmailForm(uid, id, ef);
	}

	private String sanitize(String html)
	{
		return sanitizer.sanitize(html);
	}

	private void at_emailform_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		int unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);

		List<EmailFormEntity> ef = hr.listEmailForm(uid, unit);
		List<EmailFormEntity> goodef = new ArrayList<EmailFormEntity>();

		for (EmailFormEntity e : ef)
		{
			goodef.add(new EmailFormEntity(e.id, sanitize(e.title), (e.body), (e.signature), e.type, e.creator, e.ctime, e.state));
		}

		out.print(gson.toJson(goodef));

	}

	private void at_bag_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long requestid = Long.parseLong(request.getParameter("request"));
		long uid = getUser(request);

		List<CVBagEntity> bags = hr.listBag(uid, requestid);
		List<CBag> goodbags = new ArrayList<CBag>();
		for (CVBagEntity b : bags)
		{
			long c = hr.countCV(uid, b.id);
			goodbags.add(new CBag(b.id, sanitize(b.name), c));
		}
		out.print(gson.toJson(goodbags));
	}

	private void at_bag_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{

		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));

		CVBagEntity oldbag = hr.getBag(uid, id);

		String name = request.getParameter("name");
		if (name == null)
		{
			name = oldbag.name;
		}

		CVBagEntity bag = new CVBagEntity(name, oldbag.id, oldbag.state, null, uid);
		hr.editBag(uid, id, bag);
	}

	private void at_bag_delete(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long id = Long.parseLong(request.getParameter("id"));
		long uid = getUser(request);
		hr.deleteBag(uid, id);
	}

	private void at_cv_move(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		long cvid = Long.parseLong(request.getParameter("id"));
		long bagid = Long.parseLong(request.getParameter("bagid"));
		hr.moveCV(uid, bagid, cvid);
	}

	private void at_cv_export(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		String template = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("/") + "/template/exportcv.html")), "UTF-8");

		long uid = getUser(request);
		long cvid = Long.parseLong(request.getParameter("id"));
		String html = hr.exportCV(uid, cvid, template);
		out.print(html);
	}

	private void at_cv_quickview(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);
		long cvid = Long.parseLong(request.getParameter("id"));
		String url = hr.quickviewCV(uid, cvid);
		out.print(url);
	}

	private void at_config_get(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		String ref = request.getParameter("ref");
		Integer unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);
		String val = hr.getConfig(uid, unit, ref);
		out.print(gson.toJson(new Result<String>(sanitize(val))));

	}

	private void at_config_set(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String ref = request.getParameter("ref");
		String val = request.getParameter("val");

		Integer unit = Integer.parseInt(request.getParameter("unit"));
		long uid = getUser(request);
		hr.setConfig(uid, unit, ref, val);
	}

	private void at_question_edit(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{

		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));

		QuestionEntityV2 oldquestion = hr.getQuestion(uid, id);

		String content = request.getParameter("content");
		if (content == null)
		{
			content = oldquestion.content;
		}
		String choose = request.getParameter("choose");
		if (choose == null)
		{
			choose = oldquestion.choose;
		}
		int answer;
		try
		{
			answer = Integer.parseInt(request.getParameter("answer"));
		} catch (NumberFormatException e)
		{
			answer = oldquestion.answer;
		}

		int weight;
		try
		{
			weight = Integer.parseInt(request.getParameter("weight"));
		} catch (NumberFormatException e)
		{
			weight = oldquestion.weight;
		}

		boolean isdraf;
		if (request.getParameter("isdraf") == null)
		{
			isdraf = oldquestion.isdraf;
		} else
		{
			try
			{
				isdraf = Boolean.parseBoolean(request.getParameter("isdraf"));
			} catch (NumberFormatException e)
			{
				isdraf = oldquestion.isdraf;
			}
		}

		QuestionEntityV2 nq = new QuestionEntityV2(0, oldquestion.type, null, oldquestion.state, content, choose, answer, weight, uid, isdraf);
		hr.editQuestion(uid, id, nq);

	}

	private void at_question_delete(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long id = Long.parseLong(request.getParameter("id"));
		long uid = getUser(request);
		hr.deleteQuestion(uid, id);
	}

	private void at_cv_delete(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		Long id = Long.parseLong(request.getParameter("id"));
		long uid = getUser(request);
		hr.deleteCV(uid, id);
	}

	private void at_bag_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String name = request.getParameter("name");
		long requestid = Long.parseLong(request.getParameter("request"));
		long uid = getUser(request);
		CVBagEntity bag = new CVBagEntity(name, 0, 0, null, uid);
		long id = hr.createBag(uid, requestid, bag);
		out.print(gson.toJson(new Result<Long>(id)));
	}

	private void at_play(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		String warName = new File(getServletContext().getRealPath("/")).getName();
		out.println("warname : " + warName);
		out.println("path : " + new File(getServletContext().getRealPath("/")).getParent());
		out.println("abs path : " + new File(getServletContext().getRealPath("/")).getAbsolutePath());

	}

	private void at_cv_list(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long bagid = Long.parseLong(request.getParameter("bagid"));
		int p = Integer.parseInt(request.getParameter("p"));
		int ps = Integer.parseInt(request.getParameter("ps"));
		String[] s = request.getParameterValues("s[]");

		List<CVEntityV2> cvs = hr.listCV(uid, bagid, p, ps, s);

		List<CVEntityV2> goodcvs = new ArrayList<CVEntityV2>();

		//chống xss
		for (CVEntityV2 cv : cvs)
		{
			goodcvs.add(new CVEntityV2(cv.id, cv.ctime, cv.state, cv.level, cv.filename, sanitize(cv.name), sanitize(cv.email), cv.cantesteng, cv.cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, cv.eng, cv.receivedmail, cv.iqtestresultmail, cv.engtestresultmail));
		}
		out.print(gson.toJson(goodcvs));
	}

	private void at_question_get(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long qid = Long.parseLong(request.getParameter("id"));
		QuestionEntityV2 q = hr.getQuestion(uid, qid);

		QuestionEntityV2 gq = new QuestionEntityV2(q.qid, q.type, q.ctime, q.state, (q.content), (q.choose), q.answer, q.weight, q.creator, q.isdraf);
		out.print(gson.toJson(q));
	}

	private void at_request_get(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long rid = Long.parseLong(request.getParameter("id"));
		RequestEntityV2 req = hr.getRequest(uid, rid);

		long pb = hr.getPrimaryBag(uid, req.id);
		long newcv = hr.countCV(uid, pb);
		long totalcv = hr.countCVRequest(uid, req.id);
		CRequest grq = new CRequest(req.id, (req.jobdesc), (req.title), (req.position), (req.interest), (req.requirement), req.creator, req.ctime, req.quantity, req.state, totalcv, newcv);
		out.print(gson.toJson(grq));
	}

	private void at_requiredfiled_get(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long id = Long.parseLong(request.getParameter("id"));
		RequiredField rf = hr.getRequiredField(uid, id);

		out.print(gson.toJson(new RequiredField(rf.id, sanitize(rf.title), rf.state, rf.ctime, rf.creator)));
	}

	private void at_defaultparam(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		int unit = Integer.parseInt(request.getParameter("unit"));
		List<RequestEntityV2> r = hr.listRequestByUnit(uid, unit, 0, 1, null);
		if (r == null || r.isEmpty())
		{
			out.print(gson.toJson(new Result<Integer>(-1)));
		} else
		{
			out.print(gson.toJson(new Result<Long>(r.get(0).id)));
		}
	}

	private void at_request_get2(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long uid = getUser(request);
		Long rid = Long.parseLong(request.getParameter("id"));
		RequestEntityV2 req = hr.peekRequest(uid, rid);
		long totalcv = hr.countCVRequest(2, req.id);
		CRequest2 goodreq = new CRequest2(req.id, (req.jobdesc), sanitizer.sanitize(req.title), (req.position), (req.interest), (req.requirement), req.creator, req.ctime, req.quantity, req.state, totalcv, 0, hr.getUnitbyRequest(uid, req.id));
		out.print(gson.toJson(goodreq));
	}

	private void at_question_import(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException
	{
		long uid = getUser(request);

		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("No file uploaded");
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(maxMemSize);

		factory.setRepository(new File(request.getServletContext().getRealPath("") + "/file"));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxFileSize);
		List fileItems;
		try
		{
			fileItems = upload.parseRequest(request);
		} catch (FileUploadException ex)
		{
			throw new RuntimeException("canot upload file");
		}

		Iterator i = fileItems.iterator();
		if (i.hasNext() == false)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("No file uploaded");
			return;
		}

		FileItem fi = (FileItem) i.next();

		setUp(request);
		String htmldata = this.d2converter.convert(fi.getInputStream());

		Document doc = Jsoup.parse(htmldata);
		Elements newsHeadlines = doc.select("tr");
		for (Element e : newsHeadlines)
		{
			String content = e.child(1).child(0).html();
			int weight = 1;
			try
			{
				weight = Integer.parseInt(e.child(3).child(0).html());
			} catch (Exception ex)
			{
				weight = 1;
			}
			Element ce = e.child(2).child(0);//.replace("\n", "\\n");
			Elements brs = ce.select("br");
			String choose = "";
			if (brs.size() > 0)//splited by br
			{
				for (Element br : brs)
				{

					br.after("\\n");
					br.remove();
				}
				choose = ce.toString();
			} else
			{//split by b

				Elements ps = e.child(2).children();
				for (Element p : ps)
				{
					choose += p.html() + "\\n";
				}
				if (choose.length() > 1)
				{
					choose = choose.substring(0, choose.length() - 2);
				}
			}

			QuestionEntityV2 q = new QuestionEntityV2(0, type, null, 1, content, choose, 1, weight, uid, false);

			long id = hr.createQuestion(uid, unit, q);
			;
		}
	}

	private void at_question_export(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long caller = getUser(request);
		int unit = Integer.parseInt(request.getParameter("unit"));
		int type = Integer.parseInt(request.getParameter("type"));

		String htmldata = "<head><style>td{border:1px dashed whitesmoke} th{background-color: gray}</style></head><body><table><tr><th>STT</th><th>Cau hoi</th><th>Phuon an</th><th>Trong so</th></tr>";
		long n = hr.countQuestion(caller, unit, type);
		long stt = 0;
		n = n / 50 + 1;
		for (int i = 0; i < n; i++)
		{
			List<QuestionEntityV2> qs = hr.listQuestion(caller, unit, type, i, 50, null);
			for (QuestionEntityV2 q : qs)
			{
				stt++;
				String choose = q.choose.replace("\\n", "\n");
				htmldata += "<tr><td>" + stt + "</td><td>" + q.content + "</td><td>" + choose + "</td><td>" + q.weight + "</td></tr>";
			}
			htmldata += "</table></body>";
		}
		out.print(htmldata);
	}

	private void at_request_count2(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		long caller = getUser(request);
		String k = request.getParameter("k");
		if (k == null) k = "";
		long r = hr.countRequest(caller, k);
		out.print(gson.toJson(new Result<Long>(r)));
	}

	private void at_request_list2(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		int p = Integer.parseInt(request.getParameter("p"));
		int ps = Integer.parseInt(request.getParameter("ps"));
		String[] s = request.getParameterValues("s[]");
		String k = request.getParameter("k");
		long uid = getUser(request);

		List<RequestEntityV2> requests = hr.listRequest(uid, p, ps, k, s);

		List<CRequest2> goodrequests = new ArrayList<CRequest2>();

		for (RequestEntityV2 req : requests)
		{
			long totalcv = hr.countCVRequest(2, req.id);

			goodrequests.add(new CRequest2(req.id, sanitizer.sanitize(req.jobdesc), sanitizer.sanitize(req.title), sanitizer.sanitize(req.position), sanitizer.sanitize(req.interest), sanitizer.sanitize(req.requirement), req.creator, req.ctime, req.quantity, req.state, totalcv, 0, hr.getUnitbyRequest(uid, req.id)));
		}
		out.print(gson.toJson(goodrequests));
	}
}
