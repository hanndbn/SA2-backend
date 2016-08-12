package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EField extends Entity
{

	public String name;

	public EField()
	{

	}

	public EField(long id, Calendar ctime, Calendar lmtime, int state, long creator, String name)
	{
		super(id, ctime, lmtime, state, creator);
		this.name = name;
	}


}
