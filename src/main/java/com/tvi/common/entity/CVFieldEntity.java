

package com.tvi.common.entity;

import java.util.Date;
@Deprecated
public class CVFieldEntity
{
	public final long id;
	public final String title;
	public final int state;
	public final Date ctime;
	public final long creator;

	public CVFieldEntity(long id, String title, int state, Date ctime, long creator)
	{
		this.id = id;
		this.title = title;
		this.state = state;
		this.ctime = ctime;
		this.creator = creator;
	}
}
