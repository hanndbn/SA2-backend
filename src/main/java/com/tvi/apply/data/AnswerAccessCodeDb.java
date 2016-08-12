package com.tvi.apply.data;

import com.tvi.apply.util.database.IDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnswerAccessCodeDb
{
	private IDatabase database;

	public AnswerAccessCodeDb(IDatabase database)
	{
		this.database = database;
	}

	public long getAnswerByAnswerCode(String answercode)
	{
		long ret = -1;
		try
		{
			String sql = "select taid from answeraccesscode where code = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setString(1, answercode);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ret = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return ret;
	}

	public String getAnswerCodeByAnswerId(long answerid)
	{
		String ret = null;
		try
		{
			String sql = "select code from answeraccesscode where taid = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, answerid);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ret = mRs.getString(1);
			}
			mRs.close();
			stmt.close();
			if (ret != null && ret.length() > 68)
			{
				return ret.substring(0, 68);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

		return ret;
	}

	public void deleteAnswerCode(String answercode)
	{
		boolean sucess = false;
		try
		{
			String sql = "delete from answeraccesscode where code=?;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setString(1, answercode);
			stmt.execute();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
