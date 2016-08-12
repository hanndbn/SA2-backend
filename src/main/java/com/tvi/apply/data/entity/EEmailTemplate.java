package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EEmailTemplate extends Entity
{
	public  String name;
	public  String content;
	public  String attachment;

	public EEmailTemplate()
	{

	}

	public EEmailTemplate(long id, Calendar ctime, Calendar lmtime, int state, long creator, String name, String content, String attachment)
	{
		super(id, ctime, lmtime, state, creator);
		this.name = name;
		this.content = content;
		this.attachment = attachment;
	}
}
