//thanhpk
package com.tvi.apply.business;

import com.tvi.apply.util.database.IDatabase;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth implements IAuth
{

	private final IDatabase database;

	public Auth(IDatabase database)
	{
		this.database = database;
	}

	/*
	 Trả về id của người dùng đã đăng nhập tương ứng với auth,
	 nếu ko có người dùng nào thỏa mãn, trả về -1
	 */
	@Override
	public long matchAuth(String auth)
	{

		String query = "{call getUserLoggedIn(?)}";
		CallableStatement prepareCall = database.prepareCall(query);
		try
		{
			prepareCall.setString(1, auth);

			ResultSet rs = prepareCall.executeQuery();
			if (false == rs.next())
			{
				return -1;
			}

			return rs.getLong(1);
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public long matchUser(String username)
	{
		PreparedStatement q = database.prepareStatement("select * from User where username = ?");
		try
		{
			q.setString(1, username);

			ResultSet rs = q.executeQuery();

			if (rs.next())
			{
				long id = rs.getLong("uid");
				return id;
			}
			return -1;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String login(long userid)
	{

		String query = "{call login(?)}";
		CallableStatement prepareCall = database.prepareCall(query);
		try
		{
			prepareCall.setLong(1, userid);
			ResultSet rs;
			rs = prepareCall.executeQuery();
			if (rs.next())
			{
				String auth = rs.getString(1);
				return auth;
			}
			return null;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void logout(String auth)
	{
		String query = "{call logout(?)}";
		CallableStatement prepareCall = database.prepareCall(query);
		try
		{
			prepareCall.setString(1, auth);
			prepareCall.execute();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}


	}
}
