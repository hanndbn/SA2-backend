
package com.tvi.common.entity;

import java.util.Date;

public class RequestEntityV2
{
	public final long id;

	public final String jobdesc;

	public final String title;

	public final String position;

	public final String interest;

	public final String requirement;

	public final long creator;

	public final Date ctime;

	public final int quantity;

	public final int state;

	public RequestEntityV2(long rid, String jobdesc, String title, String position, String interest, String requirement, long creator, Date ctime,  int quantity, int state)
	{
		this.id = rid;
		this.jobdesc = jobdesc;
		this.title = title;
		this.position = position;
		this.interest = interest;
		this.requirement = requirement;
		this.creator = creator;
		this.ctime = ctime;
		this.quantity = quantity;
		this.state = state;
	}

}
