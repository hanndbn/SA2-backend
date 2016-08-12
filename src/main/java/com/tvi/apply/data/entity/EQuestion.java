package com.tvi.apply.data.entity;

import java.util.Date;


public class EQuestion
{
	public  long qid;
	public  Date ctime;
	public  int state;
	public  String content;
	public  String choose;
	public  int answer;
	public  int weight;
	public  long creator;
	public  boolean isdraf;
	
	public  int type;

	public EQuestion()
	{}

	public EQuestion(long qid, int type, Date ctime, int state, String content, String choose, int answer, int weight, long creator, boolean isdraf)
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
