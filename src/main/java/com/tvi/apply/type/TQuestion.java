//thanhpk
package com.tvi.apply.type;

import java.util.Calendar;

public class TQuestion
{
	public  long id;
	public  String title;
	public  String choose;
	public Calendar ctime;
	public int type;
	public int weight;

	public TQuestion()
	{}

	public TQuestion(long id, String title, String choose)
	{
		this.id = id;
		this.title = title;
		this.choose = choose;
	}
}
