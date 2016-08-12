// thanhpk
package com.tvi.apply.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tvi.apply.business.*;
import com.tvi.apply.business.core.*;
import com.tvi.apply.data.CandidateDb;
import com.tvi.apply.data.EmailTemplateDb;
import com.tvi.apply.data.JobDb;
import com.tvi.apply.data.entity.EJobSetting;
import com.tvi.apply.type.*;
import com.tvi.apply.util.captcha.CaptchaHead;
import com.tvi.apply.util.captcha.CaptchaProvider;
import com.tvi.apply.util.captcha.ICaptchaProvider;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.MySqlDatabase;
import com.tvi.apply.util.excel.ExcelExporter;
import com.tvi.apply.util.file.IFileHelper;
import com.tvi.apply.util.file.PrivateFileHelper;
import com.tvi.apply.util.mail.EmailAccount;
import com.tvi.apply.util.mail.EmailHelper;
import com.tvi.apply.util.mail.IEmailHelper;
import com.tvi.apply.util.mail.gmailwapper.GmailWrapper;
import com.tvi.apply.util.rar.IRarPacker;
import com.tvi.apply.util.rar.RarPacker;
import com.tvi.apply.util.xml.Configter;
import com.tvi.apply.util.xml.IConfiger;
import com.tvi.apply.type.AccessDeny;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class HttpParam
{
	public HttpServletRequest request;
	public HttpServletResponse response;
	public PrintWriter out;
	public final static DateFormat m_ISO8601Local = new SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SS.MMMZ");
	public final static DateFormat m_UTCLocal = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	public Gson gson;

	public HttpParam(Gson gson, HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		this.request = request;
		this.response = response;
		this.out = out;
		this.gson = gson;
	}

	public boolean getBool(String name, boolean defvalue)
	{
		String value = request.getParameter(name);
		if (value == null) return defvalue;
		try
		{
			return Boolean.parseBoolean(value);
		} catch (Exception e) {return defvalue;}
	}

	public String getString(String name, String defvalue)
	{
		String value = request.getParameter(name);
		if (value == null) return defvalue;
		return value;
	}

	public Calendar getCalendar(String name, Calendar defvalue)
	{
		String value = request.getParameter(name);

		if (value == null) return defvalue;

		try
		{
			Date parse = m_UTCLocal.parse(value);
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(parse.getTime());
			return t;
		} catch (ParseException e)
		{
			return defvalue;
		}
	}

	public String[] getStrings(String name)
	{
		String[] value = request.getParameterValues(name);

		if (value == null)
		{

			String firstvalue = request.getParameter(name);
			if (firstvalue == null) return new String[0];
			else return new String[]{firstvalue};
		}
		return value;
	}

	public int getInt(String name, int defvalue)
	{
		String value = request.getParameter(name);
		if (value == null) return defvalue;
		try
		{
			return Integer.parseInt(value);
		} catch (Exception e) {return defvalue;}
	}

	public long getLong(String name, long defvalue)
	{
		String value = request.getParameter(name);
		if (value == null) return defvalue;
		try
		{
			return Long.parseLong(value);
		} catch (Exception e) {return defvalue;}
	}

	public void returnLong(long value)
	{
		out.print(gson.toJson(new Result<Long>(value)));
	}

	public void returnObj(Object o)
	{
		out.print(gson.toJson(o));
	}


}

public class Recruit extends HttpServlet
{

	private String domain = "http://tuyendung.tinhvan.com";
	private String filepath = "/rs-files";
	private int cookieAge = 86400; // one day
	private String cookieName = "authcode";
	private String servletName = "Recruit";
	private Gson gson = null;
	private final long maxFileSize = 25 * 1024L * 1024L;
	private final int maxMemSize = 20 * 1024;
	private String emailaddres;
	private ICaptchaProvider captchapr;
	private IRecruitSystem rs;
	private String basePath; //real path to rs-files
	PolicyFactory sanitizer;
	IFileHelper filemgr;
	IConfiger configer;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		IDatabase db;
		super.init(config);
		basePath = new File(getServletContext().getRealPath("/")).getParentFile().getAbsolutePath() + this.filepath + "/";
		basePath = basePath.replace('\\', '/');
		filemgr = new PrivateFileHelper(basePath, "file");
		configer = new Configter("config.xml", filemgr);

		//connect the database
		String dbhost = "127.0.0.1";//"127.0.0.1";//configer.load("database host");
		String dbuser = "root";//configer.load("database username");
		String dbpw = "";//configer.load("database password");
		String dbsc = "apply2";//configer.load("database schema");
		db = new MySqlDatabase();
		((MySqlDatabase) db).connect(dbhost, dbuser, dbpw, dbsc);

		this.emailaddres = "hr@tinhvan.com";//configer.load("email address")
		IRarPacker packer = new RarPacker();
		EmailAccount emailaccount = new EmailAccount(emailaddres, "Rh02)!!$", "mail.tinhvan.com", "mail.tinhvan.com", "POP3", "mail.tinhvan.com");
		IEmailHelper emailhpr = new EmailHelper(emailaccount, filemgr, packer);
		ICandidateMgt canmgr = new CandidateDb(db, "hr@tinhvan.com");
		IFieldMgt fieldmgr = new FieldMgr(db);
		IJobMgt jobmgr = new JobDb(db);
		IOrgMgt orgmgr = new OrgMgr(db);
		ITestMgt testmgr = new TestMgr(db);
		IAuth auth = new Auth(db);
		IUserMgt usermgr = new UserMgr(db, auth);
		IEmailTemplateMgt temmgr = new EmailTemplateDb(db);

