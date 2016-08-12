

package com.tvi.apply.commontype;

import java.util.List;

public class CTest
{
	public final String answercode;
	public final List<Question> question;
	public final long sec;

	public CTest( String answercode, List<Question> question, long sec)
	{
		this.answercode = answercode;
		this.question = question;
		this.sec = sec;
	}
	
	
}
