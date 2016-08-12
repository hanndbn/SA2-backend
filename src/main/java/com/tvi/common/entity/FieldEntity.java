package com.tvi.common.entity;

import java.util.Date;
@Deprecated
public class FieldEntity
{

	public final long cvfid;

	public final String title;

	public final int state;

	public final Date ctime;

	public final long creator;

	public FieldEntity(long id, String title,  int state, Date ctime, long creator)
	{
		this.cvfid = id;
		this.title = title;
		this.state = state;
		this.ctime = ctime;
		this.creator = creator;
	}

}
