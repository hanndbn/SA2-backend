package com.tvi.apply.util.log;

import java.util.Calendar;

public class Log
{
	final String url;
	final Calendar time;
	final String param;
	final int code;
	final int restime;
	final String ip;
	final String agent;
	final int size;

	public Log(String url, Calendar time, String param, int code, int restime, String ip, String agent, int size)
	{
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
