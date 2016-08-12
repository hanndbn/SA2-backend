

package com.tvi.apply.commontype;

import java.util.Date;

public class CRequest
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
	
	private final long totalcv;
	
	private final long newcv;

	public CRequest(long rid, String jobdesc, String title, String position, String interest, String requirement, long creator, Date ctime,  int quantity, int state, long toalcv, long newcv)
	{
		this.totalcv = toalcv;
		this.newcv = newcv;
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
