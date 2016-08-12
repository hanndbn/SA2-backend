package com.tvi.apply.commontype;

import java.util.Date;

public class Comment
{
	public final Date ctime;

	public final String author;

	public final String comment;

	public Comment(String author, Date ctime, String comment)
	{
		this.author = author;
		this.ctime = ctime;
		this.comment = comment;
	}
	
}


	