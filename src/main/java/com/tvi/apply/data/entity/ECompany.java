package com.tvi.apply.data.entity;

import java.util.Calendar;

public class ECompany extends  Entity
{
	public String email;
	public String password;
	public String incoming;
	public String outgoing;
	//final name

	public ECompany()
	{

	}

	public ECompany(long id, Calendar ctime, Calendar lmtime, int state, long creator, String email, String password, String incoming, String outgoing)
	{
		super(id, ctime, lmtime, state, creator);
		this.email = email;
		this.password = password;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}
}
