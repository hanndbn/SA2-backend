package com.tvi.apply.data.entity;

import java.util.Calendar;

public class EJob extends Entity
{
	public String tag;
	public String picture;
	public String cvform;
	public String description;
	public String attachment;
	public int jobstatus;
	public boolean star;
	public Calendar opentime;
	public Calendar closedtime;/* thời gian đóng thực tế */
	public Calendar endtime;/* thời gian đóng dự kiến*/
	public long orgid;
	public int nview;
	public String salary;
	public String interest;
	public String contact;
	public int category;/* partime fulltime intern*/
	public int quantity;
	public String title;
	public int color;
	public EJob()
	{

	}

	public EJob(long id, Calendar ctime, Calendar lmtime, int state, long creator, String tag, String picture, String cvform, String description, String attachment, int jobstatus, boolean star, Calendar opentime, Calendar closedtime, Calendar endtime, long orgid, int nview, String salary, String interest, String contact, int category, int quantity, String title)
	{
		super(id, ctime, lmtime, state, creator);
		this.tag = tag;
		this.picture = picture;
		this.cvform = cvform;
		this.description = description;
		this.attachment = attachment;
		this.jobstatus = jobstatus;
		this.star = star;
		this.opentime = opentime;
		this.closedtime = closedtime;
		this.endtime = endtime;
		this.orgid = orgid;
		this.nview = nview;
		this.salary = salary;
		this.interest = interest;
		this.contact = contact;
		this.category = category;
		this.quantity = quantity;
		this.title = title;
	}
}
