package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.RequestEntityV2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Manh
 */
public class RequestDAOV2
{

	private IDatabase database;
	private long id;
	private String jobdesc;
	private String title;
	private String position;
	private String interest;
	private String requirement;
	private long creator;
	private Date ctime;
	private long cvfid;
	private int quantity;
	private int state;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public long add(int unit_in, RequestEntityV2 request_in)
	{
		long sucess = -1;
		try
		{

			String strSQL = "INSERT INTO Request(jobdesc,title,position,interest,requirement,creator,ctime,unitid,quantity,state) VALUES(?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement stmt = database.preparedStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, request_in.jobdesc);
			stmt.setString(2, request_in.title);
			stmt.setString(3, request_in.position);
			stmt.setString(4, request_in.interest);
			stmt.setString(5, request_in.requirement);
			stmt.setLong(6, request_in.creator);
			stmt.setLong(7, (new java.util.Date().getTime()));
			stmt.setInt(8, unit_in);
			stmt.setInt(9, request_in.quantity);
			stmt.setInt(10, request_in.state);
			stmt.execute();
			ResultSet mRs = stmt.getGeneratedKeys();
			if (mRs.next())
			{
				sucess = mRs.getLong(1);
			}
			stmt.close();
			mRs.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	public void update(long requestid_in, RequestEntityV2 request_in)
	{
		try
		{
			int uid = -1;
			String sql2 = "SELECT unitid From request where rid = ? and state != ? and ctime = (SELECT max(ctime) from request where rid = ? and state != ?) ;";
			PreparedStatement stmt2 = database.prepareStatement(sql2);
			stmt2.setLong(1, requestid_in);
			stmt2.setInt(2, ConfigCount.DELETED);
			stmt2.setLong(3, requestid_in);
			stmt2.setInt(4, ConfigCount.DELETED);
			ResultSet mRs2 = stmt2.executeQuery();
			if (mRs2.next())
			{
				uid = mRs2.getInt(1);
			} else
			{
				throw new RuntimeException();
			}
			mRs2.close();
			stmt2.close();

			String sql1 = "UPDATE Request SET state = ? where rid = ? ";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setInt(1, ConfigCount.DELETED);
			stmt1.setLong(2, requestid_in);
			stmt1.execute();
			stmt1.close();

			String sql = "INSERT INTO Request(rid,jobdesc,title,position,interest,requirement,creator,ctime,unitid,quantity,state) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, requestid_in);
			stmt.setString(2, request_in.jobdesc);
			stmt.setString(3, request_in.title);
			stmt.setString(4, request_in.position);
			stmt.setString(5, request_in.interest);
			stmt.setString(6, request_in.requirement);
			stmt.setLong(7, request_in.creator);
			stmt.setLong(8, (new java.util.Date().getTime()));
			stmt.setInt(9, uid);
			stmt.setInt(10, request_in.quantity);
			stmt.setInt(11, request_in.state);
			stmt.execute();
			stmt.close();

		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void delete(long requestid_in)
	{
		CVDAOV2 cvdaov2_in = new CVDAOV2();
		cvdaov2_in.setDatabase(database);
		BagDAOV2 bagDAOV2_in = new BagDAOV2();
		bagDAOV2_in.setDatabase(database);
		try
		{
			// change state = -1
			String sql1 = "UPDATE Request SET state = ? where rid = ?";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setInt(1, ConfigCount.DELETED);
			stmt1.setLong(2, requestid_in);
			stmt1.execute();
			stmt1.close();

			// change state All field follow
			long bid = -1;
			String sql2 = "Select bid from bag where request = ?";
			PreparedStatement stmt2 = database.prepareStatement(sql2);
			stmt2.setLong(1, requestid_in);
			ResultSet mRs2 = stmt2.executeQuery();
			while (mRs2.next())
			{
				bid = mRs2.getLong(1);
				String sql3 = "Select cvid from cv where bagid = ?";
				PreparedStatement stmt3 = database.prepareStatement(sql3);
				stmt3.setLong(1, bid);
				ResultSet mRs3 = stmt3.executeQuery();
				while (mRs3.next())
				{
					cvdaov2_in.detele(mRs3.getLong(1));
				}
				bagDAOV2_in.delete(bid);
			}

		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public RequestEntityV2 getRequest(long requestid_in)
	{
		RequestEntityV2 requestEntityV2 = null;
		try
		{
			String sql = "SELECT rid,jobdesc,title,position,interest" + ",requirement,creator,ctime,quantity,state" + " FROM Request Where rid = ? and state != ? and " + "ctime = (Select max(ctime) from request where rid = ? and state != ?)";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, requestid_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setLong(3, requestid_in);
			stmt.setInt(4, ConfigCount.DELETED);

			ResultSet mRs = stmt.executeQuery();

			if (mRs.next())
			{
				id = mRs.getLong(1);
				jobdesc = mRs.getString(2);
				title = mRs.getString(3);
				position = mRs.getString(4);
				interest = mRs.getString(5);
				requirement = mRs.getString(6);
				creator = mRs.getLong(7);
				ctime = new java.util.Date(mRs.getLong(8));
				quantity = mRs.getInt(9);
				state = mRs.getInt(10);
				requestEntityV2 = new RequestEntityV2(id, jobdesc, title, position, interest, requirement, creator, ctime, quantity, state);
			}
			mRs.close();
			stmt.close();

		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return requestEntityV2;

	}

	public List<RequestEntityV2> listByUnit(int unit_in, int p_in, int ps_in, String[] s_in)
	{
		List<RequestEntityV2> list = new ArrayList<RequestEntityV2>();

		try
		{
			PreparedStatement stmt = null;
			String sql = "SELECT rid,jobdesc,title,position,interest" + ",requirement,creator,ctime,quantity,state" + " FROM Request Where unitid = ? and state != ? ";

			if (s_in == null || s_in.length < 1)
			{
				sql = sql + " limit ?,? ; ";
				stmt = database.prepareStatement(sql);
				stmt.setLong(1, unit_in);
				stmt.setInt(2, ConfigCount.DELETED);
				stmt.setInt(3, p_in * ps_in);
				stmt.setInt(4, ps_in);

			} else
			{
				sql = sql + " order by ";
				for (int i = 0; i < s_in.length - 1; i++)
				{
					sql = sql + StringEscapeUtils.escapeSql(s_in[i].replaceAll("[^\\w]", "")) + " desc , ";

				}
				sql = sql + StringEscapeUtils.escapeSql(s_in[s_in.length - 1].replaceAll("[^\\w]", "")) + " desc " + " limit ?,? ; ";
				stmt = database.prepareStatement(sql);
				stmt.setLong(1, unit_in);
				stmt.setInt(2, ConfigCount.DELETED);
				stmt.setInt(3, p_in * ps_in);
				stmt.setInt(4, ps_in);
			}
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				id = mRs.getLong(1);
				jobdesc = mRs.getString(2);
				title = mRs.getString(3);
				position = mRs.getString(4);
				interest = mRs.getString(5);
				requirement = mRs.getString(6);
				creator = mRs.getLong(7);
				ctime = new java.util.Date(mRs.getLong(8));
				quantity = mRs.getInt(9);
				state = mRs.getInt(10);
				list.add(new RequestEntityV2(id, jobdesc, title, position, interest, requirement, creator, ctime, quantity, state));
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return list;
	}

	public int getUnit(long requestid_in)
	{
		int unit = -1;
		try
		{
			String sql = "SELECT unitid " + " FROM Request Where rid = ? and state != ?";

			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, requestid_in);
			stmt.setInt(2, ConfigCount.DELETED);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				unit = mRs.getInt(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return unit;
	}

	public long countRequestByUnit(int unit_in)
	{
		long count = -1;
		try
		{
			String sql = "Select count(rid) From request where unitid = ? and state != ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, unit_in);
			stmt.setInt(2, ConfigCount.DELETED);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				count = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}

	public long countOpenRequest(String k)
	{
		if (k == null) k = "";
		long count = -1;
		k = "%" + k.replace("%", "[%]") + "%";

		try
		{
			String sql = "Select count(rid) From request where state = 0 and (title like ? or position like ? or interest like ? or requirement like ? or jobdesc like ?)";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setString(1, k);
			stmt.setString(2, k);
			stmt.setString(3, k);
			stmt.setString(4, k);
			stmt.setString(5, k);

			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				count = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}

	public List<RequestEntityV2> listOpenRequest(int p, int ps, String k, String[] s) throws SQLException
	{
		if (k == null) k = "";
		List<RequestEntityV2> list = new ArrayList<RequestEntityV2>();
		k = "%" + k.replace("%", "[%]") + "%";
		PreparedStatement stmt = null;
		String sql = "Select rid,jobdesc,title,position,interest,requirement,creator,ctime,quantity,state From request where state = 0 and (title like ? or position like ? or interest like ? or requirement like ? or jobdesc like ?)";

		if (s == null || s.length < 1)
		{
		} else
		{
			sql = sql + " order by ";
			for (int i = 0; i < s.length - 1; i++)
			{
				sql = sql + StringEscapeUtils.escapeSql(s[i].replaceAll("[^\\w]", "")) + " desc , ";
			}
			sql = sql + StringEscapeUtils.escapeSql(s[s.length - 1].replaceAll("[^\\w]", "")) + " desc ";
		}
		sql = sql + " limit ?,? ; ";
		stmt = database.prepareStatement(sql);
		stmt.setString(1, k);
		stmt.setString(2, k);
		stmt.setString(3, k);
		stmt.setString(4, k);
		stmt.setString(5, k);
		stmt.setInt(6, p * ps);
		stmt.setInt(7, ps);

		ResultSet mRs = stmt.executeQuery();
		while (mRs.next())
		{
			id = mRs.getLong(1);
			jobdesc = mRs.getString(2);
			title = mRs.getString(3);
			position = mRs.getString(4);
			interest = mRs.getString(5);
			requirement = mRs.getString(6);
			creator = mRs.getLong(7);
			ctime = new java.util.Date(mRs.getLong(8));
			quantity = mRs.getInt(9);
			state = mRs.getInt(10);
			list.add(new RequestEntityV2(id, jobdesc, title, position, interest, requirement, creator, ctime, quantity, state));
		}
		mRs.close();
		stmt.close();

		return list;

	}
}
