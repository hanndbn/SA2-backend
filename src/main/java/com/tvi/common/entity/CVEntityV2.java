package com.tvi.common.entity;

import java.util.Date;

public class CVEntityV2
{

	public final long id;

	public final Date ctime;

	public final int state;

	public final int level;

	public final String filename;
	
	public final String name;

	public final String email;

	public final boolean cantesteng;
	
	public final boolean cantestiq;
	
	public final boolean iqtested;
	
	public final boolean engtested;

	public final int iq;

	public final int pres;

	public final int arch;

	public final int sum;

	public final int total;

	public final int pote;
	
	public final int eng;

	public final boolean receivedmail;
	
	public final boolean iqtestresultmail;
	
	public final boolean engtestresultmail;

	public CVEntityV2(long id, Date ctime, int state, int level, String filename, String name, String email, boolean cantesteng, boolean cantestiq, boolean iqtested, boolean engtested, int iq, int pres, int arch, int sum, int total, int pote, int eng, boolean receivedmail, boolean iqtestresultmail, boolean engtestresultmail)
	{
		this.id = id;
		this.ctime = ctime;
		this.state = state;
		this.level = level;
		this.filename = filename;
		this.name = name;
		this.email = email;
		this.cantesteng = cantesteng;
		this.cantestiq = cantestiq;
		this.iqtested = iqtested;
		this.engtested = engtested;
		this.iq = iq;
		this.pres = pres;
		this.arch = arch;
		this.sum = sum;
		this.total = total;

		this.pote = pote;
		this.eng = eng;
		this.receivedmail = receivedmail;
		this.iqtestresultmail = iqtestresultmail;
		this.engtestresultmail = engtestresultmail;
	}
	
	
}
