package com.tvi.common.entity;

import java.util.Date;
@Deprecated
public class UserEntity
{
	public final long uid;

	public final String fullname;

	public final Date ctime;

	public final int state;

	public final int unit;

	public UserEntity(long uid, String fullname, Date ctime, int state, int unit)
	{
		this.uid = uid;
		this.fullname = fullname;
		this.ctime = ctime;
		this.state = state;
		this.unit = unit;
	}

}
