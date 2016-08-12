package com.tvi.common.entity;

import java.util.Date;

@Deprecated
public class QuestionEntity
{
	public final long qid;

	public final Date ctime;

	public final int state;

	public final String content;

	public final String choose;

	public final int answer;

	public final int weight;

	public final long creator;

	public final int isdraf;

	public QuestionEntity(long qid, Date ctime, int state, String content, String choose, int answer, int weight, long creator, int isdraf)
	{
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
