package com.tvi.common.entity;

@Deprecated
public class ActionEntity
{

	public final long aid;

	public final String name;

	public final String description;

	public ActionEntity(long aid_in, String name_in, String description_in)
	{
		this.aid = aid_in;
		this.name = name_in;
		this.description = description_in;
	}

}
