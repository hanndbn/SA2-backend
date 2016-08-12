package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EEmail
{
	public  long id;
	public  String messageid;
	public  Calendar ctime;
	public  String from;
	public  String to;
	public  String cc;
	public  String bcc;
	public  String subject;
	public  Calendar sendtime;
	public  int state;
	public  boolean junk;
	public  int status;
	public  long assignby;
	public  Calendar quanlifieddate;
	public  long quanlifiedby;
	public  String body;
	public  String[] attachment;
	public long candidateid;

	public EEmail()
	{

	}

	public EEmail(long id, String messageid, Calendar ctime, String from, String to, String cc, String bcc, String subject, Calendar sendtime, int state, boolean junk, int status, long assignby, Calendar quanlifieddate, long quanlifiedby, String body, String[] attachment)
	{
		this.id = id;
		this.messageid = messageid;
		this.ctime = ctime;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.sendtime = sendtime;
		this.state = state;
		this.junk = junk;
		this.status = status;

		this.assignby = assignby;
		this.quanlifieddate = quanlifieddate;
		this.quanlifiedby = quanlifiedby;
		this.body = body;
		this.attachment = attachment;
	}
}
