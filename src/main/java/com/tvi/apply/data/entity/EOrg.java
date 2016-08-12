package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EOrg extends Entity
{
	public String picture;
	public String name;
	public String description;

	public EOrg() {}

	public EOrg(long id, Calendar ctime, Calendar lmtime, int state, int creator, String picture, String name, String description)
	{
		super(id, ctime, lmtime, state, creator);
		this.picture = picture;
		this.name = name;
		this.description = description;
	}
}
