

package com.tvi.common.entity;

import java.util.Date;


public class UserEntityV2
{
	public final long id;

	public final String fullname;

	public final Date ctime;

	public final int state;

	public UserEntityV2(long id, String fullname, Date ctime, int state)
	{
		this.id = id;
		this.fullname = fullname;
		this.ctime = ctime;
		this.state = state;
	}
}
