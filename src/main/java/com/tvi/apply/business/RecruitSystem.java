//thanhpk
package com.tvi.apply.business;

import com.tvi.apply.business.core.*;
import com.tvi.apply.data.entity.*;
import com.tvi.apply.type.*;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.excel.IExcelExporter;
import com.tvi.apply.util.file.IFileHelper;
import com.tvi.apply.util.mail.Email;
import com.tvi.apply.util.mail.IEmailHelper;
import com.tvi.apply.util.mail.IMailSender;
import com.tvi.apply.util.rar.IRarPacker;
import com.tvi.apply.util.time.Watch;

import javax.xml.bind.TypeConstraintException;
import java.io.File;
import java.util.*;

public class RecruitSystem implements IRecruitSystem
{
	final IDatabase db;
	final IEmailTemplateMgt temmgr;
	final IEmailHelper emailhelper;
	final ICandidateMgt canmgr;
	final IFieldMgt fieldmgr;
	final IJobMgt jobmgr;
	final IOrgMgt orgmgr;
	final ITestMgt testmgr;
	final IUserMgt usermgr;
	final IAuth auth;
    final IFileHelper filemgr;
	private IMailSender sender;
	final IExcelExporter exler;
	IRarPacker packer;
	private String address;
	private String domain;

	public RecruitSystem(String domain, String address, IDatabase db, IMailSender sender, IEmailHelper emailhelper, ICandidateMgt canmgr, IFieldMgt fieldmgr, IJobMgt jobmgr, IOrgMgt orgmgr, ITestMgt testmgr, IUserMgt usermgr, IAuth auth, IEmailTemplateMgt temmgr, IEmailCrawler crawler, IRarPacker packer, IExcelExporter exler, IFileHelper filemgr)
	{
		this.sender = sender;
		this.domain = domain;
		this.address = address;
		this.exler = exler;
		this.packer = packer;
		this.temmgr = temmgr;
		this.db = db;
		this.emailhelper = emailhelper;
		this.canmgr = canmgr;
		this.fieldmgr = fieldmgr;
		this.jobmgr = jobmgr;
		this.orgmgr = orgmgr;
		this.testmgr = testmgr;
		this.usermgr = usermgr;
		this.auth = auth;
        this.filemgr = filemgr;
		crawler.run(emailhelper, canmgr, 3600);
	}

	@Override
	public long createUser(long caller, TUser user)
	{
		usermgr.access(caller, "create user");
		EUser e = TypeEntityConverter.convert(user);
		return usermgr.createUser(e);
	}

	@Override
	public void editUser(long caller, TUser user)
	{
		usermgr.access(caller, "edit user");
		EUser e = TypeEntityConverter.convert(user);
		usermgr.editUser(e);
	}

	@Override
	public TUser getUser(long caller, long userid)
	{
		if (caller == userid) usermgr.access(caller, "read user");
		else usermgr.access(caller, "read other user");
		return usermgr.getUser(userid);
	}

	@Override
	public String loginUP(String username, String password)
	{
		return usermgr.login(username, password);
	}

	@Override
	public long matchAuth(String authcode)
	{
		return usermgr.matchAuth(authcode);
	}

	@Override
	public void logout(long caller, String authcode)
	{
		usermgr.logout(authcode);
	}

	@Override
	public List<TEmail> listNewEmail(long caller, long userid, int n, int p)
	{
		usermgr.access(caller, "read email");
		List<EEmail> lees = canmgr.listUnreadEmail(userid, n, p);
		List<TEmail> ltes = new ArrayList<TEmail>();
		for (EEmail ee : lees)
			ltes.add(TypeEntityConverter.convert(ee));
		return ltes;
	}

	@Override
	public List<TEmail> listJunkMail(long caller, int n, int p)
	{
		return null;
	}

	@Override
	public TEmail viewEmail(long caller, long emailid)
	{

		usermgr.access(caller, "read email");
		EEmail eemail = canmgr.readEmail(emailid);
		return TypeEntityConverter.convert(eemail);
	}

	@Override
	public void markAsRead(long caller, long userid, long emailid)
	{
		usermgr.access(caller, "read email");
		canmgr.markEmailAsReaded(userid, emailid);
	}

