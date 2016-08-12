//thanhpk
package com.tvi.apply.type;

import java.util.Calendar;

public class TJob
{
	public long id;
	public Calendar ctime;
	public Calendar lmtime;
	public int state;
	public long creator;
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
	public TField[] fields;
	public int color;
	public long ccount;
	public TJobSetting setting;
}
