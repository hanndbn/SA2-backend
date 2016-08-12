//thanhpk
package com.tvi.apply.data;

import com.tvi.apply.data.entity.EUser;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.*;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class UserDb
{
	private IDatabase database;

	public UserDb(IDatabase database)
	{
		this.database = database;
	}

	public long create(EUser user)
	{
		long sucess = -1;
		try
		{
			String sql = "insert into user(fullname,ctime,state,email,username,password,lastpwchanged,status,initial,avatar,lmtime) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			Calendar curtime = Calendar.getInstance();
			String pw = sha256Hex(user.password);
			ParamHelper.create(stmt)
					.set(user.username)
					.set(curtime)
					.set(user.state)
					.set(user.email)
					.set(user.username)
					.set(pw)
					.set(curtime)
					.set(user.status)
					.set(user.initial)
					.set(user.avatar)
					.set(curtime);
			//System.out.println(stmt);
			stmt.executeUpdate();
			ResultSet mRs = stmt.getGeneratedKeys();
			if (mRs.next())
			{
				sucess = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public EUser read(long uid)
	{
		EUser tmpUser = new EUser();
		try
		{
			String strSQL = "select fullname,ctime,state,email,username,password,lastpwchanged,status,initial,avatar,lmtime from user where id = ?";
			PreparedStatement stmt = database.prepareStatement(strSQL);
			stmt.setLong(1, uid);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ResultHelper rh = new ResultHelper(mRs);
				tmpUser.id = uid;
				tmpUser.fullname = rh.s();
				tmpUser.ctime = rh.t();
				tmpUser.state = rh.i();
				tmpUser.email = rh.s();
				tmpUser.username = rh.s();
				tmpUser.password = rh.s();
				tmpUser.lastpwchanged = rh.t();
				tmpUser.status = rh.i();
				tmpUser.initial = rh.s();
				tmpUser.avatar = rh.s();
				tmpUser.lmtime = rh.t();
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return tmpUser;
	}

	public void update(EUser user)
	{
		try
		{

			String sql = "update user set fullname=?,ctime=?,state=?,email=?,username=?,status=?,initial=?,avatar=?,lmtime=? where id=? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			Calendar curtime = Calendar.getInstance();
			ParamHelper.create(stmt).set(user.username).set(user.ctime).set(user.state).set(user.email).set(user.username).set(user.status).set(user.initial).set(user.avatar).set(curtime).set(user.id);
			stmt.execute();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public long count(String keyword)
	{
		long count;
		try
		{
			String strSQL = "count(id) from user where fullname like %?% or initial like ? or email like ? or username like ? order by ctime desc";
			PreparedStatement stmt = database.prepareStatement(strSQL);

			ParamHelper.create(stmt).set("%" + keyword + "%").set("%" +keyword+"%").set("%"+keyword+"%").set("%"+keyword+"%");
			ResultSet mRs = stmt.executeQuery();
			mRs.next();
			count = mRs.getLong(1);

			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count;
	}

	public long matchUser(String username)
	{
		long ret = -1;
		try
		{
			String strSQL = "select id from user where username = ?";
			PreparedStatement stmt = database.prepareStatement(strSQL);
			stmt.setString(1, username);
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

	public List<EUser> list(String keyword, int n, int p)
	{
		List<EUser> ret = new ArrayList<EUser>();
		long count;
		try
		{
			String strSQL = "select id, fullname,ctime,state,email,username,password,lastpwchanged,status,initial,avatar,lmtime from user where fullname like ? or initial like ? or email like ? or username like ? order by ctime desc limit ?,?";
			PreparedStatement stmt = database.prepareStatement(strSQL);
			ParamHelper.create(stmt).set("%"+keyword+"%").set("%"+keyword+"%").set("%"+keyword+"%").set("%"+keyword+"%").set(p * n).set(n);
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				EUser tmpUser = new EUser();
				ResultHelper rh = new ResultHelper(mRs);
				tmpUser.id = rh.l();
				tmpUser.fullname = rh.s();
				tmpUser.ctime = rh.t();
				tmpUser.state = rh.i();
				tmpUser.email = rh.s();
				tmpUser.username = rh.s();
				tmpUser.password = rh.s();
				tmpUser.lastpwchanged = rh.t();
				tmpUser.status = rh.i();
				tmpUser.initial = rh.s();
				tmpUser.avatar = rh.s();
				tmpUser.lmtime = rh.t();

				ret.add(tmpUser);
			}

			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return ret;
	}

	public long checkUP(String username, String password)
	{
		long sucess = -1;
		try {
			//byte[] hasedpassword = DigestUtils.getSha256Digest().digest(password.getBytes());
			String pw = sha256Hex(password);
			//String pw = new String(hasedpassword);
			String sql = "select id from user where username=? and password=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, pw);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next()) {
				sucess = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return sucess;
	}
}