		sanitizer = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS).and(Sanitizers.STYLES).and(Sanitizers.LINKS).and(Sanitizers.IMAGES);

		GsonBuilder builder = new GsonBuilder();
		builder.disableHtmlEscaping();
		gson = builder.create();

		captchapr = new CaptchaProvider(db);
		rs = new RecruitSystem(this.domain, this.emailaddres, db, new GmailWrapper(emailaccount), emailhpr, canmgr, fieldmgr, jobmgr, orgmgr, testmgr, usermgr, auth, temmgr, new EmailCrawler(db), packer, new ExcelExporter(filemgr), filemgr);
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
			if (cookie.getName().equals(this.cookieName) && rs.matchAuth(cookie.getValue()) != -1)
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
			if (cookie.getName().equals(this.cookieName) && (uid = rs.matchAuth(cookie.getValue())) != -1)
			{
				return uid;
			}
		}
		//không tìm thấy ai
		return -1;
	}

	private void at_user_logout(HttpParam http) throws IOException
	{
		long uid;
		if (getUser(http.request) != -1)
		{

			Cookie[] cookies = http.request.getCookies();

			for (Cookie cookie : cookies)
			{
				if (cookie.getName().equals(this.cookieName) && (uid = rs.matchAuth(cookie.getValue())) != -1)
				{

					//remove the cookie
					Cookie c = new Cookie("authcode", "");
					c.setMaxAge(0);
					c.setPath("/");
					http.response.addCookie(c);
					rs.logout(uid, cookie.getValue());
					break;
				}
			}

		} else
		{
			throw new RuntimeException("Chưa đăng nhập");
		}
	}

	private void at_file(HttpParam http) throws ServletException, IOException
	{
		boolean secret;
		String p = http.request.getParameter("path");
		if (p == null || p.length() < 2)
		{
			throw new RuntimeException("invalid path");
		}
		final String path = p.substring(2);

		secret = p.startsWith("x_");

		RequestDispatcher rd = http.request.getRequestDispatcher("/file");
		HttpServletRequest wrapped;

		if (secret)
		{

			wrapped = new HttpServletRequestWrapper(http.request)
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
			wrapped = new HttpServletRequestWrapper(http.request)
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
		rd.forward(wrapped, http.response);
	}

	private void at_user_login(HttpParam http) throws IOException
	{

		String username = http.getString("username", "");
		String password = http.getString("password", "");

		if (getUser(http.request) != -1)
		{
			http.response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			http.out.print(gson.toJson(new Result<Integer>(-1)));
			return;
		}

		String authcode = rs.loginUP(username, password);

		if (authcode != null)
		{
			Cookie cookie = new Cookie("authcode", authcode);
			cookie.setPath("/");
			cookie.setMaxAge(this.cookieAge);
			http.response.addCookie(cookie);
			http.out.print(gson.toJson(new Result<Integer>(0x0)));
		} else
		{
			http.response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			http.returnLong(-1);
		}
	}

	private void at_play(HttpParam http) throws IOException
	{
		String warName = new File(getServletContext().getRealPath("/")).getName();
		http.out.println("warname : " + warName);
		http.out.println("path : " + new File(getServletContext().getRealPath("/")).getParent());
		http.out.println("abs path : " + new File(getServletContext().getRealPath("/")).getAbsolutePath());
	}

	private void at_email_junk(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		rs.junkMail(userid, id);
	}

	private void at_email_send(HttpParam http)
	{
		long userid = getUser(http.request);
		TEmail email = new TEmail();
		email.body = http.getString("body", "<there are no body>");
		email.subject = http.getString("subject", "<no subject>");
		email.to = http.getString("to", "speedyapply@gmail.com");
		email.from = emailaddres;
		rs.sendMail(userid, email);
	}

	private void at_email_unjunk(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		rs.unjunkMail(userid, id);
	}

	private void at_email_clean(HttpParam http)
	{
		long userid = getUser(http.request);
		rs.cleanUnread(userid, userid);
	}

	private void at_job_create(HttpParam http)
	{
		long userid = getUser(http.request);
		TJob job = new TJob();

		job.category = http.getInt("category", 0);
		job.title = http.getString("title", "");
		job.attachment = http.getString("attachment", "");

		job.closedtime = http.getCalendar("closetime", Calendar.getInstance());
		job.contact = http.getString("contact", "");
		job.creator = userid;
		job.cvform = http.getString("cvform", "");
		job.description = http.getString("description", "");
		job.endtime = http.getCalendar("endtime", Calendar.getInstance());

		String[] fs = http.getStrings("field[]");

		TField fields[] = new TField[fs.length];
		for (int i = 0; i < fs.length; i++)
		{
			fields[i] = new TField();
			fields[i].id = Long.parseLong(fs[i]);
		}
		job.fields = fields;
		job.interest = http.getString("interest", "");
		job.jobstatus = http.getInt("jobstatus", 0);
		job.opentime = http.getCalendar("opentime", Calendar.getInstance());
		job.orgid = http.getLong("orgid", 0);
		job.picture = http.getString("picture", "");
		job.quantity = http.getInt("quantity", 0);
		job.salary = http.getString("salary", "");

		job.star = http.getBool("star", false);
		job.color = http.getInt("color", 13882323);

        EJobSetting ejs = new EJobSetting();

        ejs.sendrejectmail = false;
        ejs.lmtime = Calendar.getInstance();
        ejs.neng = 30;
        ejs.niq = 30;
        ejs.piq = 30;
        ejs.peng = 30;
        ejs.rcvmail = http.getString("rcvmail","");
        ejs.rejectmail = http.getString("rejectmail","");
        ejs.sendresmail = false;
        ejs.resmail =  http.getString("resmail","");
        ejs.testmail = http.getString("testmail","");
        long id = rs.createJob(userid, job, ejs);
		http.returnLong(id);
	}

	private void at_job_update(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		TJob job = rs.getJob(userid, id);

		job.category = http.getInt("category", job.category);
		job.attachment = http.getString("attachment", job.attachment);//uploadfile(http.request, job.title);

		job.closedtime = http.getCalendar("", job.closedtime);
		job.contact = http.getString("contact", job.contact);
		job.cvform = http.getString("cvform", job.cvform);
		job.description = http.getString("description", job.description);
		job.endtime = http.getCalendar("endtime", job.endtime);
		String[] fs = http.getStrings("field[]");
		TField fields[] = new TField[fs.length];
		for (int i = 0; i < fs.length; i++)
		{
			fields[i] = new TField();
			fields[i].id = Long.parseLong(fs[i]);
		}
		job.fields = fields;
		job.interest = http.getString("interest", job.interest);
		job.jobstatus = http.getInt("jobstatus", job.jobstatus);
		job.opentime = http.getCalendar("opentime", job.opentime);
		job.orgid = http.getLong("orgid", job.orgid);
		job.picture = http.getString("picture", job.picture);
		job.quantity = http.getInt("quantity", job.quantity);
		job.salary = http.getString("salary", job.salary);
		job.title = http.getString("title", job.title);
		job.star = http.getBool("star", job.star);
		job.color = http.getInt("color", job.color);
		job.state = http.getInt("state", job.state);


		TJobSetting js = rs.getJobSetting(userid, job.id);
		js.testmail = http.getString("testmail", js.testmail);
		js.resmail = http.getString("resmail", js.resmail);
		js.rvcmail = http.getString("rcvmail", js.rvcmail);
		js.rejectmail = http.getString("rejectmail", js.rejectmail);


		rs.setJobSetting(userid, job.id, js);
		rs.updateJob(userid, job);
	}

	private void at_job_get(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		TJob job = rs.getJob(userid, id);
		job.setting = rs.getJobSetting(userid, job.id);
		http.returnObj(job);
	}

	private void at_job_listbyorg(HttpParam http)
	{
		long userid = getUser(http.request);
		boolean archived = http.getBool("archived", false);
		long org = http.getLong("org", -1);
		String keyword = http.getString("keyword", "");
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		http.returnObj(rs.listJobByOrg(userid, archived, org, keyword, n, p));
	}

	private void at_job_countbyorg(HttpParam http)
	{
		long userid = getUser(http.request);
		boolean archived = http.getBool("archived", false);
		String keyword = http.getString("keyword", "");
		long c = rs.countJob(userid, archived, keyword);
		http.returnLong(c);
	}

	private void at_job_list(HttpParam http)
	{
		long userid = getUser(http.request);
		boolean archived = http.getBool("archived", false);
		String keyword = http.getString("keyword", "");
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		http.returnObj(rs.listJob(userid, archived, keyword, n, p));

	}

	private void at_job_count(HttpParam http)
	{
		long userid = getUser(http.request);
		boolean archived = http.getBool("archived", false);
		String keyword = http.getString("keyword", "");
		long c = rs.countJob(userid, archived, keyword);
		http.returnLong(c);
	}

	private void at_actionlog_list(HttpParam http)
	{
		long userid = getUser(http.request);
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		List<TActionLog> log = rs.listActionLog(userid, userid, n, p);
		http.returnObj(log);
	}

	private void at_actionlog_count(HttpParam http)
	{
		long userid = getUser(http.request);
		long c = rs.countActionLog(userid, userid);
		http.returnLong(c);
	}

	private void at_candidate_comment(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		String comment = http.getString("comment", "");
		long id = rs.comment(userid, userid, cid, comment);
		http.returnLong(id);
	}

	private void at_comment_edit(HttpParam http)
	{

		long userid = getUser(http.request);
		long commentid = http.getLong("id", -1);
		String comment = http.getString("comment", "");
		rs.editComment(userid, commentid, comment);
	}

	private void at_comment_delete(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		rs.deleteComment(userid, id);
	}

	private String[] fieldmap = new String[]{"tech", "level", "spec", "twe", "salary", "color", "cjt", "junk", "gender", "birth", "ctime", "state", "resumestatus", "iq", "eng"};
	private String[] operatormap = new String[]{"<", ">", "<=", ">=", "=", "like"};

	private void at_candidate_countbyjob(HttpParam http)
	{
		long userid = getUser(http.request);
		String[] jobs = http.getStrings("job[]");

		boolean archived = http.getBool("archived", false);
		long[] ljobs = new long[jobs.length];

		//convert filter
		String[] filter = http.getStrings("filter[]");
		String[] value = http.getStrings("value[]");
		String[] operator = http.getStrings("operator[]");
		for (int i = 0; i < filter.length; i++)
		{
			filter[i] = fieldmap[Integer.parseInt(filter[i])];
			operator[i] = operatormap[Integer.parseInt(operator[i])];
			if (filter[i].equals("ctime") || filter[i].equals("birth"))
			{
				try
				{
					value[i] = HttpParam.m_UTCLocal.parse(value[i]).getTime() + "";
				} catch (ParseException e)
				{
					value[i] = "0";
				}
			}
		}


		if ((jobs.length == 0) || jobs[0].equals(""))
		{
			ljobs = null;
		} else
		{
			for (int i = 0; i < jobs.length; i++)
				ljobs[i] = Long.parseLong(jobs[i]);

		}
		String keyword = http.getString("keyword", "");

		int status = http.getInt("status", -1);
		if (ljobs == null || ljobs.length == 0)
		{
			http.returnLong(0);
		} else
		{
			long c = rs.countCandidateByJob(userid, ljobs, archived, status, keyword, filter, value, operator);
			http.returnLong(c);
		}
	}

	private void at_email_markasunread(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		rs.markAsUnread(userid, userid, id);
	}

	private void at_candidate_listbyjob(HttpParam http)
	{
		long userid = getUser(http.request);
		String[] jobs = http.getStrings("job[]");
		boolean archived = http.getBool("archived", false);
		long[] ljobs = new long[jobs.length];
		if ((jobs.length == 0) || jobs[0].equals(""))
		{
			ljobs = null;
		} else
		{
			for (int i = 0; i < jobs.length; i++)
				ljobs[i] = Long.parseLong(jobs[i]);
		}
		String keyword = http.getString("keyword", "");
		int p = http.getInt("p", 0);
		int n = http.getInt("n", 20);
		int status = http.getInt("status", -1);

		//convert filter
		String[] filter = http.getStrings("filter[]");
		String[] value = http.getStrings("value[]");
		String[] operator = http.getStrings("operator[]");
		for (int i = 0; i < filter.length; i++)
		{
			filter[i] = fieldmap[Integer.parseInt(filter[i])];
			operator[i] = operatormap[Integer.parseInt(operator[i])];
		}

		//convert order
		String[] order = http.getStrings("order[]");
		for (int i = 0; i < order.length; i++)
		{
			if (order[i].charAt(0) == '-' || order[i].charAt(0) == '+')
			{
				order[i] = order[i].charAt(0) + fieldmap[Integer.parseInt(order[i].substring(1))];
			} else
			{
				order[i] = "+" + fieldmap[Integer.parseInt(order[i])];
			}
		}

		if (ljobs == null || ljobs.length == 0)
		{
			http.returnObj(new ArrayList<TCandidate>());
		} else
		{
			List<TCandidate> cans = rs.listCandidateByJob(userid, ljobs, archived, status, keyword, n, p, filter, value, operator, order);
			http.returnObj(cans);
		}
	}

	private String uploadfile(HttpServletRequest request, String defname)
	{
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) throw new RuntimeException("No file attached");

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
			throw new RuntimeException(ex);
		}

		Iterator i = fileItems.iterator();
		if (!i.hasNext()) throw new RuntimeException("No file attached");

		FileItem fi = (FileItem) i.next();

		String filename = filemgr.saveFile(fi.get(), "." + FilenameUtils.getExtension(fi.getName()), defname);
		fi.delete();
		return filename;
	}

	private void deleteFile(String filename)
	{
		File file = new File(filename);
		if(file.exists()) file.delete();
	}

	private void at_candidate_create(HttpParam http) throws ParseException {
		long capid = http.getLong("capid", -1); //Long.parseLong(http.request.getParameter("capid"));
		String cap = http.getString("cap", "");

		if (!captchapr.validate(capid, cap, getIp(http.request)))
		{
				throw new RuntimeException("wrong captcha (capid,cap) = (" +capid + "," + cap + ")" );
		}

		String[] sfieldids = http.getStrings("field");
		String[] sinfos = http.getStrings("info");

		if (sfieldids.length != sinfos.length)
		{
			throw new RuntimeException("invalid parameter");
		}
        Calendar cal = Calendar.getInstance();
		String name = http.getString("name", "");
		String email = http.getString("email", "");
		Long jobid = http.getLong("jobid", 0); // 0 =RESERVED
		String curemp = http.getString("exp", "");
		String phone = http.getString("phone", "");
		String spec = http.getString("spec", "");
		Calendar birth = http.getCalendar("birth", cal);
        Integer temp = http.getInt("gender", 1);
        Boolean gender;
        if(temp == 1) gender = true;
        else gender = false;

		TCandidate candidate = new TCandidate();
		candidate.email = email;
		candidate.jobid = jobid;
		candidate.fullname = name;
		candidate.gender = gender;
		candidate.curemp = curemp;
		candidate.phone = phone;
		candidate.spec = spec;
        candidate.birth = birth;

		TCandidateField infos[] = new TCandidateField[sinfos.length];

		for (int i = 0; i < sinfos.length; i++)
		{
			infos[i] = new TCandidateField();
			try
			{
				infos[i].fieldid = Integer.parseInt(sfieldids[i]);
			} catch (Exception e)
			{
				infos[i].fieldid = 0;
			}
			infos[i].content = sinfos[i];
			infos[i].lmtime = Calendar.getInstance();
			infos[i].author = 2;
		}

		candidate.fields = infos;
		String type = http.getString("type", "");

		if ("file".equals(type))
		{
			candidate.attachment = "_" + uploadfile(http.request, name);
		} else
		{
			candidate.attachment = http.getString("link", "");
		}

//		candidate.birth = Calendar.getInstance();
//		candidate.gender = true;
		candidate.tech = "chưa nhập";
		candidate.level = "chưa nhập";
//		candidate.spec = "chưa nhập";
		candidate.salary = "chưa nhập";
		candidate.cjt = "chưa nhập";
		candidate.twe = "chưa nhập";
//		candidate.curemp = "chưa nhập";
		candidate.quanlified = true;
//		candidate.phone = "chưa nhập";
		candidate.ctime = Calendar.getInstance();
		candidate.color = 0;
		candidate.eng = 0;
		candidate.iq = 0;
		candidate.englishteststate = 0;
		candidate.iqteststate = 0;
		candidate.junk = true;
		candidate.star = false;
		candidate.resumestatus = 0;
		candidate.state = 0;
		long canid = rs.createCandidate(2, candidate);
		TCandidate can = rs.viewCandidate(2, canid, null);
		http.returnObj(can);
	}

	private void at_candidate_update(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("id", -1);
		TCandidate candidate = rs.viewCandidate(2, cid, null);
		if(candidate.jobid == 3 && http.getLong("jobid", candidate.jobid) == 3){
			rs.deleteCandidate(candidate.id);
		}else{
			candidate.fullname = http.getString("fullname", candidate.fullname);
			candidate.email = http.getString("email", candidate.email);
			candidate.color = http.getInt("color", candidate.color);
			candidate.star = http.getBool("star", candidate.star);
			candidate.phone = http.getString("phone", candidate.phone);
			candidate.jobid = http.getLong("jobid", candidate.jobid);
			candidate.englishteststate = http.getInt("engstate", candidate.englishteststate);
			candidate.junk = http.getBool("junk", candidate.junk);
			candidate.iqteststate = http.getInt("iqstate", candidate.iqteststate);
			candidate.resumestatus = http.getInt("status", candidate.resumestatus);
			candidate.salary = http.getString("salary", candidate.salary);
			candidate.cjt = http.getString("cjt", candidate.cjt);
			candidate.twe = http.getString("twe", candidate.twe);
			candidate.curemp = http.getString("curemp", candidate.curemp);
			candidate.tech = http.getString("tech", candidate.tech);
			candidate.level = http.getString("level", candidate.level);
			candidate.spec = http.getString("spec", candidate.spec);
			candidate.quanlified = http.getBool("quanlified", candidate.quanlified);
			candidate.gender = http.getBool("gender", candidate.gender);
			candidate.birth = http.getCalendar("birth", candidate.birth);
			candidate.ctime = http.getCalendar("ctime", candidate.ctime);

			rs.updateCandidate(userid, candidate);
		}
	}

	private void at_info_add(HttpParam http)
	{
		long userid = getUser(http.request);

		long cid = http.getLong("cid", -1);
		long fid = http.getLong("fid", -1);
		String value = http.getString("value", "");
		long id = rs.addInfo(userid, cid, fid, value);
		http.returnLong(id);
	}

	private void at_info_delete(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		long fid = http.getLong("fid", -1);
		rs.deleteInfo(userid, cid, fid);
	}

	private void at_info_edit(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		long fid = http.getLong("fid", -1);
		String value = http.getString("value", "");
		rs.editInfo(userid, cid, fid, value);
	}

	private void at_candidate_view(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		TCandidate candidate = rs.viewCandidate(userid, cid, null);
		http.returnObj(candidate);
	}

	private void at_email_listbycandidate(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		int n = http.getInt("n", 30);
		int p = http.getInt("p", 0);
		List<TEmail> emails = rs.listCandidateMail(userid, cid, n, p);
		http.returnObj(emails);
	}

	private void at_org_list(HttpParam http)
	{
		long userid = getUser(http.request);
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		String keyword = http.getString("keyword", "");
		List<TOrg> orgs = rs.listOrg(userid, true, keyword, n, p);
		http.returnObj(orgs);
	}

	private void at_email_markasread(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		rs.markAsRead(userid, userid, id);
	}

	private void at_org_edit(HttpParam http)
	{
		long userid = getUser(http.request);
		TOrg org = new TOrg();
		org.id = http.getLong("id", -1);
		rs.getOrg(2, org.id);
		org.name = http.getString("name", org.name);
		org.picture = http.getString("picture", org.picture);
		org.description = http.getString("description", org.description);
		rs.editOrg(userid, org);
	}

	private void at_org_create(HttpParam http)
	{
		long userid = getUser(http.request);
		TOrg org = new TOrg();
		org.name = http.getString("name", org.name);
		org.picture = http.getString("picture", org.picture);
		org.description = http.getString("description", org.description);
		long id = rs.createOrg(userid, org);
		http.returnLong(id);
	}

	private void at_org_count(HttpParam http)
	{
		long userid = getUser(http.request);
		String keyword = http.getString("keyword", "");
		long c = rs.countOrg(userid, true, keyword);
		http.returnLong(c);
	}

	private void at_field_list(HttpParam http)
	{
		long userid = getUser(http.request);

		boolean archived = http.getBool("archived", false);
		String keyword = http.getString("keyword", "");
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		List<TField> fs = rs.listField(userid, archived, keyword, n, p);

		http.returnObj(fs);
	}

	private void at_field_count(HttpParam http)
	{
		long userid = getUser(http.request);
		boolean archived = http.getBool("archived", false);
		String keyword = http.getString("keyword", "");
		long c = rs.countField(userid, archived, keyword);
		http.returnLong(c);
	}

	private void at_email_view(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		TEmail email = rs.viewEmail(userid, id);
		http.returnObj(email);
	}

	private void at_field_create(HttpParam http)
	{
		long userid = getUser(http.request);

		TField f = new TField();
		f.creator = userid;
		f.name = http.getString("name", "");
		long id = rs.createField(userid, f);
		http.returnLong(id);
	}

	private void at_field_edit(HttpParam http)
	{
		long userid = getUser(http.request);

		long id = http.getLong("id", -1);

		TField f = rs.getField(2, id);
		f.name = http.getString("name", f.name);
		rs.editField(userid, f);

	}

	private void at_template_create(HttpParam http)
	{

		long userid = getUser(http.request);

		TEmailTemplate template = new TEmailTemplate();
		template.attachment = http.getString("attachment", "");
		template.body = http.getString("body", template.body);
		template.subject = http.getString("subject", template.subject);
		template.sign = http.getString("sign", template.sign);
		template.creator = userid;
		template.name = http.getString("name", "");

		long id = rs.createTemplate(userid, template);
		http.returnLong(id);
	}

	private void at_template_edit(HttpParam http)
	{
		long userid = getUser(http.request);
		TEmailTemplate template = new TEmailTemplate();
		template.attachment = http.getString("attachment", "");
		template.id = http.getLong("id", template.id);
		template.body = http.getString("body", "");
		template.subject = http.getString("subject", "");
		template.sign = http.getString("sign", "");
		template.name = http.getString("name", "");


		rs.updateTemplate(userid, template);
	}

	private void at_template_list(HttpParam http)
	{
		long userid = getUser(http.request);
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		String keyword = http.getString("keyword", "");

		List<TEmailTemplate> ts = rs.listTemplate(userid, keyword, n, p);
		http.returnObj(ts);
	}

	private void at_template_count(HttpParam http)
	{
		long userid = getUser(http.request);
		String keyword = http.getString("keyword", "");
		long c = rs.countTemplate(userid, keyword);
		http.returnLong(c);
	}

	private void at_test_start(HttpParam http)
	{
		String code = http.getString("code", "");
		Long capid = http.getLong("capid", 0);
		String cap = http.request.getParameter("cap");
		long uid = getUser(http.request);

		if (captchapr.validate(capid, cap, getIp(http.request)) == false)
		{
			throw new RuntimeException("sai captcha");
		}

		TTest2 ctest = rs.startTest(uid, code);
		TTest2 goddctest = ctest;
		if (uid != -1)
		{
			List<TQuestion> qs = ctest.question;
			List<TQuestion> goodqs = new ArrayList<TQuestion>();
			for (TQuestion q : qs)
			{
				goodqs.add(new TQuestion(q.id, q.title, q.choose));
			}
			TTest t = new TTest(ctest.answercode, goodqs, ctest.sec);
			goddctest = new TTest2(t, ctest.fullname, ctest.time, ctest.requestid, ctest.requesttitle);
		}

		http.returnObj(goddctest);
	}

	private void at_test_submit(HttpParam http)
	{
		String code = http.request.getParameter("code");
		String[] qs = http.request.getParameterValues("question[]");
		String[] cs = http.request.getParameterValues("choose[]");
		String[] as = http.request.getParameterValues("answer[]");

		if (qs == null || cs == null || as == null)
		{
			qs = new String[0];
			cs = new String[0];
			as = new String[0];
		}

		long uid = getUser(http.request);

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
		Collections.addAll(scs, cs);
		rs.submitTest(uid, code, iqs, scs, ias);
	}

	private void at_test_open(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		int type = http.getInt("type", 0);
		String code = rs.openTest(userid, cid, type);
		http.returnObj(code);
	}

	private void at_test_close(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		int type = http.getInt("type", 0);
		rs.closeTest(userid, cid, type);
	}

	private void at_email_listjunk(HttpParam http)
	{
		//		long userid = getUser(http.request);


		//rs.listJunkMail(userid, n, p);


	}

	private void at_email_listnew(HttpParam http)
	{
		long userid = getUser(http.request);
		String email = http.getString("email", "");
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		TEmail[] es = rs.listUnreadEmail(userid, userid, email, n, p);
		http.returnObj(es);
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

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		//setup request, response
		response.setStatus(HttpServletResponse.SC_OK);
		String contentType = "text/html;charset=UTF-8";
		response.setContentType(contentType);
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		HttpParam http = new HttpParam(gson, request, response, out);
		String path = request.getPathInfo();
		try
		{
			if (path.equals("/play")) at_play(http);
			else if (path.equals("/user/isloggedin")) at_user_isloggedin(request, out);
			else if (path.equals("/user/get")) at_user_get(http);
			else if (path.equals("/user/create")) at_user_create(http);
			else if (path.equals("/user/edit")) at_user_edit(http);
			else if (path.equals("/file")) at_file(http);
			else if (path.equals("/user/logout")) at_user_logout(http);
			else if (path.equals("/user/login")) at_user_login(http);
			else if (path.equals("/email/listnew")) at_email_listnew(http);
			else if (path.equals("/email/countnew")) at_email_countnew(http);
			else if (path.equals("/email/listjunk")) at_email_listjunk(http);
			else if (path.equals("/email/view")) at_email_view(http);
			else if (path.equals("/email/markasread")) at_email_markasread(http);
			else if (path.equals("/email/markasunread")) at_email_markasunread(http);
			else if (path.equals("/email/junk")) at_email_junk(http);
			else if (path.equals("/email/unjunk")) at_email_unjunk(http);
			else if (path.equals("/email/send")) at_email_send(http);
			else if (path.equals("/email/merge")) at_email_merge(http);
			else if (path.equals("/email/merge2")) at_email_merge2(http);
			else if (path.equals("/email/clean")) at_email_clean(http);
			else if (path.equals("/job/create")) at_job_create(http);
			else if (path.equals("/job/edit")) at_job_update(http);
			else if (path.equals("/job/get")) at_job_get(http);
			else if (path.equals("/job/listbyorg")) at_job_listbyorg(http);
			else if (path.equals("/job/countbyorg")) at_job_countbyorg(http);
			else if (path.equals("/job/list")) at_job_list(http);
			else if (path.equals("/job/count")) at_job_count(http);
			else if (path.equals("/actionlog/list")) at_actionlog_list(http);
			else if (path.equals("/actionlog/count")) at_actionlog_count(http);
			else if (path.equals("/candidate/comment")) at_candidate_comment(http);
			else if (path.equals("/comment/edit")) at_comment_edit(http);
			else if (path.equals("/comment/delete")) at_comment_delete(http);
			else if (path.equals("/candidate/count")) at_candidate_countbyjob(http);
			else if (path.equals("/candidate/list")) at_candidate_listbyjob(http);
			else if (path.equals("/candidate/create")) at_candidate_create(http);
			else if (path.equals("/candidate/edit")) at_candidate_update(http);
			else if (path.equals("/info/add")) at_info_add(http);
			else if (path.equals("/info/delete")) at_info_delete(http);
			else if (path.equals("/info/edit")) at_info_edit(http);
			else if (path.equals("/candidate/view")) at_candidate_view(http);
			else if (path.equals("/email/listbycandidate")) at_email_listbycandidate(http);
			else if (path.equals("/email/countbycandidate")) at_email_countbycandidate(http);
			else if (path.equals("/org/list")) at_org_list(http);
			else if (path.equals("/org/edit")) at_org_edit(http);
			else if (path.equals("/org/create")) at_org_create(http);
			else if (path.equals("/org/count")) at_org_count(http);
			else if (path.equals("/captcha/create")) at_captcha_create(request, response, out);
			else if (path.equals("/engquestion/list")) at_question_list(http, 2);
			else if (path.equals("/engquestion/count")) at_question_count(http, 2);
			else if (path.equals("/engquestion/create")) at_question_create(http, 2);
			else if (path.equals("/engquestion/edit")) at_question_edit(http);
			else if (path.equals("/engquestion/get")) at_question_get(http);
			else if (path.equals("/iqquestion/list")) at_question_list(http, 1);
			else if (path.equals("/iqquestion/count")) at_question_count(http, 1);
			else if (path.equals("/iqquestion/create")) at_question_create(http, 1);
			else if (path.equals("/iqquestion/edit")) at_question_edit(http);
			else if (path.equals("/iqquestion/get")) at_question_get(http);
			else if (path.equals("/field/list")) at_field_list(http);
			else if (path.equals("/field/count")) at_field_count(http);
			else if (path.equals("/field/create")) at_field_create(http);
			else if (path.equals("/field/edit")) at_field_edit(http);
			else if (path.equals("/field/get")) at_field_get(http);
			else if (path.equals("/template/create")) at_template_create(http);
			else if (path.equals("/template/edit")) at_template_edit(http);
			else if (path.equals("/template/list")) at_template_list(http);
			else if (path.equals("/template/count")) at_template_count(http);
			else if (path.equals("/test/start")) at_test_start(http);
			else if (path.equals("/test/submit")) at_test_submit(http);
			else if (path.equals("/test/open")) at_test_open(http);
			else if (path.equals("/test/close")) at_test_close(http);
			else if (path.equals("/candidate/send")) at_candidate_send(http);
			else if (path.equals("/candidate/export")) at_candidate_export(http);
			else if (path.equals("/candidate/downloadcv")) at_candidate_downloadcv(http);
			else if (path.equals("/subscriber/add")) at_subscriber_add(http);
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				out.println("path: " + path);
			}
		} catch (AccessDeny e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println("<p style='color:yellow;background-color:red'>ACCESS DENY</p>");
			out.println("Message: " + e.getMessage());
		} catch (Exception e)
		{
			if (1 == 1) throw new RuntimeException(e);
			if (response.getStatus() == HttpServletResponse.SC_OK)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			out.println("<p style='color:yellow;background-color:red'>INTERNAL ERROR</p>");
			out.println("Message: " + e.getMessage());

			Logger.getLogger(Recruit.class.getName()).log(Level.SEVERE, null, e);
			//throw new RuntimeException(e);
		}
		out.close();
	}

	private void at_email_countbycandidate(HttpParam http)
	{
		long userid = getUser(http.request);
		long cid = http.getLong("cid", -1);
		long count = rs.countCandidateMail(userid, cid);
		http.returnLong(count);
	}

	private void at_subscriber_add(HttpParam http)
	{
		try
		{
			String file = basePath + "/subscriber.txt";
			File f = new File(file);
			f.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.println(http.getString("email", "-"));
			out.close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void at_email_merge2(HttpParam http)
	{
		long userid = getUser(http.request);

		String[] listto = http.getStrings("to[]");


		//convert to long
		String subject = http.getString("subject", "DEFAULT SUBJECT");
		String body = http.getString("body", "default body");
		TEmail t = new TEmail();
		t.subject = subject;
		t.body = body;

		for (String s : listto)
		{
			t.to = s;
			rs.sendMail(userid, t);
		}
	}

	private void at_email_countnew(HttpParam http)
	{
		long userid = getUser(http.request);
		String email = http.getString("email", "");
		long es = rs.countUnreadEmail(userid, userid, email);
		http.returnObj(es);

	}

	private String getIp(HttpServletRequest request)
	{
		if (1 == 1) return "0:0:0:0:1";
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

	private void at_captcha_create(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		CaptchaHead ch = captchapr.create(getIp(request));
		out.print(gson.toJson(ch));
	}

	private void at_question_get(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		TQuestion q = rs.readQuestion(userid, id);

		http.returnObj(q);
	}

	private void at_question_create(HttpParam http, int type)
	{
		long userid = getUser(http.request);
		TQuestion q = new TQuestion();
		q.title = http.getString("title", "");
		q.choose = http.getString("choose", "");
		long id;
		if (type == 1)
		{

			id = rs.createIQQuestion(userid, q);
		} else
		{

			id = rs.createEngQuestion(userid, q);
		}
		http.returnLong(id);
	}

	private void at_question_count(HttpParam http, int type)
	{
		long userid = getUser(http.request);

		//int = http.getBool("n", false);
		String keyword = http.getString("keyword", "");
		long count;
		if (type == 1)
		{
			count = rs.countIQQuestion(userid, keyword);
		} else
		{
			count = rs.countEngQuestion(userid, keyword);
		}


		http.returnLong(count);
	}

	private void at_question_list(HttpParam http, int type)
	{
		long userid = getUser(http.request);

		//int = http.getBool("n", false);
		String keyword = http.getString("keyword", "");
		int n = http.getInt("n", 20);
		int p = http.getInt("p", 0);
		List<TQuestion> fs;

		if (type == 1)
		{
			fs = rs.listIQQuestion(userid, keyword, n, p);
		} else
		{
			fs = rs.listEngQuestion(userid, keyword, n, p);
		}

		http.returnObj(fs);
	}

	private void at_question_edit(HttpParam http)
	{
		long userid = getUser(http.request);
		long id = http.getLong("id", -1);
		TQuestion q = rs.readQuestion(userid, id);

		q.title = http.getString("title", q.title);
		q.choose = http.getString("choose", q.choose);
		rs.editQuestion(userid, q);
	}

	private void at_candidate_export(HttpParam http)
	{
		long userid = getUser(http.request);
		String[] strcans = http.getStrings("can[]");
		Long[] candidates = new Long[strcans.length];
		for (int i = 0; i < strcans.length; i++)
		{
			candidates[i] = Long.parseLong(strcans[i]);
		}

		Map<String, String> map = new HashMap<String, String>();


		map.put("id", "Mã số");
		map.put("ctime", "Ngày ứng tuyển");
		map.put("lmtime", "Ngày sửa cuối");
		map.put("state", "Trạng thái");
		map.put("junk", "Rác");
		map.put("fullname", "Họ tên");
		map.put("email", "Email");
		map.put("phone", "SĐT");
		map.put("jobid", "Mã tin tuyển dụng");
		map.put("color", "Trạng thái màu");
		map.put("star", "Ứng viên tiềm năng");
		map.put("attachment", "File đính kèm");
		map.put("englishteststate", "Thi Eng");
		map.put("iqteststate", "Thi IQ");
		map.put("eng", "Điểm Eng");
		map.put("iq", "Điểm IQ");
		map.put("resumestatus", "Trạng thái ứng tuyển");
		map.put("birth", "Ngày sinh");
		map.put("gender", "Giới tính");
		map.put("nmailsent", "Số mail đã gửi");
		map.put("nmailreceived", "Số mail nhận");
		map.put("logs", "Nhật ký");
		map.put("comments", "Chú thích");
		map.put("fields", "Trường thông tin");
		map.put("unread", "Đã đọc");
		map.put("salary", "Mức lương mong muốn");
		map.put("cjt", "Chuyên ngành");
		map.put("twe", "Số năm kinh nghiệm");
		map.put("curemp", "Vị trí làm việc hiện tại");
		map.put("quanlified", "Đã duyệt");
		map.put("attachments", "File đính kèm");
		map.put("spec", "Chuyên ngành");
		map.put("level", "Trình độ");
		map.put("tech", "Công nghệ");
		map.put("engtestlink", "Linh test ENG");
		map.put("iqtestlink", "Link test IQ");

		http.returnObj(rs.exportCandidate(userid, candidates, "candidate list" + Calendar.getInstance().get(Calendar.YEAR) + "_" + Calendar.getInstance().get(Calendar.MONTH) + "_" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH), map));
	}

	private void at_candidate_send(HttpParam http)
	{
		long userid = getUser(http.request);
		long eid = http.getLong("eid", -1);
		String email = http.getString("email", "speedyapply@gmail.com");
		rs.sendCandidate(userid, eid, email);
	}

	private void at_user_get(HttpParam http)
	{
		long userid = getUser(http.request);
		long uid = http.getLong("id", userid);
		TUser user = rs.getUser(userid, uid);
		user.password = "";
		http.returnObj(user);
	}

	private void at_user_edit(HttpParam http)
	{

		long userid = getUser(http.request);
		TUser user = new TUser();

		user.avatar = http.getString("avatar", "/ui/avatars/avatar.png");
		user.creator = userid;
		user.ctime = Calendar.getInstance();
		user.email = http.getString("email", "speedyapply@gmail.com");
		user.fullname = http.getString("fullname", "Không tên");
		user.initial = http.getString("inital", "TPK");
		user.lastpwchanged = user.ctime;
		user.lmtime = user.ctime;
		user.password = http.getString("password", "vietdeptrai");
		user.state = 0;
		user.status = 0;
		user.username = user.email;
		http.returnLong(rs.createUser(userid, user));
	}

	private void at_user_create(HttpParam http)
	{
		long userid = getUser(http.request);
		TUser user = new TUser();

		user.avatar = http.getString("avatar", "/ui/avatars/avatar.png");
		user.creator = userid;
		user.ctime = Calendar.getInstance();
		user.email = http.getString("email", "speedyapply@gmail.com");
		user.fullname = http.getString("fullname", "Không tên");
		user.initial = http.getString("inital", "TPK");
		user.lastpwchanged = user.ctime;
		user.lmtime = user.ctime;
		user.password = http.getString("password", "vietdeptrai");
		user.state = 0;
		user.status = 0;
		user.username = user.email;
		http.returnLong(rs.createUser(userid, user));
	}

	private void at_email_merge(HttpParam http)
	{
		long userid = getUser(http.request);

		String[] listto = http.getStrings("to[]");
		long[] idls = new long[listto.length];

		for (int i = 0; i < listto.length; i++)
		{
			idls[i] = Long.parseLong(listto[i]);
		}

		//convert to long
		String subject = http.getString("subject", "DEFAULT SUBJECT");
		String body = http.getString("body", "default body");
		TEmail t = new TEmail();
		t.subject = subject;
		t.body = body;
		rs.mailMerge(userid, idls, t);
	}

//	private void at_candidate_downloadcv(HttpParam http)
//	{
//		long userid = getUser(http.request);
//		String[] ids = http.getStrings("id[]");
//
//		long[] idls = new long[ids.length];
//
//		for (int i = 0; i < ids.length; i++)
//		{
//			idls[i] = Long.parseLong(ids[i]);
//		}
//		String pathInfo = http.request.getPathInfo();
//		http.returnObj(rs.packCv(userid, idls));
//	}

	private void at_candidate_downloadcv(HttpParam http)
	{
		long userid = getUser(http.request);
		String id = http.getString("id", "");
		long cid = Long.parseLong(id);
		http.returnObj(rs.getAttachment(userid, cid));
	}

	private void at_field_get(HttpParam http)
	{
		long userid = getUser(http.request);

		long id = http.getLong("id", -1);

		TField fs = rs.getField(userid, id);

		http.returnObj(fs);
	}
}
