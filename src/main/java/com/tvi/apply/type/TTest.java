

package com.tvi.apply.type;

import java.util.List;

public class TTest
{
	public final String answercode;
	public final List<TQuestion> question;
	public final long sec;

	public TTest(String answercode, List<TQuestion> question, long sec)
	{
		this.answercode = answercode;
		this.question = question;
		this.sec = sec;
	}
	
	
}
