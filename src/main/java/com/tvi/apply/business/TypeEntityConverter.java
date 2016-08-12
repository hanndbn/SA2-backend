package com.tvi.apply.business;

import com.tvi.apply.data.entity.*;
import com.tvi.apply.type.*;

import java.util.Calendar;

public class TypeEntityConverter
{
	public static TEmail convert(EEmail e)
	{
		TEmail t = new TEmail();
		t.id = e.id;
		t.messageid = e.messageid;
		t.ctime = e.ctime;
		t.from = e.from;
		t.to = e.to;
		t.cc = e.cc;
		t.bcc = e.bcc;
		t.subject = e.subject;
		t.sendtime = e.sendtime;
		t.state = e.state;
		t.junk = e.junk;
		t.status = e.status;
		t.assignby = e.assignby;
		t.quanlifieddate = e.quanlifieddate;
		t.quanlifiedby = e.quanlifiedby;
		t.body = e.body;
		t.attachment = e.attachment;
		return t;
	}

	public static TQuestion convert(EQuestion e)
	{
		TQuestion q = new TQuestion();
		q.choose = e.choose;
		q.id = e.qid;
		q.title = e.content;
		q.weight = e.weight;
		q.type = e.type;
		q.ctime = Calendar.getInstance();
		q.ctime.setTime(e.ctime);
		return q;
	}


	public static EQuestion convert(TQuestion e)
	{
		EQuestion q = new EQuestion();
		q.choose = e.choose;
		q.qid = e.id;
		q.content = e.title;
		q.weight = e.weight;
		return q;
	}

	public static EEmail convert(TEmail e)
	{
		EEmail t = new EEmail();
		t.id = e.id;
		t.messageid = e.messageid;
		t.ctime = e.ctime;
		t.from = e.from;
		t.to = e.to;
		t.cc = e.cc;
		t.bcc = e.bcc;
		t.subject = e.subject;
		t.sendtime = e.sendtime;
		t.state = e.state;
		t.junk = e.junk;
		t.status = e.status;
		t.assignby = e.assignby;
		t.quanlifieddate = e.quanlifieddate;
		t.quanlifiedby = e.quanlifiedby;
		t.body = e.body;
		t.attachment = e.attachment;
		return t;
	}

	public static EJob convert(TJob t)
	{
		EJob e = new EJob();
		e.id = t.id;
		e.tag = t.tag;
		e.creator = t.creator;
		e.attachment = t.attachment;
		e.category = t.category;
		e.closedtime = t.closedtime;
		e.color = t.color;
		e.contact = t.contact;
		e.cvform = t.cvform;
		e.description = t.description;
		e.endtime = t.endtime;
		e.interest = t.interest;
		e.jobstatus = t.jobstatus;
		e.nview = t.nview;
		e.opentime = t.opentime;
		e.contact = t.contact;
		e.orgid = t.orgid;
		e.picture = t.picture;
		e.quantity = t.quantity;
		e.salary = t.salary;
		e.star = t.star;
		e.title = t.title;
		e.state = t.state;

		return e;
	}

	public static TJob convert(EJob t)
	{
		TJob e = new TJob();
		e.id = t.id;
		e.tag = t.tag;
		e.creator = t.creator;
		e.attachment = t.attachment;
		e.category = t.category;
		e.closedtime = t.closedtime;
		e.color = t.color;
		e.contact = t.contact;
		e.cvform = t.cvform;
		e.description = t.description;
		e.endtime = t.endtime;
		e.interest = t.interest;
		e.jobstatus = t.jobstatus;
		e.nview = t.nview;
		e.opentime = t.opentime;
		e.contact = t.contact;
		e.orgid = t.orgid;
		e.picture = t.picture;
		e.quantity = t.quantity;
		e.salary = t.salary;
		e.star = t.star;
		e.title = t.title;
		e.state = t.state;

		return e;
	}