	@Override
	public TEmail[] listUnreadEmail(long caller, long userid, String email, int n, int p)
	{
		usermgr.access(caller, "read email");
		List<EEmail> els = canmgr.listUnreadEmail(userid, email, n, p);
		int s = els.size();
		TEmail[] tls = new TEmail[s];
		for (int i = 0; i < s; i++)
		{
			tls[i] = TypeEntityConverter.convert(els.get(i));
		}

		return tls;
	}

	@Override
	public long countUnreadEmail(long caller, long userid, String email)
	{
		usermgr.access(caller, "read email");
		return canmgr.countUnreadEmail(userid, email);
	}


	@Override
	public void markAsUnread(long caller, long userid, long emailid)
	{
		usermgr.access(caller, "read email");
		canmgr.markEmailAsUnreaded(userid, emailid);
	}

	@Override
	public void junkMail(long caller, long emailid)
	{
		usermgr.access(caller, "update email");
		canmgr.junkMail(emailid);
	}

	@Override
	public void unjunkMail(long caller, long emailid)
	{
		usermgr.access(caller, "update email");
		canmgr.unjunkMail(emailid);
	}

	@Override
	public void sendMail(long caller, TEmail email)
	{
		usermgr.access(caller, "send email");
		Email mail = new Email();
		mail.attachment = email.attachment;
		mail.bcc = email.bcc != null ? email.bcc.split(";") : new String[0];
		mail.body = email.body;
		mail.cc = email.cc != null ? email.cc.split(";") : new String[0];
		mail.from = address;
		mail.messageid = email.messageid;
		mail.sendtime = new Date().toString();
		mail.subject = email.subject;
		mail.to = email.to;

		emailhelper.send(mail);
	}

	@Override
	public void cleanUnread(long caller, long user)
	{
		usermgr.access(caller, "read email");
		canmgr.cleanUnread(user);
	}

	@Override
	public long createJob(long caller, TJob job, EJobSetting ejs)
	{
		usermgr.access(caller, "write job");
		EJob j = TypeEntityConverter.convert(job);
		long[] fs = new long[job.fields.length];
		for (int i = 0; i < fs.length; i++)
			fs[i] = job.fields[i].id;
		jobmgr.updateJobField(j.id, fs);

		j.tag = "J";//+ id  + "." + j.ctime.get(Calendar.MONTH) + "" + (j.ctime.get(Calendar.YEAR) +"").substring(2);

		j.ctime = Calendar.getInstance();
		j.lmtime = Calendar.getInstance();

		long id = jobmgr.createJob(j, ejs);
		j.tag = "J" + id + "." + j.ctime.get(Calendar.MONTH) + "" + (j.ctime.get(Calendar.YEAR) + "").substring(2);
		j.id = id;
		jobmgr.updateJob(j);
		return id;

	}

	@Override
	public void updateJob(long caller, TJob job)
	{
		usermgr.access(caller, "write job");
		EJob j = TypeEntityConverter.convert(job);
		jobmgr.updateJob(j);
		long[] fs = new long[job.fields.length];
		for (int i = 0; i < fs.length; i++)
			fs[i] = job.fields[i].id;
		jobmgr.updateJobField(j.id, fs);
	}

	@Override
	public TJob getJob(long caller, long id)
	{
		usermgr.access(caller, "read job");
		TJob tt = TypeEntityConverter.convert(jobmgr.readJob(id));
		List<EField> es = jobmgr.listFieldByJob(tt.id);
		TField[] fs = new TField[es.size()];

		int i = 0;
		for (EField e : es)
		{
			fs[i] = TypeEntityConverter.convert(e);
			i++;
		}

		tt.fields = fs;

		return tt;
	}

	@Override
	public List<TJob> listJobByOrg(long caller, boolean archived, long org, String keyword, int n, int p)
	{
		return null;
	}

	@Override
	public long countJobByOrg(long caller, boolean archived, long org, String keyword)
	{
		return 0;
	}

	@Override
	public List<TJob> listJob(long caller, boolean archived, String keyword, int n, int p)
	{
		usermgr.access(caller, "read job");
		List<EJob> lees = jobmgr.listJob(archived, keyword, n, p);

		List<TJob> ltes = new ArrayList<TJob>();
		for (EJob ee : lees)
		{
			long countcandidate = canmgr.countCandidateByJob(new long[]{ee.id}, true, -1, "", null, null, null);
			TJob tt = TypeEntityConverter.convert(ee);
			tt.ccount = countcandidate;
			List<EField> es = jobmgr.listFieldByJob(tt.id);
			TField[] fs = new TField[es.size()];

			int i = 0;
			for (EField e : es)
			{
				fs[i] = TypeEntityConverter.convert(e);
				i++;
			}

			tt.fields = fs;
			ltes.add(tt);
		}
		return ltes;
	}

