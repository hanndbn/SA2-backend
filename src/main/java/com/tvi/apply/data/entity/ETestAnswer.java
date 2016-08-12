

package com.tvi.apply.data.entity;

import java.util.Date;
import java.util.List;

public class ETestAnswer
{
	public final long id;
	public final Date ctime;
	public final List<Long> questions;
	public final List<Integer> answers;
	public final int point;
	public final Date stime;

	public ETestAnswer(long id, Date ctime, List<Long> questions, List<Integer> answers, int point, Date stime)
	{
		this.id = id;
		this.ctime = ctime;
		this.questions = questions;
		this.answers = answers;
		this.point = point;
		this.stime = stime;
	}
	
	
}
