package com.tvi.common.entity;

import java.util.Date;
@Deprecated
public class EmailSendStateEntity
{
	public final long id;
	public boolean cvreceivedmail;
	public boolean testresultmail;
	public boolean rejectedmail;
	public Date ctime;
	public int state;

	public EmailSendStateEntity(long id, boolean cvreceivedmail, boolean testresultmail, boolean rejectedmail)
	{
		this.id = id;
		this.cvreceivedmail = cvreceivedmail;
		this.testresultmail = testresultmail;
		this.rejectedmail = rejectedmail;
	}
	
	
}