	@Override
	public long countJob(long caller, boolean archived, String keyword)
	{
		usermgr.access(caller, "read job");
		return orgmgr.countOrg(archived, keyword);
	}

	@Override
	public List<TActionLog> listActionLog(long caller, long userid, int n, int p)
	{
		if (caller != userid) usermgr.access(caller, "read other log");
		else usermgr.access(caller, "read log");
		List<EActionLog> lees = usermgr.listActionLog(userid, n, p);
		List<TActionLog> ltes = new ArrayList<TActionLog>();
		for (EActionLog ee : lees)
			ltes.add(TypeEntityConverter.convert(ee));
		return ltes;
	}

	@Override
	public long countActionLog(long caller, long userid)
	{
		if (caller != userid) usermgr.access(caller, "read other log");
		else usermgr.access(caller, "read log");
		return usermgr.countActionLog(userid);
	}

	@Override
	public long comment(long caller, long userid, long cid, String comment)
	{
		usermgr.access(caller, "comment candidate");
		usermgr.logAction(caller, new EActionLog(0, null, "comment in ?", cid + "", "candidate"));
		canmgr.logCandidate(cid, new ECandidateLog("commented by ?", userid + "", "user"));
		return canmgr.commentCandidate(cid, new EComment(comment, userid));
	}

	@Override
	public void editComment(long caller, long commentid, String comment)
	{
		usermgr.access(caller, "edit comment");
		canmgr.updateComment(commentid, new EComment(comment, caller));
	}

	@Override
	public void deleteComment(long caller, long commentid)
	{
		usermgr.access(caller, "delete comment");
		canmgr.deleteComment(commentid);
	}

	@Override
	public long createCandidate(long caller, TCandidate candidate)
	{
		ECandidate c = TypeEntityConverter.convert(candidate);
		TCandidateField[] fields = candidate.fields;

		long id = canmgr.createCandidate(c);

		for (int i = 0; i < fields.length; i++)
		{
			canmgr.createCandidateInfo(id, candidate.fields[i].fieldid, candidate.fields[i].content, caller);
		}

		EJobSetting js = jobmgr.getJobSetting(candidate.jobid);
		String mail = buildMail(js.rcvmail, c);
		TEmail email = new TEmail();
		String[] split = mail.split("\n");
		if (split.length > 1)
		{
			email.body = split[1];
			email.subject = split[0];
		} else
		{
			email.body = "";
			email.subject = "";
		}

		email.to = candidate.email;
		sendMail(2, email);
		return id;
	}

