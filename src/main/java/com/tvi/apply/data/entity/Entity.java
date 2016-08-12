package com.tvi.apply.data.entity;

import java.util.Calendar;

public class Entity
{
	public  long id;
	public  Calendar ctime;
	public  Calendar lmtime;
	public  int state;
	public  long creator;

	public Entity(){}
	public Entity(long id, Calendar ctime, Calendar lmtime, int state, long creator)
	{
		this.id = id;
		this.ctime = ctime;
		this.lmtime = lmtime;
		this.state = state;
		this.creator = creator;
	}
}
