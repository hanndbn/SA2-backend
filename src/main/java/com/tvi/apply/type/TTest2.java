

package com.tvi.apply.type;

import java.util.Calendar;
import java.util.Date;

public class TTest2 extends TTest
{
	public String fullname;
	public Calendar time;
	public long requestid;
	public String requesttitle;
	
	public TTest2(TTest test, String fullname, Calendar time, long requestid, String requestitle)
	{
		super(test.answercode, test.question, test.sec);
		this.fullname = fullname;
		this.requestid = requestid;
		this.requesttitle = requestitle;
		this.time = time;
	}
	
}