	public static EField convert(TField t)
	{
		EField e = new EField();
		e.name = t.name;
		e.creator = t.creator;
		e.id = t.id;
		e.ctime = t.ctime;
		e.lmtime = t.lmtime;
		e.state = t.state;
		return e;
	}

	public static TField convert(EField t)
	{
		TField e = new TField();
		e.name = t.name;
		e.creator = t.creator;
		e.id = t.id;
		e.ctime = t.ctime;
		e.lmtime = t.lmtime;
		e.state = t.state;
		return e;
	}

	public static EEmailTemplate convert(TEmailTemplate t)
	{
		EEmailTemplate e = new EEmailTemplate();
		e.name = t.name;
		e.creator = t.creator;
		e.content = t.subject + "\\n\n" + t.body + "\\n\n" + t.sign;
		e.attachment = t.attachment;
		e.ctime = t.ctime;
		e.id = t.id;
		e.lmtime = t.lmtime;
		e.state = t.state;
		return e;
	}

	public static TEmailTemplate convert(EEmailTemplate t)
	{
		TEmailTemplate e = new TEmailTemplate();
		e.name = t.name;
		e.creator = t.creator;


		String[] tm = t.content.split("n\n");

		if(tm == null || tm.length < 2)
		{
			e.body = t.content;
			e.subject = "";
			e.sign = "";
		}
		else if(tm.length < 3)
		{
			e.subject = tm[0];
			e.body = tm[1];
			e.sign = "";
		}
		else if(tm.length < 4)
		{
			e.body = tm[0];
			e.subject = tm[1];
			e.sign = tm[2];
		}

		e.body = t.content;
		e.attachment = t.attachment;
		e.ctime = t.ctime;
		e.id = t.id;
		e.lmtime = t.lmtime;
		e.state = t.state;
		return e;
	}

	public static TOrg convert(EOrg t)
	{
		TOrg e = new TOrg();
		e.creator = t.creator;
		e.state = t.state;
		e.description = t.description;
		e.name = t.name;
		e.picture = t.picture;
		e.ctime = t.ctime;
		e.id = t.id;
		e.lmtime = t.lmtime;
		return e;
	}

	public static EOrg convert(TOrg t)
	{
		EOrg e = new EOrg();
		e.creator = t.creator;
		e.state = t.state;
		e.description = t.description;
		e.name = t.name;
		e.picture = t.picture;
		e.ctime = t.ctime;
		e.id = t.id;
		e.lmtime = t.lmtime;
		return e;
	}

	public static EActionLog convert(TActionLog t)
	{
		EActionLog e = new EActionLog();
		e.action = t.action;
		e.ctime = t.ctime;
		e.id = t.id;
		e.subject = t.subject;
		e.tag = t.tag;
		return e;
	}

	public static TActionLog convert(EActionLog t)
	{
		TActionLog e = new TActionLog();
		e.action = t.action;
		e.ctime = t.ctime;
		e.id = t.id;
		e.subject = t.subject;
		e.tag = t.tag;
		return e;
	}

	public static TCandidate convert(ECandidate t)
	{
		TCandidate e = new TCandidate();
		e.birth = t.birth;
		e.gender = t.gender;
		e.tech = t.tech;
		e.level = t.level;
		e.spec = t.spec;
		e.salary = t.salary;
		e.cjt = t.cjt;
		e.twe = t.twe;
		e.curemp = t.curemp;
		e.quanlified = t.quanlified;
		e.phone = t.phone;
		e.id = t.id;
		e.ctime = t.ctime;
		e.attachment = t.attachment;
		e.color = t.color;
		e.email = t.email;
		e.eng = t.eng;
		e.iq = t.iq;
		e.englishteststate = t.englishteststate;
		e.iqteststate = t.iqteststate;
		e.fullname = t.fullname;
		e.jobid = t.jobid;
		e.junk = t.junk;
		e.star = t.star;
		e.resumestatus = t.resumestatus;
		e.state = t.state;
		return e;
	}


