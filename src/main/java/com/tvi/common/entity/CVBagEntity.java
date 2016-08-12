
package com.tvi.common.entity;

import java.util.Date;

public class CVBagEntity
{
	public final String name;
	public final long id;
	public final int state;
	public final Date ctime;
	public final long creator;

	public CVBagEntity(String name, long id, int state,  Date ctime, long creator)
	{
		this.name = name;
		this.id = id;
		this.state = state;
		this.ctime = ctime;
		this.creator = creator;
	}
}
