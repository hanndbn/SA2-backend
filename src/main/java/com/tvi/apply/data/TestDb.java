//thanhpk
package com.tvi.apply.data;

import com.tvi.apply.data.entity.ETest;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.business.CreateTestV2;
import com.tvi.apply.util.string.MStringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestDb
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

	public TestDb(IDatabase database)
	{
		this.database = database;
	}

	public ETest getTest(long tid)
	{
		ETest sucess = null;
		try
		{
			String sql = "select ctime,state,type from test where tid = ? and state != ? and ctime = (select max(ctime) from test where tid = ? and state != ? ) ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, tid);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setInt(4, ConfigCount.DELETED);
			stmt.setLong(3, tid);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ctime = new java.util.Date(mRs.getLong(1));
				state = mRs.getInt(2);
				type = mRs.getInt(3);

				sucess = new ETest(tid, ctime, type, state);
			}

			mRs.close();
			stmt.close();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public List<ETest> getListTest(long cvId_in)
	{
		List<ETest> list = new ArrayList<ETest>();
		try
		{
			String sql = "select test.tid,test.ctime,test.state,test.type FROM test,cvtest where test.state != ? and test.tid = cvtest.testid and cvtest.cvid = ? ;";
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
				list.add(new ETest(tid, ctime, type, state));
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return list;
	}

	private long addTest(ETest testEntity_in)
	{
		long sucess = -1;
		try
		{
			String sql = "insert into test(ctime,state,type) values(?,?,?);";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public long addTestWithTestCode(int unit_in, int type_in, int n_in, int p_in)
	{
		String code = null;
		long tid = -1;
		QuestionInTestDb questionInTestDAOV2_in = new QuestionInTestDb(database);
		TestAccessCodeDb testcode = new TestAccessCodeDb(database);
		ETest testEntity = new ETest(tid, ctime, type_in, ConfigCount.CREATE);
		try
		{
			while (true)
			{
				code = MStringUtil.randomNumberString();
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
			String sql = "insert into testaccesscode(testid,code) values(?,?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, tid);
			stmt.setString(2, code.trim());
			stmt.execute();
			stmt.close();


			//create weight 1
			if (test[0] > 0)
			{
				String sql2 = "select qid from question where weight = 1 and unit = ? and type = ? and state != ? order by RAND() limit 0, ?;";

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
				String sql3 = "select qid from question where weight = 2 and unit = ? and  type = ? and  state != ? order by RAND() limit 0, ?;";

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
				String sql4 = "select qid from question where weight = 3 and unit = ? and  type = ? and state != ? order by RAND() limit 0, ?;";

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
			throw new RuntimeException(ex);
		}

		return tid;
	}

	public long getTestID(long cvId_in, int type_in)
	{
		long id = -1;
		try
		{
			String sql = "select test.tid from test, cvtest where cvtest.cvid = ? and test.state != ? and cvtest.testid = test.tid and test.type = ? order by cvtest.testid desc limit 1";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, cvId_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setInt(3, type_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				id = mRs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return id;

	}

	public boolean delete(long testid_in)
	{
		boolean sucess = false;
		try
		{
			String sql1 = "update test set state = ? where tid = ?";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setInt(1, ConfigCount.DELETED);
			stmt1.setLong(2, testid_in);
			sucess = stmt1.execute();
			stmt1.close();

			String sql = "delete from testaccesscode where testid = ? ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, testid_in);
			stmt.execute();
			sucess = true;

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
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
			throw new RuntimeException(ex);
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
			throw new RuntimeException(ex);
		}
		return count;
	}
}