	public static TComment convert(EComment t)
	{
		TComment c = new TComment();
		c.author = t.creator;
		c.id = t.id;
		c.lmtime = t.lmtime;
		c.value = t.comment;
		return c;
	}


	public static EInfo convert(TCandidateField t)
	{
		EInfo f = new EInfo();

		f.creator = t.author;
		f.state = t.state;
		f.lmtime = t.lmtime;
		f.value = t.content;
		f.fid = t.fieldid;
		return f;
	}

	public static TCandidateField convert(EInfo t)
	{
		TCandidateField f = new TCandidateField();

		f.author = t.creator;
		f.state = t.state;
		f.lmtime = t.lmtime;
		f.content = t.value;
		f.fieldid = t.fid;
		return f;
	}

	public static EUser convert(TUser t)
	{
		EUser e = new EUser();
		e.creator = t.creator;
		e.state = t.state;
		e.ctime = t.ctime;
		e.id = t.id;
		e.avatar = t.avatar;
		e.email = t.email;
		e.fullname = t.fullname;
		e.initial = t.initial;
		e.lastpwchanged = t.lastpwchanged;
		e.password = t.password;
		e.status = t.status;
		e.username = t.username;
		e.lmtime = t.lmtime;
		return e;
	}

	public static TUser convert(EUser t)
	{
		TUser e = new TUser();
		e.creator = t.creator;
		e.state = t.state;
		e.ctime = t.ctime;
		e.id = t.id;
		e.avatar = t.avatar;
		e.email = t.email;
		e.fullname = t.fullname;
		e.initial = t.initial;
		e.lastpwchanged = t.lastpwchanged;
		e.password = t.password;
		e.status = t.status;
		e.username = t.username;
		e.lmtime = t.lmtime;
		return e;
	}

	public static ECandidate convert(TCandidate t)
	{
		ECandidate e = new ECandidate();
		e.birth = t.birth;
		e.gender = t.gender;
		e.tech = t.tech;
		e.level = t.level;
		e.spec = t.spec;
		e.salary = t.salary;
		e.cjt = t.cjt;
		e.twe = t.twe;
		e.curemp = t.curemp;
		e.quanlified = t.quanlified;
		e.phone = t.phone;
		e.id = t.id;
		e.ctime = t.ctime;
		e.attachment = t.attachment;
		e.color = t.color;
		e.email = t.email;
		e.eng = t.eng;
		e.iq = t.iq;
		e.englishteststate = t.englishteststate;
		e.iqteststate = t.iqteststate;
		e.fullname = t.fullname;
		e.jobid = t.jobid;
		e.junk = t.junk;
		e.star = t.star;
		e.resumestatus = t.resumestatus;
		e.state = t.state;
		return e;
	}

	public static TJobSetting convert(EJobSetting e)
	{
		TJobSetting t = new TJobSetting();
		t.rvcmail = e.rcvmail;
		t.testmail = e.testmail;
		t.resmail = e.resmail;
		t.rejectmail = e.rejectmail;
		t.sendresmail = e.sendresmail;
		t.sendrejectmail = e.sendrejectmail;
		t.peng = e.peng;
		t.piq = e.piq;
		t.neng = e.neng;
		t.niq = e.niq;
		t.lmtime = e.lmtime;
		return t;
	}

	public static EJobSetting convert(TJobSetting e)
	{
		EJobSetting t = new EJobSetting();
		t.rcvmail = e.rvcmail;
		t.testmail = e.testmail;
		t.resmail = e.resmail;
		t.rejectmail = e.rejectmail;
		t.sendresmail = e.sendresmail;
		t.sendrejectmail = e.sendrejectmail;
		t.peng = e.peng;
		t.piq = e.piq;
		t.neng = e.neng;
		t.niq = e.niq;
		t.lmtime = e.lmtime;
		return t;
	}
}