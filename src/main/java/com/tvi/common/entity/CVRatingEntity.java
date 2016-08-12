package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class CVRatingEntity
{

	public final long id;
	public final int iq;

	public final int pres;

	public final int arch;

	public final int sum;

	public final int total;

	public final int rating;

	public final int pote;

	public final int state;

	public final Date ctime;

	public CVRatingEntity(long id, int iq, int pres, int arch, int sum, int total, int rating, int pote, int state, Date ctime)
	{
		this.id = id;
		this.iq = iq;
		this.pres = pres;
		this.arch = arch;
		this.sum = sum;
		this.total = total;
		this.rating = rating;
		this.pote = pote;
		this.state = state;
		this.ctime = ctime;
	}

}
