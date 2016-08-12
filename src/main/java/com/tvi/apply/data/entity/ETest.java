package com.tvi.apply.data.entity;

import java.util.Date;

public class ETest
{

	public final long tid;

	public final Date ctime;

	public final int type;

	public final int state;

	public ETest(long tid, Date ctime, int type, int state)
	{
		this.tid = tid;
		this.ctime = ctime;
		this.type = type;
		this.state = state;
	}

}
