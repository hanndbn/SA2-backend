package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class RequestEntity
{

	public final long id;

	public final int unit;

	public final String jobdesc;

	public final String title;

	public final String position;

	public final String interest;

	public final String requirement;

	public final long creator;

	public final Date ctime;

	public final long cvfid;

	public final int quantity;

	public final int state;

	public RequestEntity(long rid, int unit, String jobdesc, String title, String position, String interest, String requirement, long creator, Date ctime, long cvfid, int quantity, int state)
	{
		this.id = rid;
		this.unit = unit;
		this.jobdesc = jobdesc;
		this.title = title;
		this.position = position;
		this.interest = interest;
		this.requirement = requirement;
		this.creator = creator;
		this.ctime = ctime;
		this.cvfid = cvfid;
		this.quantity = quantity;
		this.state = state;
	}

}
