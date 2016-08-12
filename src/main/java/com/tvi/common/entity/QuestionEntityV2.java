package com.tvi.common.entity;

import java.util.Date;


public class QuestionEntityV2
{
	public final long qid;

	public final Date ctime;

	public final int state;

	public final String content;

	public final String choose;

	public final int answer;

	public final int weight;

	public final long creator;

	public final boolean isdraf;
	
	public final int type;

	public QuestionEntityV2(long qid, int type, Date ctime, int state, String content, String choose, int answer, int weight, long creator, boolean isdraf)
	{
		this.type = type;
		this.qid = qid;
		this.ctime = ctime;
		this.state = state;
		this.content = content;
		this.choose = choose;
		this.answer = answer;
		this.weight = weight;
		this.creator = creator;
		this.isdraf = isdraf;
	}

}
