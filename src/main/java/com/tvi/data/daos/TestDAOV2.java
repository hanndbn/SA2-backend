/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.TestEntity;
import com.tvi.common.util.CreateTestV2;
import com.tvi.common.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Manh
 */
public class TestDAOV2
{

	private IDatabase database;
	private long tid;
	private Date ctime;
	private int type;
	private int state;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public TestEntity getTest(long tid_in)
	{

		TestEntity sucess = null;

		try
		{
			String sql = "SELECT ctime,state,type FROM Test where tid = ? and state != ? and ctime = (Select max(ctime) from test where tid = ? and state != ? ) ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, tid_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setInt(4, ConfigCount.DELETED);
			stmt.setLong(3, tid_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ctime = new java.util.Date(mRs.getLong(1));
				state = mRs.getInt(2);
				type = mRs.getInt(3);

				sucess = new TestEntity(tid_in, ctime, type, state);
			}

			mRs.close();
			stmt.close();

		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	public List<TestEntity> getListTest(long cvId_in)
	{
		List<TestEntity> list = new ArrayList<TestEntity>();
		try
		{
			String sql = "SELECT Test.tid,Test.ctime,Test.state,Test.type FROM Test,CVTest where Test.state != ? and Test.tid = CVTest.testid and CVTest.cvid = ? ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(2, cvId_in);
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				tid = mRs.getLong(1);
				ctime = new java.util.Date(mRs.getLong(2));
				state = mRs.getInt(3);
				type = mRs.getInt(4);
				list.add(new TestEntity(tid, ctime, type, state));
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return list;
	}

	private long addTest(TestEntity testEntity_in)
	{
		long sucess = -1;
		try
		{
			String sql = "INSERT INTO Test(ctime,state,type) VALUES(?,?,?);";
			PreparedStatement stmt = database.preparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, (new java.util.Date().getTime()));
			stmt.setInt(2, testEntity_in.state);
			stmt.setInt(3, testEntity_in.type);
			stmt.executeUpdate();
			ResultSet mRs = stmt.getGeneratedKeys();
			if (mRs.next())
			{
				sucess = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();

		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	public long addTestWithTestCode(int unit_in, int type_in, int n_in, int p_in)
	{
		String code = null;
		long tid = -1;
		QuestionInTestDAOV2 questionInTestDAOV2_in = new QuestionInTestDAOV2();
		questionInTestDAOV2_in.setDatabase(database);
		TestAccessCodeV2 testcode = new TestAccessCodeV2();
		testcode.setDatabase(database);
		TestEntity testEntity = new TestEntity(tid, ctime, type_in, ConfigCount.CREATE);
		try
		{
			while (true)
			{
				code = StringUtil.randomNumberString();
				if (testcode.getTestByTestCode(code) == -1)
				{
					break;
				}
			}

			int[] test = checkTest(unit_in, type_in, n_in, p_in, 0, 0, 0);

			if (test == null || test[0] == -1)
			{
				return -1;
			}
			tid = addTest(testEntity);
			String sql = "INSERT INTO Testaccesscode(testid,code) VALUES(?,?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, tid);
			stmt.setString(2, code);
			stmt.execute();
			stmt.close();

			try
			{
				throw new Exception("create test access code ( " + tid + ";" + code);
			} catch (Exception ex)
			{
				Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
			}

			//create weight 1
			if (test[0] > 0)
			{
				String sql2 = "Select qid from question where weight = 1 and unit = ? and type = ? and state != ? ORDER BY RAND() LIMIT 0, ?;";

				PreparedStatement stmt2 = database.prepareStatement(sql2);
				stmt2.setLong(1, unit_in);
				stmt2.setInt(2, type_in);
				stmt2.setInt(3, ConfigCount.DELETED);
				stmt2.setInt(4, test[0]);
				ResultSet mRs2 = stmt2.executeQuery();
				while (mRs2.next())
				{
					questionInTestDAOV2_in.add(tid, mRs2.getLong(1));
				}
				mRs2.close();
				stmt2.close();
			}
			if (test[1] > 0)
			{
				//create weight 2
				String sql3 = "Select qid from question where weight = 2 and unit = ? and  type = ? and  state != ? ORDER BY RAND() LIMIT 0, ?;";

				PreparedStatement stmt3 = database.prepareStatement(sql3);
				stmt3.setLong(1, unit_in);
				stmt3.setInt(2, type_in);
				stmt3.setInt(3, ConfigCount.DELETED);
				stmt3.setInt(4, test[1]);
				ResultSet mRs3 = stmt3.executeQuery();
				while (mRs3.next())
				{
					questionInTestDAOV2_in.add(tid, mRs3.getLong(1));
				}
				mRs3.close();
				stmt3.close();
			}
			if (test[2] > 0)
			{
				//create weight 3
				String sql4 = "Select qid from question where weight = 3 and unit = ? and  type = ? and state != ? ORDER BY RAND() LIMIT 0, ?;";

				PreparedStatement stmt4 = database.prepareStatement(sql4);
				stmt4.setLong(1, unit_in);
				stmt4.setInt(2, type_in);
				stmt4.setInt(3, ConfigCount.DELETED);
				stmt4.setInt(4, test[2]);
				ResultSet mRs4 = stmt4.executeQuery();
				while (mRs4.next())
				{
					questionInTestDAOV2_in.add(tid, mRs4.getLong(1));
				}
				mRs4.close();
				stmt4.close();
			}

		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}

		return tid;
	}

	public long getTestID(long cvId_in, int type_in)
	{
		long id = -1;
		try
		{
			String sql = "SELECT Test.tid,Test.ctime,Test.state,Test.type FROM Test,CVTest where CVTest.cvid = ? and Test.state != ? and Test.ctime = (Select max(Test.ctime) from test where Test.tid = ? and Test.state != ? ) and Test.type = ? ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, cvId_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setLong(3, cvId_in);
			stmt.setInt(4, ConfigCount.DELETED);
			stmt.setInt(5, type_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				id = mRs.getLong(1);
			}
		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;

	}

	public boolean delete(long testid_in)
	{
		boolean sucess = false;
		try
		{
			String sql1 = "UPDATE Test SET state = ? where tid = ?";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setInt(1, ConfigCount.DELETED);
			stmt1.setLong(2, testid_in);
			sucess = stmt1.execute();
			stmt1.close();

			String sql = "DELETE FROM Testaccesscode WHERE testid = ? ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, testid_in);
			stmt.execute();
			sucess = true;

		} catch (SQLException ex)
		{
			Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	/**
	 * Kiem tra sinh test duoc hay khong voi tap dau vao la cac question cung
	 * kieu, va khong phai la nhap.
	 *
	 * @param unit_in
	 * @param type_in
	 * @param n_in
	 * @param p_in
	 * @return
	 */
	public int[] checkTest(int unit_in, int type_in, int n_in, int p_in, int a_in, int b_in, int c_in)
	{
		int a = -1;
		int b = -1;
		int c = -1;
		int[] test = new int[]{-1, -1, -1};
		try
		{
			String sql1 = "select count(qid) from question where question.unit = ? and question.type=? and ( question.state != ? and isdraf = ? )group by weight  ;";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setLong(1, unit_in);
			stmt1.setInt(2, type_in);
			stmt1.setInt(3, -1); //Thanh config khong biet.
			stmt1.setBoolean(4, false);

			ResultSet mRs1 = stmt1.executeQuery();
			if (mRs1.next())
			{
				a = mRs1.getInt(1);
				a = a + a_in;
			}
			if (a < 0)
			{
				a = 0;
			}
			if (mRs1.next())
			{
				b = mRs1.getInt(1);
				b = b + b_in;
			}
			if (b < 0)
			{
				b = 0;
			}
			if (mRs1.next())
			{
				c = mRs1.getInt(1);
				c = c + c_in;
			}
			if (c < 0)
			{
				c = 0;
			}

			mRs1.close();
			stmt1.close();

			test = CreateTestV2.randomTest(a, b, c, n_in, p_in);

			if (test == null || test[0] == -1)
			{
				return new int[]{-1, -1, -1};
			}
		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return test;
	}

	public int countTest(int unit_in, int type_in, int n_in, int p_in)
	{
		int a = -1;
		int b = -1;
		int c = -1;
		int count = 0;
		try
		{
			String sql1 = "select count(qid) from question where question.unit = ? and question.type=? and ( question.state != ? and isdraf = ? )group by weight  ;";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setLong(1, unit_in);
			stmt1.setInt(2, type_in);
			stmt1.setInt(3, -1); //Thanh config khong biet.
			stmt1.setBoolean(4, false);

			ResultSet mRs1 = stmt1.executeQuery();
			if (mRs1.next())
			{
				a = mRs1.getInt(1);
			}
			if (a < 0)
			{
				a = 0;
			}
			if (mRs1.next())
			{
				b = mRs1.getInt(1);
			}
			if (b < 0)
			{
				b = 0;
			}
			if (mRs1.next())
			{
				c = mRs1.getInt(1);
			}
			if (c < 0)
			{
				c = 0;
			}

			mRs1.close();
			stmt1.close();

			count = CreateTestV2.countTest(a, b, c, n_in, p_in);

		} catch (SQLException ex)
		{
			Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}
}
