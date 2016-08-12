package com.tvi.apply;

// CAUTION: DO NOT, AGAIN, DO NOT EXPOSE THIS COMPONENT TO A UNTRUSTED CLIENT, 
// THEY ARE ABLE TO HURT THE SYSTEM BY THIS COMPONENT'S INTERFACE
// -thanhpk-

import com.tvi.apply.commontype.*;
import com.tvi.common.*;
import com.tvi.common.entity.*;
import com.tvi.common.type.AccessDeny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HRSystem implements IHRSystem
{

	public final int IQTEST = 1;
	public final int ENGTEST = 2;
	public final int RECEIVEDMAIL = 1;
	public final int REJECTEDMAIL = 4;
	public final int TESTRESULTMAIL = 2;
	public final int TESTMAIL = 3;
	public final int REQUESTCLOSED = 1;
	public final int REQUESTOPEN = 0;
	private final IAuth auth;
	private final IRequiredFieldMgt fieldmgr;
	private final IRequestMgtV2 requestmgr;
	private final ITestMgtV2 testmgr;
	private final IUnitMgtV2 unitmgr;
	private final IEmailFormMgt emailformmgr;
	private final IMailSender mailsdr;
	private final String path;

	public HRSystem(String path, IAuth auth, IRequiredFieldMgt fieldmgr, IRequestMgtV2 requestmgr, ITestMgtV2 testmgr, IUnitMgtV2 unitmgr, IEmailFormMgt emailformmgr, IMailSender mailsdr)
	{
		this.path = path;
		this.auth = auth;
		this.fieldmgr = fieldmgr;
		this.requestmgr = requestmgr;
		this.testmgr = testmgr;
		this.unitmgr = unitmgr;
		this.emailformmgr = emailformmgr;
		this.mailsdr = mailsdr;
	}

	@Override
	public String loginUP(String username, String password)
	{
		long uid;
		if (-1 != (uid = unitmgr.matchUP(username, password)))
		{
			return auth.login(uid);
		}
		return null;
	}

	@Override
	public long matchAuth(String authcode)
	{
		return auth.matchAuth(authcode);
	}

	@Override
	public void logout(long caller, long uid, String authcode)
	{
		if (caller != uid)
		{
			unitmgr.access(uid, "logout other");
		}
		auth.logout(authcode);
	}

	@Override
	public String resetPassword(long caller, long userid)
	{
		unitmgr.access(caller, "reset password");
		return unitmgr.resetPassword(userid);
	}

	private boolean isInUnit(long user, int unit)
	{
		List<UnitEntity> units = unitmgr.listUnitbyUser(user);
		for (UnitEntity u : units)
		{
			if (unit == u.id)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void deleteCV(long caller, long id)
	{
		int unit = requestmgr.getCVsUnit(id);
		access(caller, unit, "delete", "cv");

		CVEntityV2 cv = requestmgr.getCV(id);
		RequestEntityV2 request = requestmgr.getRequest(requestmgr.getRequestFromCV(id));
		if (unitmgr.getConfig(unit, "send reject mail").equals("true"))
		{
			EmailFormEntity ef = emailformmgr.getForm(unit, REJECTEDMAIL);
			EmailEntity email = buildRejectMail(ef, request, cv);
			mailsdr.send(cv.email, email);
		}
		requestmgr.deleteCV(id);
	}

	@Override
	public void comment(long caller, long cvid, CVCommentEntity comment)
	{
		int cvunit = requestmgr.getCVsUnit(cvid);
		if (isInUnit(caller, cvunit))
		{
			unitmgr.access(caller, "comment cv");
		} else
		{
			unitmgr.access(caller, "comment other cv");
		}

		requestmgr.commentCV(cvid, comment);
	}

	@Override
	public List<Comment> listComment(long caller, long cvid)
	{
		int cvunit = requestmgr.getCVsUnit(cvid);
		if (isInUnit(caller, cvunit))
		{
			unitmgr.access(caller, "list cv comment");
		} else
		{
			unitmgr.access(caller, "list other cv comment");
		}

		//chuyển đổi từ List<CVCommentEntity> sang List<Comment>
		List<CVCommentEntity> cl = requestmgr.listCVComment(cvid);
		List<Comment> cl2 = new ArrayList<Comment>();
		for (int i = 0; i < cl.size(); i++)
		{
			CVCommentEntity cvcomment = cl.get(i);
			Comment comment = new Comment(unitmgr.getUser(cvcomment.author).fullname, cvcomment.ctime, cvcomment.comment);
			cl2.add(comment);
		}

		return cl2;
	}

	@Override
	public void deleteField(long caller, long id)
	{
		int funit = fieldmgr.getRequiredFieldsUnit(id);
		if (isInUnit(caller, funit))
		{
			unitmgr.access(caller, "delete field");
		} else
		{
			unitmgr.access(caller, "delete other field");
		}

		fieldmgr.deleteField(id);
	}

	public CTest2 startTest2(long caller, String code)
	{
		long testid = testmgr.matchTest(code);
		if (testid == -1)
		{
			throw new RuntimeException("Test is aldready taken or is locked for some reason");
		}
		long cvid = requestmgr.getCVFromTest(testid);
		CVEntityV2 cv = requestmgr.getCV(cvid);
		CTest test = startTest(caller, code);

		// không cần kiểm tra trạng thái của request nữa
		// đã kiểm tra trong hàm startTest
		long rid = requestmgr.getRequestFromCV(cvid);
		RequestEntityV2 r = requestmgr.getRequest(rid);

		CTest2 test2 = new CTest2(test, cv.name, cv.ctime, r.id, r.title);
		return test2;
	}

	@Override
	public CTest startTest(long caller, String code)
	{
		//Kiểm tra xem test có tồn tại hay ko
		long testid = testmgr.matchTest(code);
		if (testid == -1)
		{
			throw new RuntimeException("Test is aldready taken or is locked for some reason");
		}
		TestEntity test = testmgr.getTest(testid);
		long cvid = requestmgr.getCVFromTest(testid);

		if (cvid == -1)
		{
			throw new RuntimeException("Test is locked");
		}

		CVEntityV2 cv = requestmgr.getCV(cvid);
		long reqid = requestmgr.getRequestFromCV(cvid);
		RequestEntityV2 req = requestmgr.getRequest(reqid);

		if (req.state != 0)
		{
			throw new RuntimeException("Request closed");
		}

		CVEntityV2 newcv;
		CTest ret;
		long answerid;
		if (test.type == IQTEST)
		{
		//	if (cv.cantestiq == false)
		//	{
		//		throw new AccessDeny("IQ test has already taken or lock");
		//	}
			testmgr.deleteTestCode(code);
			answerid = testmgr.startTest(testid);
			requestmgr.addCVAnswer(cvid, answerid);
			newcv = new CVEntityV2(cvid, null, cv.state, cv.level, cv.filename, cv.name, cv.email, cv.cantesteng, false, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, cv.eng, cv.receivedmail, cv.iqtestresultmail, cv.engtestresultmail);
		} else
		{
			//if (cv.cantesteng == false)
			//{
		//		throw new AccessDeny("English test has already taken or lock");
		//	}
			testmgr.deleteTestCode(code);
			answerid = testmgr.startTest(testid);
			requestmgr.addCVAnswer(cvid, answerid);
			newcv = new CVEntityV2(cvid, null, cv.state, cv.level, cv.filename, cv.name, cv.email, false, cv.cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, cv.eng, cv.receivedmail, cv.iqtestresultmail, cv.engtestresultmail);
		}

		List<QuestionEntityV2> qes = testmgr.getTestQuestion(testid);
		List<Question> qs = new ArrayList<Question>(qes.size());
		int sec = 0;
		for (int i = 0; i < qes.size(); i++)
		{
			QuestionEntityV2 qe = qes.get(i);
			sec += 60 * qe.weight;
			qs.add(new Question(qe.qid, qe.content, mixQ(qe.choose)));
		}

		ret = new CTest(testmgr.matchAnswerCode(answerid), qs, sec);

		requestmgr.editCV(cvid, newcv);

		return ret;
	}

	private String mixQ(String choose)
	{
		if (!choose.contains("\\n"))
		{
			return choose;
		}
		List<String> cs = Arrays.asList(choose.split("\\\\n"));
		Collections.shuffle(cs);
		String ret = "";
		for (int i = 0; i < cs.size() - 1; i++)
		{
			ret += cs.get(i) + "\\n";
		}
		ret += cs.get(cs.size() - 1);
		return ret;
	}

	@Override
	public long createUserUP(long caller, String username, String password, UserEntityV2 user)
	{
		unitmgr.access(caller, "create user up");
		return unitmgr.createUP(username, password, user);
	}

	@Override
	public long createCV(long caller, long requestid, CVEntityV2 cv, String cvfile, List<InfoEntity> infos)
	{
		unitmgr.access(caller, "create cv");
		RequestEntityV2 request = requestmgr.getRequest(requestid);
		if (request.state == REQUESTCLOSED)
		{
			throw new RuntimeException("This request is locked");
		}

		cv = new CVEntityV2(0, null, cv.state, cv.level, cv.filename, cv.name, cv.email, false, false, false, false, -1, -1, -1, -1, -1, -1, -1, false, false, false);

		//this is why edit or remove should have id splited
		long cvid = requestmgr.createCV(requestmgr.getPrimaryBag(requestid), cv);
		int unit = requestmgr.getRequestsUnit(requestid);

		try
		{
			requestmgr.storeCVFile(cvid, cvfile);
			if (unitmgr.getConfig(unit, "auto send received mail").equals("true"))
			{
				EmailFormEntity ef = emailformmgr.getForm(unit, RECEIVEDMAIL);
				EmailEntity email = buildReceivedMail(ef, request, cv);
				mailsdr.send(cv.email, email);
				cv = new CVEntityV2(0, null, cv.state, cv.level, cv.filename, cv.name, cv.email, false, false, false, false, -1, -1, -1, -1, -1, -1, -1, true, false, false);
				requestmgr.editCV(cvid, cv);
			}

			if (unitmgr.getConfig(unit, "auto open test").equals("true"))
			{
				cv = new CVEntityV2(0, null, cv.state, cv.level, cv.filename, cv.name, cv.email, true, true, false, false, -1, -1, -1, -1, -1, -1, -1, cv.receivedmail, false, false);
				editCV(2, cvid, cv);
			}

			if (infos != null)
			{
				for (InfoEntity ie : infos)
				{
					requestmgr.addCVInfo(cvid, ie);
				}
			}
			return cvid;
		} catch (Exception e)
		{
			requestmgr.deleteCV(cvid);
			throw new RuntimeException(e.toString());
		}
	}

	@Override
	public void editUserUP(long caller, long uid, String username, String password, String oldpassword)
	{
		unitmgr.access(caller, "edit up");
		if (caller != uid)
		{
			unitmgr.access(caller, "edit other up");
		}

		if (uid != unitmgr.matchUP(username, oldpassword))
		{
			throw new RuntimeException("Wrong password");
		}
		;

		unitmgr.editUP(uid, username, password);
	}

	@Override
	public long matchTestCode(long caller, String code)
	{
		return testmgr.matchTest(code);
	}

	@Override
	public List<CVEntityV2> listCV(long caller, long bagid, int p, int ps, String[] s)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, "list cv");

		} else
		{
			unitmgr.access(caller, "list other cv");
		}

		return requestmgr.listCV(bagid, p, ps, s);
	}

	@Override
	public List<CVBagEntity> listBag(long caller, long requestid)
	{
		int unit = requestmgr.getRequestsUnit(requestid);
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, "list bag");

		} else
		{
			unitmgr.access(caller, "list other bag");
		}

		return requestmgr.listBag(requestid);
	}

	@Override
	public long countRequest(long caller, int unit)
	{
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, "count request");

		} else
		{
			unitmgr.access(caller, "count other request");
		}

		return requestmgr.countRequestByUnit(unit);
	}

	@Override
	public long countCV(long caller, long bagid)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, "count cv");

		} else
		{
			unitmgr.access(caller, "count other cv");
		}

		return requestmgr.countCV(bagid);
	}

	@Override
	public List<RequiredField> listField(long caller, int unit)
	{
		access(caller, unit, "list", "field");
		return fieldmgr.listField(unit);
	}

	@Override
	public List<RequiredField> listField2(long caller, long request)
	{
		int unit = requestmgr.getRequestsUnit(request);
		List<RequiredField> fs = fieldmgr.listField(unit);
		List<RequiredField> goodfs = new ArrayList<RequiredField>();
		for (RequiredField f : fs)
		{
			if (f.state == 0)
			{
				goodfs.add(f);
			}
		}
		return goodfs;
	}

	@Override
	public long createField(long caller, int unit, RequiredField field)
	{
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, "create field");

		} else
		{
			unitmgr.access(caller, "create other field");
		}

		return fieldmgr.createField(unit, field);
	}

	@Override
	public void editField(long caller, long id, RequiredField field)
	{
		if (isInUnit(caller, fieldmgr.getRequiredFieldsUnit(id)))
		{
			unitmgr.access(caller, "edit field");

		} else
		{
			unitmgr.access(caller, "edit other field");
		}

		fieldmgr.editField(id, field);
	}

	@Override
	public List<QuestionEntityV2> listQuestion(long caller, int unit, int type, int p, int ps, String[] s)
	{
		access(caller, unit, "list", "question");
		return testmgr.listQuestion(unit, type, p, ps, s);
	}

	@Override
	public long countQuestion(long caller, int unit, int type)
	{
		access(caller, unit, "count", "question");
		return testmgr.countQuestion(unit, type);
	}

	@Override
	public long createQuestion(long caller, int unit, QuestionEntityV2 question)
	{
		access(caller, unit, "create", "question");
		return testmgr.addQuestion(unit, question);
	}

	private String[] getFieldMap()
	{
		return new String[]{"", "#name", "#sdate", "#reqname", "#reqlink", "#point", "#test", "#testdate", "#testlink"};
	}

	private EmailEntity buildTestResultMail(EmailFormEntity ef, RequestEntityV2 req, CVEntityV2 cv, TestEntity test, TestAnswerEntity testanswer)
	{
		String[] fieldmap = getFieldMap();
		String[] datamap = fieldmap.clone();
		datamap[0] = cv.name;
		if (testanswer.ctime != null)
		{
			datamap[1] = cv.ctime.toString();
		}
		datamap[2] = req.title;
		datamap[3] = "#uploadcv" + req.id;
		datamap[4] = testanswer.point + "";
		if (test.type == 1)
		{
			datamap[5] = "IQ";
		} else if (test.type == 2)
		{
			datamap[5] = "Tiếng anh";
		} else
		{
			datamap[5] = "Chuyên môn";
		}

		if (testanswer.stime != null)
		{
			datamap[6] = testanswer.stime.toString();
		}
		datamap[7] = "";
		return buildMail(ef, fieldmap, datamap);
	}

	private EmailEntity buildReceivedMail(EmailFormEntity ef, RequestEntityV2 req, CVEntityV2 cv)
	{
		String[] fieldmap = getFieldMap();
		String[] datamap = fieldmap.clone();
		datamap[0] = cv.name;
		if (cv.ctime != null)
		{
			datamap[1] = cv.ctime.toString();
		}
		datamap[2] = req.title;
		datamap[3] = this.path + "#uploadcv/" + req.id;

		return buildMail(ef, fieldmap, datamap);
	}

	private EmailEntity buildMail(EmailFormEntity ef, String[] fieldmap, String[] datamap)
	{
		int i;
		int n = fieldmap.length;
		String title = ef.title;
		String body = ef.body;
		String signature = ef.signature;

		for (i = 1; i < n; i++)
		{
			title = title.replaceAll(fieldmap[i] + "\\b", "%" + i + "\\$s");
			body = body.replaceAll(fieldmap[i] + "\\b", "%" + i + "\\$s");
			signature = signature.replaceAll(fieldmap[i] + "\\b", "%" + i + "\\$s");
		}

		title = String.format(title, (Object[]) datamap);
		body = String.format(body, (Object[]) datamap);
		signature = String.format(signature, (Object[]) datamap);

		EmailEntity email = new EmailEntity(title, body, signature);
		return email;
	}

	private EmailEntity buildTestMail(EmailFormEntity ef, RequestEntityV2 req, CVEntityV2 cv, TestEntity test, String testcode)
	{
		String[] fieldmap = getFieldMap();
		String[] datamap = fieldmap.clone();
		datamap[0] = cv.name;
		datamap[1] = cv.ctime.toString();
		datamap[2] = req.title;
		datamap[3] = this.path + "#uploadcv/" + req.id;
		if (test.type == 1)
		{
			datamap[5] = "IQ";
		} else if (test.type == 2)
		{
			datamap[5] = "Tiếng anh";
		} else
		{
			datamap[5] = "Chuyên môn";
		}

		datamap[7] = this.path + "#test/" + testcode;

		return buildMail(ef, fieldmap, datamap);
	}

	private EmailEntity buildRejectMail(EmailFormEntity ef, RequestEntityV2 req, CVEntityV2 cv)
	{
		String[] fieldmap = getFieldMap();
		String[] datamap = fieldmap.clone();
		datamap[0] = cv.name;
		datamap[1] = cv.ctime.toString();
		datamap[2] = req.title;
		datamap[3] = this.path + "#uploadcv/" + req.id;
		return buildMail(ef, fieldmap, datamap);
	}

	private int f(String s1, String s2, int k)
	{
		int pos = 0;
		k--;
		String[] s1Parts = s1.split("\\\\n");
		String[] s2Parts = s2.split("\\\\n");
		if (k >= s2Parts.length)
		{
			k = s2Parts.length - 1;
		}
		try
		{
			for (int i = 0; i < s1Parts.length; i++)
			{
				if (s1Parts[i].equals(s2Parts[k]))
				{
					pos = i;
				}
			}
		} catch (Exception e)
		{
			pos = 1;
			Logger.getLogger(HRSystem.class.getName()).log(Level.SEVERE, null, e);
		}
		return pos+1;
	}

	@Override
	public void submitTest(long caller, String answercode, List<Long> qs, List<String> cs, List<Integer> answers)
	{
		long answerid = testmgr.matchAnswer(answercode);
		if (answerid == -1)
		{
			throw new RuntimeException("Answer code expired or doesn't exist");
		}

		long cvid = requestmgr.getCVFromAnswer(answerid);
		int unit = requestmgr.getCVsUnit(cvid);
		CVEntityV2 cv = requestmgr.getCV(cvid);

		List<Integer> realanswer = new ArrayList<Integer>();

		for (int i = 0; i < qs.size(); i++)
		{
			QuestionEntityV2 question = testmgr.getQuestion(qs.get(i));
			realanswer.add(this.f(question.choose, cs.get(i), answers.get(i)));
		}

		testmgr.submitAnswer(answerid, qs, realanswer);
		testmgr.deleteAnswerCode(answercode);

		long testid = testmgr.getTestbyAnswer(answerid);
		TestEntity test = testmgr.getTest(testid);
		TestAnswerEntity testanswer = testmgr.getAnswer(answerid);
		RequestEntityV2 request = requestmgr.getRequest(requestmgr.getRequestFromCV(cvid));

		EmailFormEntity form = emailformmgr.getForm(unit, 2);
		EmailEntity email = buildTestResultMail(form, request, cv, test, testanswer);
		mailsdr.send(cv.email, email);

		CVEntityV2 newcv;
		if (test.type == 1)
		{
			newcv = new CVEntityV2(cvid, null, cv.state, cv.level, cv.filename, cv.name, cv.email, cv.cantesteng, false, cv.iqtested, cv.engtested, testanswer.point, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, cv.eng, cv.receivedmail, true, cv.engtestresultmail);
		} else
		{
			newcv = new CVEntityV2(cvid, null, cv.state, cv.level, cv.filename, cv.name, cv.email, false, cv.cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, testanswer.point, cv.receivedmail, cv.iqtestresultmail, true);
		}

		requestmgr.editCV(cvid, newcv);
	}

	@Override
	public void editCV(long caller, long id, CVEntityV2 cv)
	{
		int unit = requestmgr.getCVsUnit(id);
		access(caller, unit, "edit", "cv");

		CVEntityV2 oldcv = requestmgr.getCV(id);
		RequestEntityV2 request = requestmgr.getRequest(requestmgr.getRequestFromCV(id));

		boolean cantestiq = cv.cantestiq;
		boolean cantesteng = cv.cantesteng;
		//Mở lại test english
		if (oldcv.cantesteng == false && cv.cantesteng == true)
		{
			long oldtestid = requestmgr.getCVTest(id, ENGTEST);
			String testcode = null;
			if (oldtestid != -1)
			{
				testcode = testmgr.matchTestCode(oldtestid);
			}
			if (oldtestid != -1 && testcode != null)
			{
				testmgr.deleteTestCode(testcode);
			}

			EmailFormEntity ef = emailformmgr.getForm(unit, TESTMAIL);
			Integer n = Integer.parseInt(unitmgr.getConfig(unit, "n"));
			Integer p = Integer.parseInt(unitmgr.getConfig(unit, "p"));
			long testid = testmgr.createTest(unit, ENGTEST, n, p);
			TestEntity test = testmgr.getTest(testid);
			requestmgr.addCVTest(id, testid);

			EmailEntity email = buildTestMail(ef, request, oldcv, test, testmgr.matchTestCode(testid));
			mailsdr.send(cv.email, email);
		}

		//Mở lại test iq
		if (oldcv.cantestiq == false && cv.cantestiq == true)
		{
			long oldtestid = requestmgr.getCVTest(id, IQTEST);
			String testcode = null;
			if (oldtestid != -1)
			{
				testcode = testmgr.matchTestCode(oldtestid);
			}
			if (oldtestid != -1 && testcode != null)
			{
				testmgr.deleteTestCode(testcode);
			}

			EmailFormEntity ef = emailformmgr.getForm(unit, TESTMAIL);
			Integer n = Integer.parseInt(unitmgr.getConfig(unit, "n"));
			Integer p = Integer.parseInt(unitmgr.getConfig(unit, "p"));
			long testid = testmgr.createTest(unit, IQTEST, n, p);
			requestmgr.addCVTest(id, testid);
			TestEntity test = testmgr.getTest(testid);
			EmailEntity email = buildTestMail(ef, request, oldcv, test, testmgr.matchTestCode(testid));
			mailsdr.send(cv.email, email);
		}

        /*tự động chuyển bag*/
		long primarybag = requestmgr.getPrimaryBag(request.id);
		if (cv.iq != -1 && cv.eng != -1 && requestmgr.getBagFromCV(id) == primarybag)
		{
			requestmgr.moveCVintoBag(id, primarybag);
		}

		if (cv.arch > 0 && cv.iq > 0 && cv.eng > 0 && cv.pote > 0 && cv.pres > 0)
		{
			int total = cv.arch + cv.iq * 3 + cv.eng / 2 + cv.pote + cv.pres;
			cv = new CVEntityV2(cv.id, cv.ctime, cv.state, cv.level, cv.filename, cv.name, cv.email, cantesteng, cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, total, cv.pote, cv.eng, cv.receivedmail, cv.iqtested, cv.engtestresultmail);

		} else
		{
			cv = new CVEntityV2(cv.id, cv.ctime, cv.state, cv.level, cv.filename, cv.name, cv.email, cantesteng, cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, -1, cv.pote, cv.eng, cv.receivedmail, cv.iqtested, cv.engtestresultmail);
		}
		requestmgr.editCV(id, cv);
	}

	@Override
	public void editRequest(long caller, long requestid, RequestEntityV2 req)
	{
		int unit = requestmgr.getRequestsUnit(requestid);
		access(caller, unit, "delete", "request");
		requestmgr.editRequest(requestid, req);
	}

	@Override
	public long createRequest(long caller, int unit, RequestEntityV2 request)
	{
		access(caller, unit, "create", "request");
		long rid = requestmgr.createRequest(unit, request);
		if (rid != -1)
		{
			requestmgr.createBag(rid, new CVBagEntity("Inbox", 0, 0, null, caller));
			requestmgr.createBag(rid, new CVBagEntity("Tested", 0, 0, null, caller));
		}
		return rid;
	}

	@Override
	public void deleteRequest(long caller, long requestid)
	{
		int unit = requestmgr.getRequestsUnit(requestid);
		access(caller, unit, "delete", "request");
		requestmgr.deleteRequest(requestid);
	}

	@Override
	public List<RequestEntityV2> listRequestByUnit(long caller, int unit, int p, int ps, String[] s)
	{
		access(caller, unit, "list", "request");
		return requestmgr.listByUnit(unit, p, ps, s);
	}

	@Override
	public void editBag(long caller, long bagid, CVBagEntity bag)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		access(caller, unit, "edit", "bag");
		requestmgr.editBag(bagid, bag);
	}

	@Override
	public CVBagEntity getBag(long caller, long bagid)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		access(caller, unit, "get", "bag");
		return requestmgr.getBag(bagid);
	}

	@Override
	public String exportCV(long caller, long cvid, String template)
	{
		int unit = requestmgr.getCVsUnit(cvid);
		access(caller, unit, "export", "cv");

		String[] datamap = new String[20];
		String[] fieldmap = new String[]{"", "$name", "$email", "$submitdate", "$iq", "$eng", "$pres", "$arch", "$pote", "$sum", "$total", "$comment", "$requiredfield", "$cvfilecontent", "$iqpoint", "$maxiqpoint", "$engpoint", "$maxengpoint", "$engtestcontent", "$iqtestcontent"};

		CVEntityV2 cv = requestmgr.getCV(cvid);

		List<CVCommentEntity> comments = requestmgr.listCVComment(cvid);

		String commentlist = "";
		for (CVCommentEntity comment : comments)
		{
			commentlist += comment.ctime.toString() + "|" + unitmgr.getUser(comment.author).fullname + " : " + comment.comment + "</br>";
		}

		String requiredfieldlist = "";
		List<InfoEntity> infos = requestmgr.listCVInfo(cvid);
		for (InfoEntity info : infos)
		{
			RequiredField field = fieldmgr.getField(info.fieldid);
			requiredfieldlist += field.title + " : " + info.info + "</br>";
		}

		String iqtestcontent = "";

		int g = 0;

		long iqtestid = requestmgr.getCVAnswer(cvid, IQTEST);
		long engtestid = requestmgr.getCVAnswer(cvid, ENGTEST);
		TestAnswerEntity iqtestresult = testmgr.getAnswer(iqtestid);
		TestAnswerEntity engtestresult = testmgr.getAnswer(engtestid);

		int iqpoint = 0;
		int maxiqpoint = 0;
		if (iqtestresult != null)
		{
			for (int j = 0; j < iqtestresult.answers.size(); j++)
			{

				QuestionEntityV2 q = testmgr.getQuestion(iqtestresult.questions.get(j));
				if (iqtestresult.answers.get(j) == q.answer)
				{
					g++;
				}
				String[] cs = q.choose.split("\\\\n");

				iqtestcontent += "<div class='testanswer'>" + (j + 1) + "." + q.content + "</br>";

				iqtestcontent += "<ol>";
				int ai = 0; //answer index
				for (String a : cs)
				{

					ai++;
					if (ai == q.answer)
					{
						iqtestcontent += "<li><i>" + a + "</i></li>";
					} else
					{

						if (iqtestresult.answers.get(j) == ai || iqtestresult.answers.get(j) == 0)
						{
							iqtestcontent += "<li style='text-decoration: line-through;'>" + a + "</li>";
						} else
						{
							iqtestcontent += "<li>" + a + "</li>";
						}
					}
				}

				iqtestcontent += "</ol></div>";
			}
			iqpoint = g;
			maxiqpoint = iqtestresult.answers.size();
		}

		int engpoint = 0;
		int maxengpoint = 0;
		g = 0;
		String engtestcontent = "";
		if (engtestresult != null)
		{
			for (int j = 0; j < engtestresult.answers.size(); j++)
			{
				QuestionEntityV2 q = testmgr.getQuestion(engtestresult.questions.get(j));
				String[] cs = q.choose.split("\\\\n");
				if (engtestresult.answers.get(j) == q.answer)
				{
					g++;
				}
				engtestcontent += "<div class='testanswer'>" + (j + 1) + "." + q.content + "</br>";

				engtestcontent += "<ol>";
				int ai = 0; //answer index
				for (String a : cs)
				{

					ai++;
					if (ai == q.answer)
					{
						engtestcontent += "<li><i>" + a + "</i></li>";
					} else
					{

						if (engtestresult.answers.get(j) == ai || engtestresult.answers.get(j) == 0)
						{
							engtestcontent += "<li style='text-decoration: line-through;'>" + a + "</li>";
						} else
						{
							engtestcontent += "<li>" + a + "</li>";
						}
					}
				}

				engtestcontent += "</ol></div>";

			}
			engpoint = g;
			maxengpoint = engtestresult.answers.size();
		}

		datamap[0] = cv.name;
		datamap[1] = cv.email;
		datamap[2] = cv.ctime.toString();
		datamap[3] = cv.iq + "";
		datamap[4] = cv.eng + "";
		datamap[5] = cv.pres + "";
		datamap[6] = cv.arch + "";
		datamap[7] = cv.pote + "";
		datamap[8] = cv.sum + "";
		datamap[9] = cv.total + "";
		datamap[10] = commentlist;
		datamap[11] = requiredfieldlist;
		datamap[12] = "";
		datamap[13] = iqpoint + "";
		datamap[14] = maxiqpoint + "";
		datamap[15] = engpoint + "";
		datamap[16] = maxengpoint + "";
		datamap[17] = engtestcontent;
		datamap[18] = iqtestcontent;

		int n = fieldmap.length;
		for (int i = 1; i < n; i++)
		{
			template = template.replace(fieldmap[i] + " ", "%" + i + "$s");
		}

		template = String.format(template, (Object[]) datamap);
		return template;
	}

	@Override
	public String quickviewCV(long caller, long cvid)
	{
		int unit = requestmgr.getCVsUnit(cvid);
		access(caller, unit, "quickview", "cv");
		CVEntityV2 cv = requestmgr.getCV(cvid);

		return cv.filename;

	}

	@Override
	public QuestionEntityV2 getQuestion(long caller, long qid)
	{
		int unit = testmgr.getQuestionsUnit(qid);
		access(caller, unit, "get", "question");
		return testmgr.getQuestion(qid);
	}

	@Override
	public RequiredField getRequiredField(long caller, long fieldid)
	{
		int unit = fieldmgr.getRequiredFieldsUnit(fieldid);
		access(caller, unit, "get", "requiredfield");
		return fieldmgr.getField(fieldid);
	}

	@Override
	public void getInUnit(long caller, long userid, int unit)
	{
		//if (userid == caller)
		//{
		//	unitmgr.access(caller, "set user unit");
		//} else
		//{
		//	unitmgr.access(caller, "set other user unit");
		//}

		unitmgr.getInUnit(userid, unit);
	}

	@Override
	public void getOutUnit(long caller, long userid, int unit)
	{
		if (userid == caller)
		{
			unitmgr.access(caller, "set user unit");
		} else
		{
			unitmgr.access(caller, "set other user unit");
		}

		unitmgr.getOutUnit(userid, unit);
	}

	@Override
	public void deleteQuestion(long caller, long questionid)
	{
		int unit = testmgr.getQuestionsUnit(questionid);
		access(caller, unit, "delete", "question");

		int n = Integer.valueOf(unitmgr.getConfig(unit, "n"));
		int p = Integer.valueOf(unitmgr.getConfig(unit, "p"));

		int ret = testmgr.tryDeleteQuestion(questionid, n, p);
		if (ret == -1)
		{
			throw new RuntimeException("cannot edit question");
		}

		testmgr.deleteQuestion(questionid);
	}

	@Override
	public void deleteBag(long caller, long bagid)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		access(caller, unit, "delete", "bag");

		long id = requestmgr.getRequestFromBag(bagid);
		long primarybagid = requestmgr.getPrimaryBag(id);
		long secondarybagid = requestmgr.getSecondaryBag(id);
		if (bagid == primarybagid || bagid == secondarybagid)
		{
			throw new RuntimeException("cannot delete standard bag");
		}

		long count = requestmgr.countCV(bagid);
		count = count / 20 + 1;
		for (int i = 0; i < count; i++)
		{
			List<CVEntityV2> cvs = requestmgr.listCVInBag(bagid, i, 20, null);
			for (CVEntityV2 cv : cvs)
			{
				requestmgr.moveCVintoBag(cv.id, primarybagid);
			}

		}
		requestmgr.deleteBag(bagid);
	}

	@Override
	public void editQuestion(long caller, long qid, QuestionEntityV2 q)
	{
		int unit = testmgr.getQuestionsUnit(qid);
		access(caller, unit, "edit", "question");
		if (q.weight < 1 || q.weight > 3)
		{
			throw new RuntimeException("Question weight must be 1, 2 or 3");
		}

		int n = Integer.valueOf(unitmgr.getConfig(unit, "n"));
		int p = Integer.valueOf(unitmgr.getConfig(unit, "p"));

		int ret = testmgr.tryEditQuestion(qid, q, n, p);
		if (ret == -1)
		{
			throw new RuntimeException("cannot edit question");
		}
	}

	@Override
	public void editEmailForm(long caller, long efid, EmailFormEntity ef)
	{
		int unit = emailformmgr.getEmailFormsUnit(efid);
		access(caller, unit, "edit", "emailform");
		emailformmgr.editEmailForm(efid, ef);
	}

	@Override
	public List<EmailFormEntity> listEmailForm(long caller, int unit)
	{
		access(caller, unit, "list", "emailform");
		return emailformmgr.listEmailForm(unit);
	}

	@Override
	public EmailFormEntity getEmailForm(long caller, long id)
	{
		int unit = emailformmgr.getEmailFormsUnit(id);
		access(caller, unit, "get", "emailform");
		return emailformmgr.getForm(id);
	}

	@Override
	public long createBag(long caller, long requestid, CVBagEntity bag)
	{
		int unit = requestmgr.getRequestsUnit(requestid);
		access(caller, unit, "create", "bag");

		return requestmgr.createBag(requestid, bag);
	}

	@Override
	public long createEmailForm(long caller, int unit, EmailFormEntity ef)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

		//access(caller, unit, "create", "emailform");
	}

	@Override
	public void moveCV(long caller, long bagid, long cvid)
	{
		int unit = requestmgr.getBagsUnit(bagid);
		if (unit != requestmgr.getCVsUnit(cvid))
		{
			throw new RuntimeException("Cant not move cv cross unit");
		}

		access(caller, unit, "move", "cv");
		requestmgr.moveCVintoBag(cvid, bagid);
	}

	@Override
	public String getConfig(long caller, int unitid, String ref)
	{
		access(caller, unitid, "get", "config");
		return unitmgr.getConfig(unitid, ref);
	}

	@Override
	public void setConfig(long caller, int unitid, String ref, String val)
	{
		access(caller, unitid, "set", "config");

		if (ref.equals("np"))
		{
			String[] vals = val.split(",");

			int p = Integer.valueOf(vals[1]);
			int n = Integer.valueOf(vals[0]);
			if (testmgr.estimateTest(unitid, 1, n, p) <= 0)
			{
				throw new RuntimeException("Invalid value");
			}
			if (testmgr.estimateTest(unitid, 2, n, p) <= 0)
			{
				throw new RuntimeException("Invalid value");
			}
			unitmgr.setConfig(unitid, "n", n + "");
			unitmgr.setConfig(unitid, "p", p + "");

		} else
		{
			unitmgr.setConfig(unitid, ref, val);
		}
	}

	@Override
	public UserEntityV2 getUser(long caller, long uid)
	{
		unitmgr.access(caller, "get user");
		if (caller != uid)
		{
			unitmgr.access(caller, "get other user");
		}

		return unitmgr.getUser(uid);
	}

	@Override
	public List<UnitEntity> getUnitbyUser(long caller, long userid)
	{
		unitmgr.access(caller, "get unit");
		if (caller != userid)
		{
			unitmgr.access(caller, "get other unit");
		}

		return unitmgr.listUnitbyUser(userid);
	}

	private void access(long caller, int unit, String action, String resouce)
	{
		if (isInUnit(caller, unit))
		{
			unitmgr.access(caller, action + " " + resouce);
		} else
		{
			unitmgr.access(caller, action + " other " + resouce);
		}
	}

	@Override
	public CVEntityV2 getCV(long caller, long id)
	{
		int unit = requestmgr.getCVsUnit(id);
		access(caller, unit, "get", "cv");
		return requestmgr.getCV(id);
	}

	public RequestEntityV2 peekRequest(long caller, long requestid)
	{
		int unit = requestmgr.getRequestsUnit(requestid);

		RequestEntityV2 request = requestmgr.getRequest(requestid);
		if (request == null)
		{
			return null;
		}
		if (request.state != REQUESTOPEN)
		{
			access(caller, unit, "get", "closed request");
		}
		RequestEntityV2 gr = new RequestEntityV2(requestid, request.jobdesc, request.title, request.position, request.interest, request.requirement, -1, request.ctime, -1, -1);
		return gr;
	}

	@Override
	public RequestEntityV2 getRequest(long caller, long requestid)
	{
		int unit = requestmgr.getRequestsUnit(requestid);
		access(caller, unit, "get", "request");

		RequestEntityV2 request = requestmgr.getRequest(requestid);
		if (request == null)
		{
			return null;
		}
		if (request.state != REQUESTOPEN)
		{
			access(caller, unit, "get", "closed request");
		}
		return request;
	}

	@Override
	public long countCVRequest(long caller, long request)
	{
		int unit = requestmgr.getRequestsUnit(request);
		access(caller, unit, "get", "bag");
		List<CVBagEntity> bags = requestmgr.listBag(request);
		long n = 0;
		for (CVBagEntity bag : bags)
		{
			n += requestmgr.countCV(bag.id);
		}
		return n;
	}

	@Override
	public long getPrimaryBag(long caller, long request)
	{
		int unit = requestmgr.getRequestsUnit(request);
		access(caller, unit, "get", "bag");
		return requestmgr.getPrimaryBag(request);
	}

	@Override
	public long getSecondaryBag(long caller, long request)
	{
		int unit = requestmgr.getRequestsUnit(request);
		access(caller, unit, "get", "bag");
		return requestmgr.getSecondaryBag(request);

	}

	@Override
	public String matchUsername(long caller, long uid)
	{
		if (caller == uid)
		{
			unitmgr.access(caller, "get user");
		} else
		{
			unitmgr.access(caller, "get other user");
		}

		return unitmgr.getUsername(uid);
	}

	@Override
	public List<CCVField> listCVField(long caller, long cvid)
	{
		int unit = requestmgr.getCVsUnit(cvid);
		access(caller, unit, "list", "cvinfo");

		List<CCVField> ccvfield = new ArrayList<CCVField>();
		List<InfoEntity> infos = requestmgr.listCVInfo(cvid);
		for (InfoEntity info : infos)
		{
			RequiredField field = fieldmgr.getField(info.fieldid);
			ccvfield.add(new CCVField(field.title, info.info));
		}
		return ccvfield;
	}

	@Override
	public long countRequest(long caller, String k)
	{
		return requestmgr.countOpenRequest(k);
	}

	@Override
	public List<RequestEntityV2> listRequest(long caller, int p, int ps, String k, String[] s)
	{
		return requestmgr.listOpenRequest(p, ps, k, s);
	}

	@Override
	public UnitEntity getUnitbyRequest(long caller, long requestid)
	{
		int unitid = requestmgr.getRequestsUnit(requestid);
		return unitmgr.getUnit(unitid);
	}
}
