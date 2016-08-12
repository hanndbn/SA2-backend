package com.tvi.apply.util.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

public class ParamHelper
{
	PreparedStatement st;

	private int i = 1;

	private ParamHelper(PreparedStatement st)
	{
		this.st = st;
	}

	public static ParamHelper create(PreparedStatement st)
	{

		return new ParamHelper(st);
	}

	public ParamHelper set(long value)
	{
		try
		{
			st.setLong(i, value);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		i++;
		return this;
	}

	public ParamHelper set(int value)
	{
		try
		{
			st.setInt(i, value);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		i++;
		return this;
	}

	public ParamHelper set(Calendar value)
	{
		try
		{
			if(value == null)
				value = Calendar.getInstance();


			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(value.getTime());

			st.setString(i, currentTime);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		i++;
		return this;
	}

	public ParamHelper set(String value)
	{
		try
		{
			st.setString(i, value);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		i++;
		return this;
	}

	public ParamHelper set(boolean value)
	{
		try
		{
			st.setBoolean(i, value);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		i++;
		return this;
	}

}
