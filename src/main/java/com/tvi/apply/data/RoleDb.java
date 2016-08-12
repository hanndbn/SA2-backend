package com.tvi.apply.data;

import com.tvi.apply.data.entity.ERole;
import com.tvi.apply.util.database.IDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDb
{

	private IDatabase database;
	private long id;
	private String name;

	public RoleDb(IDatabase database)
	{
		this.database = database;
	}

	public List<ERole> getAllRoleOfUser(long userId_in)
	{
		List<ERole> listRole = new ArrayList<ERole>();
		try
		{
			String sql = "select role.rid , role.name from role, membership " + "where role.rid = membership.rid and membership.uid = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, userId_in);
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				id = mRs.getLong(1);
				name = mRs.getString(2);
				listRole.add(new ERole(id, name));
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return listRole;
	}

	public ArrayList<String> actionOfUser(long userId_in)
	{
		ArrayList<String> listAction = new ArrayList<String>();
		List<ERole> listRole = getAllRoleOfUser(userId_in);
		String sql = "SELECT action.name From action,perm,role" + " WHERE role.rid = ? " + "and role.rid = perm.rid " + "and perm.aid = action.aid " + "and action.aid NOT IN (SELECT action.aid FROM negperm,role" + " WHERE role.rid = ? " + "and role.rid = negperm.rid  " + "and action.aid = ne" + ")";

		for (ERole i : listRole)
		{
			try
			{
				PreparedStatement stmt = database.prepareStatement(sql);
				stmt.setLong(1, i.rid);
				stmt.setLong(2, i.rid);
				ResultSet mRs = stmt.executeQuery();
				while (mRs.next())
				{
					listAction.add(mRs.getString(1));
				}
				mRs.close();
				stmt.close();

			} catch (SQLException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return listAction;
	}

	public boolean checkAccess(long userid__in, String action_in)
	{
		boolean sucess = false;
		try
		{
			String sql = "call action (?,?);";
			PreparedStatement stmt = database.prepareCall(sql);
			stmt.setLong(1, userid__in);
			stmt.setString(2, action_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				mRs.getLong(1);
				sucess = true;
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}
}
