package com.tvi.apply.util.time;

public class Watch
{
	public long starttime;
	public long endtime;
	public long delta;

	public Watch()
	{
		starttime = System.currentTimeMillis();
	}

	public void stop()
	{
		endtime = System.currentTimeMillis();
		delta = endtime - starttime;
	}
}
