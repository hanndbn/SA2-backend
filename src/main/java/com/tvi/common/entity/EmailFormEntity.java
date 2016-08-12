
package com.tvi.common.entity;

import java.util.Date;

public class EmailFormEntity
{
	public final long id;
	public final String title;
	public final String body;
	public final String signature;
	public final int type;
	public final long creator;
	public final Date ctime;
	public final int state;

	public EmailFormEntity(long id, String title, String body, String signature, int type, long creator, Date ctime, int state)
	{
		this.id = id;
		this.title = title;
		this.body = body;
		this.signature = signature;
		this.type = type;
		this.creator = creator;
		this.ctime = ctime;
		this.state = state;
	}
	
	
}
