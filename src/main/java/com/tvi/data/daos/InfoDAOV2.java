/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.entity.InfoEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Manh
 */
public class InfoDAOV2
{

	private IDatabase database;

	private long id;

	private long fieldid;

	private String info;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public List<InfoEntity> listInfobyCv(long cvid_in)
	{

		List<InfoEntity> listInfo = new ArrayList<InfoEntity>();
		try
		{
			String strSQL = "SELECT iid,field,info FROM Info where cvid = ? ;";
			PreparedStatement stmt = database.prepareStatement(strSQL);
			stmt.setLong(1, cvid_in);
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				id = mRs.getLong(1);
				fieldid = mRs.getLong(2);
				info = mRs.getString(3);
				listInfo.add(new InfoEntity(id, fieldid, info));
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(InfoDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listInfo;
	}

	public long add(long cvid_in, InfoEntity info_in)
	{
		long iid = -1;
		try
		{
			String sql = "INSERT INTO Info(field,info,cvid) VALUES(?,?,?);";
			PreparedStatement stmt = database.preparedStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, info_in.fieldid);
			stmt.setString(2, info_in.info);
			stmt.setLong(3, cvid_in);
			stmt.execute();
			ResultSet mRs = stmt.getGeneratedKeys();
			if (mRs.next())
			{
				iid = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(InfoDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return iid;


	}

}
