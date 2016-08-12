package com.tvi.apply.business.core;

import com.tvi.apply.business.IAuth;
import com.tvi.apply.business.TypeEntityConverter;
import com.tvi.apply.data.ActionLogDb;
import com.tvi.apply.data.RoleDb;
import com.tvi.apply.data.UserDb;
import com.tvi.apply.data.entity.EAccessLog;
import com.tvi.apply.data.entity.EActionLog;
import com.tvi.apply.data.entity.EUser;
import com.tvi.apply.type.TUser;
import com.tvi.apply.util.database.IDatabase;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class UserMgr implements IUserMgt
{

	UserDb userdb;
	RoleDb roledb;
	IDatabase database;
	IAuth auth;
	ActionLogDb actionlogdb;
	final SimpleDateFormat dateformater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public UserMgr(IDatabase database, IAuth auth)
	{

		this.auth = auth;
		this.database = database;
		this.userdb = new UserDb(database);
		this.roledb = new RoleDb(database);
		this.actionlogdb = new ActionLogDb(database);
	}

	@Override
	public List<EAccessLog> listAccessLog(long userid, int n, int p)
	{
		return null;
	}

	@Override
	public long createUser(EUser user)
	{
		return userdb.create(user);
	}

	@Override
	public void editUser(EUser user)
	{
		userdb.update(user);
	}

	@Override
	public long countAccessLog(long uid)
	{
		return 0;
	}

	@Override
	public void logAccess(long userid, EAccessLog entity)
	{
		long reqtime = entity.time.getTimeInMillis();
		String reqt = this.dateformater.format(new Date(reqtime)) + "." + reqtime % 1000;
		String query = String.format("insert into log(ip,uid,time, restime,code,url, useragent,size,param) values('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s', '%8$s', '%9$s');", StringEscapeUtils.escapeSql(entity.ip), userid, reqt, entity.restime, entity.code, StringEscapeUtils.escapeSql(entity.url), StringEscapeUtils.escapeSql(entity.agent), entity.size, StringEscapeUtils.escapeSql(entity.param));

		Statement statement = this.database.createStatement();

		try
		{
			statement.execute(query);
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String login(String username, String password)
	{
		EUser user = getUserByUsername(username);
		String hasedpassword = sha256Hex(password);
		if (hasedpassword.equals(user.password))
		{
			return auth.login(user.id);
		}

		return null;
	}

	@Override
	public void logout(String authcode)
	{
		auth.logout(authcode);
	}

	@Override
	public long matchAuth(String auth)
	{
		return this.auth.matchAuth(auth);
	}

	@Override
	public List<EActionLog> listActionLog(long userid, int n, int p)
	{
		return actionlogdb.listActionLog(userid, n, p);
	}

	@Override
	public long countActionLog(long uid)
	{
		return actionlogdb.countActionLog(uid);
	}

	@Override
	public void logAction(long userid, EActionLog entity)
	{
		actionlogdb.add(userid, entity);
	}

	@Override
	public EUser readUser(long id)
	{
		return userdb.read(id);
	}

	@Override
	public EUser getUserByUsername(String username)
	{
		long id = userdb.matchUser(username);
		return userdb.read(id);
	}

	@Override
	public List<EUser> listUser(String keyword, int n, int p)
	{
		return userdb.list(keyword, n, p);
	}

	@Override
	public long countUser(String keyword)
	{
		return userdb.count(keyword);
	}

	@Override
	public void updateUser(EUser entity)
	{
		userdb.update(entity);
	}

	@Override
	public void access(long userid, String action)
	{
		if (true) return;
		if (!roledb.checkAccess(userid, action)) throw new RuntimeException("không đủ quyền");
	}

	@Override
	public TUser getUser(long userid)
	{
		EUser e = userdb.read(userid);
		return TypeEntityConverter.convert(e);
	}


}
