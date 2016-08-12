package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EComment extends Entity
{
	public String comment;
	public int priority;

	public EComment()
	{

	}

	public EComment(String comment, long creator)
	{
		this.comment=comment;
		this.creator=creator;
	}

	public EComment(long id, Calendar ctime, Calendar lmtime, int state, long creator, String comment)
	{
		super(id, ctime, lmtime, state, creator);
		this.comment = comment;
	}
}
