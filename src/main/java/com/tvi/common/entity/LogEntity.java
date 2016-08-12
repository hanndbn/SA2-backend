package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class LogEntity
{

	public final long lid;

	public final long uid;

	public final String url;

	public final Date time;

	public final String param;

	public final int code;

	public final int restime;

	public final long ip;

	public final String useragent;

	public final int cmdtype;

	public LogEntity(long lid, long uid, String url, Date time, String param, int code, int restime, long ip, String useragent, int cmdtype)
	{
		this.lid = lid;
		this.uid = uid;
		this.url = url;
		this.time = time;
		this.param = param;
		this.code = code;
		this.restime = restime;
		this.ip = ip;
		this.useragent = useragent;
		this.cmdtype = cmdtype;
	}

}
