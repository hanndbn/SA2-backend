package com.tvi.common.entity;


import java.util.Date;

public class CVCommentEntity
{
	public final long id;

	public final Date ctime;

	public final long author;

	public final int state;

	public final String comment;

	public CVCommentEntity(long id, Date ctime, long author, int state, String comment)
	{
		this.id = id;
		this.ctime = ctime;
		this.author = author;
		this.state = state;
		this.comment = comment;
	}

}
