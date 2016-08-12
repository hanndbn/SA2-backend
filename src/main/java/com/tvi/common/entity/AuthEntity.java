package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class AuthEntity
{

	public final long uid;

	public final String auth;

	public final Date lastused;

	public AuthEntity(long uid_in, String auth_in, Date /* kieu time */ lastused_in)
	{
		this.uid = uid_in;
		this.auth = auth_in;
		this.lastused = lastused_in;
	}

}
