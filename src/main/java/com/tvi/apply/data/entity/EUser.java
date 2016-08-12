package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EUser extends Entity
{

	public String fullname;
	public String email;
	public String username;
	public String password;
	public Calendar lastpwchanged;
	public int status;
	public String initial;
	public String avatar;

	public EUser() {}

	public EUser(long id, Calendar ctime, Calendar lmtime, int state, long creator, String fullname, String email, String username, String password, Calendar lastpwchanged, int status, String initial, String avatar)
	{
		super(id, ctime, lmtime, state, creator);
		this.fullname = fullname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.lastpwchanged = lastpwchanged;
		this.status = status;
		this.initial = initial;
		this.avatar = avatar;
	}
}
