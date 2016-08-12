//thanhpk
package com.tvi.apply.util.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class ResultHelper
{
	ResultSet rs;
	int i = 1;

	public ResultHelper(ResultSet rs)
	{
		this.rs = rs;
	}

	public long l()
	{
		i++;
		try
		{
			return rs.getLong(i - 1);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public boolean b()
	{
		i++;
		try
		{
			return rs.getBoolean(i - 1);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Calendar t()
	{
		i++;
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(rs.getTimestamp(i - 1).getTime());
			return cal;
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public int i()
	{
		i++;
		try
		{
			return rs.getInt(i - 1);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String s()
	{
		i++;
		try
		{
			return rs.getString(i - 1);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

}
