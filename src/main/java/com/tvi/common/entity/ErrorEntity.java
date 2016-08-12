package com.tvi.common.entity;

@Deprecated
public class ErrorEntity
{

	public final long eid;

	public final int state;

	public final long lid;

	public final String message;

	public ErrorEntity(long eid, int state, long lid, String message)
	{
		this.eid = eid;
		this.state = state;
		this.lid = lid;
		this.message = message;
	}

}
