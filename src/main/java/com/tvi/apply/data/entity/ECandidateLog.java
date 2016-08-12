package com.tvi.apply.data.entity;

import java.util.Calendar;

public class ECandidateLog
{
	public long id;
	public Calendar ctime;
	public String action;
	public String subject;
	public String tag;

	public ECandidateLog()
	{

	}

	public ECandidateLog(String action, String subject, String tag)
	{
		this.action = action;
		this.subject = subject;
		this.tag = tag;
	}

}
