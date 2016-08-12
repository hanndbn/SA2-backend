package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EInfo extends Entity
{
	public  int fid;
	public  String value;

	public EInfo()
	{

	}

	public EInfo(long id, Calendar ctime, Calendar lmtime, int state, long creator, int fid, String value)
	{
		super(id, ctime, lmtime, state, creator);
		this.fid = fid;
		this.value = value;
	}
}
