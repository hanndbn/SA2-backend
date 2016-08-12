package com.tvi.apply.data;

import com.tvi.apply.util.database.IDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CVTestDb
{

	private IDatabase database;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public boolean add(long cvid_in, long testid_in)
	{
		boolean sucess = false;
		try
		{
			String sql = "INSERT INTO cvtest(testid,cvid) VALUES(?,?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, testid_in);
			stmt.setLong(2, cvid_in);
			sucess = stmt.execute();
			stmt.close();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public long getCV(long testid_in)
	{
		long cv = -1;
		try
		{
			String sql = "Select cvid from cvtest where testid = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, testid_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				cv = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

		return cv;
	}
}
