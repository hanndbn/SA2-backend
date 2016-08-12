package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EAccessLog
{
	//final long uid;
	public String url;
	public Calendar time;
	public String param;
	public int code;
	public int restime;
	public String ip;
	public String agent;
	public int size;


	public EAccessLog()
	{

	}

	public EAccessLog(/*long uid,*/ String url, Calendar time, String param, int code, int restime, String ip, String agent, int size) {
	//	this.uid = uid;
		this.url = url;
		this.time = time;
		this.param = param;
		this.code = code;
		this.restime = restime;
		this.ip = ip;
		this.agent = agent;
		this.size = size;
	}
}
