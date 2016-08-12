package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class CVEntity
{

	public final long id;

	public final Date ctime;

	public final int state;

	public final long rid;

	public final int level;

	public final boolean canretest;

	public final boolean forwarded;

	public final String filename;
	
	public final String name;

	public final String email;

	public CVEntity(long cvid_in, Date ctime_in, int state_in, long rid_in, int level_in, boolean canretest_in, boolean forwarded_in, String filename, String name_in, String email_in)
	{
		this.id = cvid_in;
		this.ctime = ctime_in;
		this.state = state_in;
		this.rid = rid_in;
		this.level = level_in;
		this.canretest = canretest_in;
		this.forwarded = forwarded_in;
		this.filename = filename;
		this.name = name_in;
		this.email = email_in;
	}

}
