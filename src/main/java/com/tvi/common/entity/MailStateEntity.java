

package com.tvi.common.entity;

import java.util.Date;

public class MailStateEntity
{
	public final long id;
	public final boolean receivedmail;
	public final boolean iqtestresult;
	public final boolean engtestresult;
	public final Date ctime;

	public MailStateEntity(long id, boolean receivedmail, boolean iqtestresult, boolean engtestresult, Date ctime)
	{
		this.id = id;
		this.receivedmail = receivedmail;
		this.iqtestresult = iqtestresult;
		this.engtestresult = engtestresult;
		this.ctime = ctime;
	}
	
	
	
}