	@Override
	public void updateCandidate(long caller, TCandidate candidate)
	{
		usermgr.access(caller, "write candidate");
		canmgr.updateCandidate(TypeEntityConverter.convert(candidate));
	}
	public  void deleteCandidate(long id)
	{
        ECandidate c = canmgr.readCandidate(id);
        try{
            canmgr.deleteCandidate(id);
            if(c.attachment.length()>13){
                File file = new File(filemgr.getBasepath()+"/"+c.attachment.substring(13));
                if(file.exists()) file.delete();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
	}

	@Override
	public long addInfo(long caller, long cid, long fid, String value)
	{
		return canmgr.createCandidateInfo(cid, fid, value, caller);
	}

	@Override
	public void deleteInfo(long caller, long cid, long fid)
	{
		canmgr.deleteCandidateInfo(fid, cid);
	}

	@Override
	public void editInfo(long caller, long cid, long fid, String value)
	{
		canmgr.updateCandidateInfo(cid, fid, value);
	}

	@Override
	public TCandidate viewCandidate(long caller, long id, ECandidate ecan)
	{
		Watch w1 = new Watch();

		usermgr.access(caller, "read candidate");
		ECandidate ee = ecan == null ? canmgr.readCandidate(id) : ecan;
		w1.stop();
		Watch w4 = new Watch();
		TCandidate can = TypeEntityConverter.convert(ee);

		long un = canmgr.countUnreadEmail(caller, ee.email);
		can.unread = un;
		w4.stop();
		Watch w2 = new Watch();
		can.replied = canmgr.haveSent(ee.email);
		w2.stop();


		List<String[]> attachments = canmgr.listCandidateAttachments(can.id);

		if (can.attachment != null || !can.attachment.equals(""))
		{
			can.attachments = new String[attachments.size() + 1];
			can.emailid = new String[attachments.size() + 1];
			can.attachments[0] = can.attachment;
			can.emailid[0] = "";
			//attachment
			for (int i = 0; i < attachments.size(); i++)
			{
				can.attachments[i + 1] = attachments.get(i)[1];
				can.emailid[i + 1] = attachments.get(i)[0];
			}
		} else
		{
			can.attachments = new String[attachments.size()];
			can.emailid = new String[attachments.size()];
			//attachment
			for (int i = 0; i < attachments.size(); i++)
			{

				can.attachments[i] = attachments.get(i)[1];
				can.emailid[i] = attachments.get(i)[0];
			}
		}



		Watch w3 = new Watch();
		//field
		List<EInfo> eInfos = canmgr.listCandidateInfo(can.id);
		can.fields = new TCandidateField[eInfos.size()];
		for (int i = 0; i < eInfos.size(); i++)
		{
			can.fields[i] = TypeEntityConverter.convert(eInfos.get(i));
		}

		//comment
		List<EComment> eComments = canmgr.listCandidateComment(can.id);
		can.comments = new TComment[eComments.size()];
		for (int i = 0; i < eComments.size(); i++)
		{
			can.comments[i] = TypeEntityConverter.convert(eComments.get(i));
		}

		can.iqtestlink = testmgr.matchTestCode(testmgr.getUserTest(can.id, 1));
		can.engtestlink = testmgr.matchTestCode(testmgr.getUserTest(can.id, 2));
		w3.stop();

		return can;
	}

	@Override
	public List<TEmail> listCandidateMail(long caller, long cid, int n, int p)
	{
		usermgr.access(caller, "read candidate");
		List<EEmail> eEmails = canmgr.listCandidateEmail(cid, n, p);

		List<TEmail> tEmails = new ArrayList<TEmail>();

		for (EEmail e : eEmails)
		{
			TEmail t = TypeEntityConverter.convert(e);
			t.readed = canmgr.isEmailReaded(caller, t.id);
			tEmails.add(t);
		}
		return tEmails;
	}

	@Override
	public List<TOrg> listOrg(long caller, boolean archived, String keyword, int n, int p)
	{
		List<EOrg> leos = orgmgr.listOrg(archived, keyword, n, p);

		List<TOrg> ltos = new ArrayList<TOrg>();
		for (EOrg org : leos)
			ltos.add(TypeEntityConverter.convert(org));
		return ltos;
	}

	@Override
	public long createOrg(long caller, TOrg org)
	{
		return orgmgr.createOrg(TypeEntityConverter.convert(org));
	}

	@Override
	public void editOrg(long caller, TOrg org)
	{
		orgmgr.updateOrg(TypeEntityConverter.convert(org));
	}

	@Override
	public long countOrg(long caller, boolean archived, String keyword)
	{
		return orgmgr.countOrg(archived, keyword);
	}

	@Override
	public TOrg getOrg(long caller, long id)
	{
		return TypeEntityConverter.convert(orgmgr.readOrg(id));
	}

	@Override
	public List<TField> listField(long caller, boolean archived, String keyword, int n, int p)
	{
		List<EField> els = fieldmgr.listField(archived, keyword, n, p);
		List<TField> tls = new ArrayList<TField>();
		for (EField ee : els)
			tls.add(TypeEntityConverter.convert(ee));
		return tls;
	}

	@Override
	public long createField(long caller, TField field)
	{
		return fieldmgr.createField(TypeEntityConverter.convert(field));
	}

	@Override
	public void editField(long caller, TField field)
	{
		fieldmgr.updateField(TypeEntityConverter.convert(field));
	}

	@Override
	public long countField(long caller, boolean archived, String keyword)
	{
		return fieldmgr.countField(archived, keyword);
	}

	@Override
	public List<TEmailTemplate> listTemplate(long caller, String keyword, int n, int p)
	{
		usermgr.access(caller, "read template");
		List<EEmailTemplate> els = temmgr.listTemplate(keyword, n, p);
		List<TEmailTemplate> tls = new ArrayList<TEmailTemplate>();

		for (EEmailTemplate ee : els)
			tls.add(TypeEntityConverter.convert(ee));

		return tls;
	}

	@Override
	public long createTemplate(long caller, TEmailTemplate template)
	{
		usermgr.access(caller, "write candidate");
		return temmgr.createTemplate(TypeEntityConverter.convert(template));
	}

	@Override
	public void updateTemplate(long caller, TEmailTemplate template)
	{
		usermgr.access(caller, "write candidate");
		temmgr.updateTemplate(TypeEntityConverter.convert(template));
	}

	@Override
	public long countTemplate(long caller, String keyword)
	{
		usermgr.access(caller, "read candidate");
		return temmgr.countTemplate(keyword);
	}

	@Override
	public TTest2 startTest(long caller, String code)
	{
		//Kiểm tra xem test có tồn tại hay ko
		long testid = testmgr.matchTest(code);
		if (testid == -1)
		{
			throw new RuntimeException("Test is aldready taken or is locked for some reason");
		}

		ETest test = testmgr.getTest(testid);
		long cvid = testmgr.getUserFromTest(testid);

		if (cvid == -1)
		{
			throw new RuntimeException("Test is locked");
		}

		ECandidate cv = canmgr.readCandidate(cvid);
		long reqid = cv.jobid;
		EJob req = jobmgr.readJob(reqid);

		//if (req.state != 0)
		//{
		//		throw new RuntimeException("Request closed");
		//	}

		// ret;
		long answerid;
		if (test.type == 1) //IQTEST
		{
			testmgr.deleteTestCode(code);
			answerid = testmgr.startTest(testid);
			testmgr.addUserAnswer(cvid, answerid);
			cv.iq = -2; //dang lam
		} else //ENGLISH TEST
		{
			testmgr.deleteTestCode(code);
			answerid = testmgr.startTest(testid);
			testmgr.addUserAnswer(cvid, answerid);
			cv.eng = -2; //dang lam
		}

		List<EQuestion> qes = testmgr.getTestQuestion(testid);
		List<TQuestion> qs = new ArrayList<TQuestion>(qes.size());
		int sec = 0;

		for (EQuestion e : qes)
		{
			TQuestion q = TypeEntityConverter.convert(e);
			sec += 60 * e.weight;
			q.choose = mixQ(q.choose);
			qs.add(q);
		}

		TTest ret = new TTest(testmgr.matchAnswerCode(answerid), qs, sec);
		canmgr.updateCandidate(cv);
		return new TTest2(ret, cv.fullname, cv.ctime, req.id, req.title);
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
		}
		return pos + 1;
	}

	private String mixQ(String choose)
	{
		if (!choose.contains("\\n")) {return choose;}

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
	public void submitTest(long caller, String answercode, List<Long> qs, List<String> cs, List<Integer> answers)
	{
		long answerid = testmgr.matchAnswer(answercode);
		if (answerid == -1)
		{
			throw new RuntimeException("Tin tuyển dụng bị đóng hoặc đã submit câu trả lời rồi");
		}

		long cvid = testmgr.getUserFromAnswer(answerid);//  requestmgr.getCVFromAnswer(answerid);
		//int unit = requestmgr.getCVsUnit(cvid);
		ECandidate cv = canmgr.readCandidate(cvid);// requestmgr.getCV(cvid);

		List<Integer> realanswer = new ArrayList<Integer>();

		for (int i = 0; i < qs.size(); i++)
		{
			EQuestion question = testmgr.getQuestion(qs.get(i));
			realanswer.add(this.f(question.choose, cs.get(i), answers.get(i)));
		}

		testmgr.submitAnswer(answerid, qs, realanswer);
		testmgr.deleteAnswerCode(answercode);

		long testid = testmgr.getTestbyAnswer(answerid);

		ETest test = testmgr.getTest(testid);
		ETestAnswer testanswer = testmgr.getAnswer(answerid);

		if (test.type == 1) // IQ
		{
			cv.iqteststate = 2; //test xong
			cv.iq = testanswer.point;

		} else
		{
			cv.englishteststate = 2; //test xong
			cv.eng = testanswer.point;
		}

		canmgr.updateCandidate(cv);
	}

	@Override
	public String openTest(long caller, long cid, long type)
	{
		usermgr.access(caller, "open test");
		long testid = testmgr.createTest(1, (int) type, 30, 30);
		testmgr.addUserTest(cid, testid);
		ECandidate can = canmgr.readCandidate(cid);
		if (type == 1) can.iq = -1;
		else can.eng = -1;

		canmgr.updateCandidate(can);

		return testmgr.matchTestCode(testid);
	}

	@Override
	public void closeTest(long caller, long cid, long type)
	{
		usermgr.access(caller, "close test");
		long oldtestid = testmgr.getUserTest(cid, (int) type);
		String testcode = testmgr.matchTestCode(oldtestid);
		testmgr.deleteTestCode(testcode);
		ECandidate can = canmgr.readCandidate(cid);
		if (type == 1) can.iq = 0;
		else can.eng = 0;
	}

	@Override
	public TField getField(long caller, long id)
	{
		return TypeEntityConverter.convert(fieldmgr.readField(id));
	}

	@Override
	public String packCv(long caller, long[] cids)
	{
		usermgr.access(caller, "pack candidate");
		List<String> files = new ArrayList<String>(cids.length);
		for (long cid : cids)
		{
			ECandidate can = canmgr.readCandidate(cid);
			List<String[]> tmps = canmgr.listCandidateAttachments(cid);
			List<String> attachments = new ArrayList<String>(tmps.size() + 1);
			if (!can.attachment.equals("")) attachments.add(can.attachment);
			for (String[] e : tmps)
			{
				attachments.add(e[1]);
			}

			files.add(packer.pack(attachments, "attach"));
		}
		return packer.pack(files, "attach");
	}

	private String buildMail(String body, ECandidate can)
	{
		int i;
		//do not change the order
		String[] fieldmap = new String[]{"#fullname", "#sdate", "#jobtitle", "#joblink", "#email", "#englink", "#iqlink", "#engpoint", "#iqpoint", "#domain", "#id"};
		String[] datamap = new String[fieldmap.length];
		EJob job = jobmgr.readJob(can.jobid);

		datamap[0] = can.fullname;
		datamap[1] = can.ctime.getTime().toGMTString();
		datamap[2] = job.title;
		datamap[3] = "/tuyendung/" + job.id;
		datamap[4] = can.email;
		datamap[5] = domain + "/#test/" + testmgr.matchTestCode(testmgr.getUserTest(can.id, 2));
		datamap[6] = domain + "/#test/" + testmgr.matchTestCode(testmgr.getUserTest(can.id, 1));
		datamap[7] = can.eng + "";
		datamap[8] = can.iq + "";
		datamap[9] = domain;
		datamap[10] = can.id + "";

		int n = fieldmap.length;
		for (i = 0; i < n; i++)
		{
			body = body.replaceAll(fieldmap[i] + "\\b", "%" + (i + 1) + "\\$s");
		}
		body = String.format(body, (Object[]) datamap);
		return body;
	}

	@Override
	public void mailMerge(long caller, long[] to, TEmail emailtem)
	{
		usermgr.access(caller, "send email");
		for (long aTo : to)
		{
			ECandidate can = canmgr.readCandidate(aTo);

			TEmail email = new TEmail();
			email.from = "HR";
			email.to = can.email;
			email.subject = buildMail(emailtem.subject, can);
			email.body = buildMail(emailtem.body, can);
			sendMail(caller, email);
		}
	}

	@Override
	public void sendCandidate(long userid, long eid, String em)
	{
		usermgr.access(userid, "send candidate");

		EEmail email = canmgr.readEmail(eid);
		email.from = em;

		ECandidate can = canmgr.readCandidate(email.candidateid);

		canmgr.createEmail(email, can.jobid);
	}

	@Override
	public void sendCandidate(long userid, long canid, String attachment, String email)
	{
		usermgr.access(userid, "send candidate");

		ECandidate can = canmgr.readCandidate(canid);

		if (can.attachment.equals(attachment))
		{
			can.email = email;
			canmgr.updateCandidate(can);
		} else
		{
			canmgr.sendAttachment(canid, attachment, email);
		}
	}

	@Override
	public List<TCandidate> listCandidateByJob(long caller, long[] jobs, boolean archived, int cvstate, String keyword, int n, int p, String[] filter, String[] value, String[] operator, String[] orderby)
	{
		usermgr.access(caller, "read candidate");
		List<ECandidate> lees = canmgr.listCandidateByJob(caller, jobs, archived, cvstate, keyword, n, p, filter, value, operator, orderby);
		List<TCandidate> ltes = new ArrayList<TCandidate>();

		for (ECandidate ee : lees)
		{
			TCandidate can = viewCandidate(caller, ee.id, ee);

			ltes.add(can);
		}
		return ltes;
	}

	@Override
	public long countCandidateByJob(long caller, long[] jobs, boolean archived, int cvstate, String keyword, String[] filter, String[] value, String[] operator)
	{
		usermgr.access(caller, "read candidate");
		return canmgr.countCandidateByJob(jobs, archived, cvstate, keyword, filter, value, operator);
	}

	@Override
	public String exportCandidate(long userid, Long[] candidates, String s, Map<String, String> name)
	{
		usermgr.access(userid, "read candidate");
		List<Object> cans = new ArrayList<Object>(candidates.length);
		for (int i = 0; i < candidates.length; i++)
		{
			ECandidate c = canmgr.readCandidate(candidates[i]);
			cans.add(c);
		}

		return this.exler.export(cans, s, name);
	}

	@Override
	public List<TQuestion> listEngQuestion(long caller, String keyword, int n, int p)
	{
		usermgr.access(caller, "read question");
		List<EQuestion> qes = testmgr.listQuestion(1, 2, p, n, null);
		List<TQuestion> qs = new ArrayList<TQuestion>(qes.size());

		for (EQuestion e : qes)
		{
			TQuestion q = TypeEntityConverter.convert(e);
			qs.add(q);
		}
		return qs;
	}

	@Override
	public long createEngQuestion(long caller, TQuestion question)
	{
		usermgr.access(caller, "create question");

		EQuestion q = TypeEntityConverter.convert(question);
		q.type = 2;
		return testmgr.addQuestion(0, q);
	}

	@Override
	public long countEngQuestion(long caller, String keyword)
	{
		usermgr.access(caller, "create question");
		return testmgr.countQuestion(0, 2);
	}

	@Override
	public List<TQuestion> listIQQuestion(long caller, String keyword, int n, int p)
	{
		usermgr.access(caller, "read question");
		List<EQuestion> qes = testmgr.listQuestion(1, 1, p, n, null);
		List<TQuestion> qs = new ArrayList<TQuestion>(qes.size());

		for (EQuestion e : qes)
		{
			TQuestion q = TypeEntityConverter.convert(e);
			qs.add(q);
		}
		return qs;
	}

	@Override
	public long createIQQuestion(long caller, TQuestion question)
	{
		usermgr.access(caller, "create question");

		EQuestion q = TypeEntityConverter.convert(question);
		q.type = 1;
		return testmgr.addQuestion(0, q);
	}

	@Override
	public TQuestion readQuestion(long caller, long id)
	{
		usermgr.access(caller, "create question");
		return TypeEntityConverter.convert(testmgr.getQuestion(id));
	}

	@Override
	public void editQuestion(long caller, TQuestion question)
	{
		usermgr.access(caller, "create question");
		EQuestion q = TypeEntityConverter.convert(question);
		testmgr.editQuestion(q.qid, q);
	}

	@Override
	public TJobSetting getJobSetting(long caller, long id)
	{
		usermgr.access(caller, "read jobsetting");

		EJobSetting jobSetting = jobmgr.getJobSetting(id);
		if(jobSetting == null) jobSetting = new EJobSetting();
		return TypeEntityConverter.convert(jobSetting);
	}

	@Override
	public void setJobSetting(long caller, long id, TJobSetting jobsetting)
	{
		usermgr.access(caller, "create jobsetting");
		EJobSetting js = TypeEntityConverter.convert(jobsetting);
		jobmgr.setJobSetting(id, js);
	}

	@Override
	public long countCandidateMail(long caller, long cid)
	{
		usermgr.access(caller, "read candidate");
		return canmgr.countCandidateEmail(cid);
	}

    @Override
    public String getAttachment(long userid, long cid) {
        ECandidate c = canmgr.readCandidate(cid);
        if(c.attachment.length()>13){
            File file  = new File(filemgr.getBasepath()+"/"+c.attachment.substring(13));
            if(file.exists()) return c.attachment.substring(1);
        }
        return "Ứng viên "+c.fullname+" không có CV hoặc CV online";
    }

    @Override
	public long countIQQuestion(long caller, String keyword)
	{
		usermgr.access(caller, "create question");
		return testmgr.countQuestion(0, 2);
	}
}
