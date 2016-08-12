

package com.tvi.apply.commontype;

import java.util.Date;

public class CTest2 extends CTest
{
	public final String fullname;
	public final Date time;
	public final long requestid;
	public final String requesttitle;
	
	public CTest2(CTest test, String fullname, Date time, long requestid, String requestitle)
	{
		super(test.answercode, test.question, test.sec);
		this.fullname = fullname;
		this.requestid = requestid;
		this.requesttitle = requestitle;
		this.time = time;
	}
	
}
