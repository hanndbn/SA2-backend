//thanhpk
package com.tvi.apply.data;

import com.tvi.apply.util.database.IDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestAccessCodeDb
{
	private IDatabase database;

	public TestAccessCodeDb(IDatabase database)
	{
		this.database = database;
	}

	public long getTestByTestCode(String testCode_in)
	{
		long sucess = -1;
		try
		{
			String sql = "select testid from testaccesscode where code = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setString(1, testCode_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				sucess = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public String getTestCodeByTestId(long testid_in)
	{
		String sucess = null;
		try
		{
			String sql = "select code from testaccesscode where testid = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, testid_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				sucess = mRs.getString(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		if (sucess != null && sucess.length() > 68)
		{
			return sucess.substring(0, 68);
		}
		return sucess;
	}

	public boolean deleteTestCode(String testcode_in)
	{
		boolean sucess = false;
		try
		{
			String sql = "delete from testaccesscode where code=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setBytes(1, testcode_in.getBytes());
			stmt.execute();
			sucess = true;

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}
}
