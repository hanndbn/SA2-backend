package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EActionLog
{
	public long id;
	public Calendar ctime;
	//final long uid;
	public String action;
	public String subject;
	public String tag;

	public EActionLog()
	{

	}

	public EActionLog(long id, Calendar ctime,/* long uid,*/ String action, String subject, String tag)
	{
		this.id = id;
		this.ctime = ctime;
		//this.uid = uid;
		this.action = action;
		this.subject = subject;
		this.tag = tag;
	}
}
