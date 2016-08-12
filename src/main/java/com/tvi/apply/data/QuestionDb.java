package com.tvi.apply.data;

import com.tvi.apply.data.entity.EQuestion;
import com.tvi.apply.util.database.IDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class QuestionDb
{

	private IDatabase database;

	private long qid;

	private Date ctime;

	private int state;

	private String content;

	private String choose;

	private int answer;

	private int weight;

	private long creator;

	private boolean isdraf;

	private int type;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public long add(int unit, EQuestion questionEntity_in)
	{
		long sucess = -1;
		try
		{
			String strSQL = "INSERT INTO question(ctime,state,content,choose,answer,weight,creator,isdraf,unit,type) VALUES(?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement stmt = database.prepareStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, (new java.util.Date().getTime()));
			stmt.setInt(2, questionEntity_in.state);
			stmt.setString(3, questionEntity_in.content);
			stmt.setString(4, questionEntity_in.choose);
			stmt.setInt(5, questionEntity_in.answer);
			stmt.setInt(6, questionEntity_in.weight);
			stmt.setLong(7, questionEntity_in.creator);
			stmt.setBoolean(8, questionEntity_in.isdraf);
			stmt.setInt(9, unit);
			stmt.setInt(10, questionEntity_in.type);
			stmt.execute();
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

	public long countQuestion(int unit_in, int type_in)
	{
		long count = -1;
		try
		{
			String sql = "Select count(qid) From question where state != ? and unit = ? and type = ? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setInt(2, unit_in);
			stmt.setInt(3, type_in);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				count = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count;
	}

	public List<EQuestion> getqQuestionEntitys(int unit_in, int type_in, int p_in, int ps_in, String[] s_in)
	{

		List<EQuestion> listQuest = new ArrayList<EQuestion>();
		try
		{
			String sql = "Select qid,ctime,state,content,choose,answer,weight,creator,isdraf,type FROM question WHERE state != ? and unit = ? and type = ?";

			PreparedStatement stmt;
			if (s_in == null || s_in.length < 1)
			{
				sql = sql + " limit ?,? ;";
				stmt = database.prepareStatement(sql);
				stmt.setInt(1, ConfigCount.DELETED);
				stmt.setInt(2, unit_in);
				stmt.setInt(3, type_in);
				stmt.setInt(4, p_in * ps_in);
				stmt.setInt(5, ps_in);
			} else
			{
				sql = sql + " order by ";
				for (int i = 0; i < s_in.length - 1; i++)
				{
					sql = sql + StringEscapeUtils.escapeSql(s_in[i].replaceAll("[^\\w]", "")) + " desc, ";
				}
				sql = sql + StringEscapeUtils.escapeSql(s_in[s_in.length - 1].replaceAll("[^\\w]", "")) + " desc " + " limit ?,? ; ";
				stmt = database.prepareStatement(sql);
				stmt.setInt(1, ConfigCount.DELETED);
				stmt.setInt(2, unit_in);
				stmt.setInt(3, type_in);
				stmt.setInt(4, p_in * ps_in);
				stmt.setInt(5, ps_in);
			}
			ResultSet mRs = stmt.executeQuery();
			while (mRs.next())
			{
				qid = mRs.getLong(1);
				ctime = new Date(mRs.getLong(2));
				state = mRs.getInt(3);
				content = mRs.getString(4);
				choose = mRs.getString(5);
				answer = mRs.getInt(6);
				weight = mRs.getInt(7);
				creator = mRs.getLong(8);
				isdraf = mRs.getBoolean(9);
				type = mRs.getInt(10);
				listQuest.add(new EQuestion(qid, type, ctime, state, content, choose, answer, weight, creator, isdraf));
			}
			mRs.close();
			stmt.close();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return listQuest;

	}

	public boolean delete(long questionId_in)
	{
		try
		{
			String sql = "UPDATE question SET state=?  WHERE qid = ? ; ";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(2, questionId_in);
			stmt.execute();
			stmt.close();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return true;
	}

	public int delete2(long questionId_in, int n_in, int p_in)
	{
		int sucess;
		int unit = -1;
		int a = 0, b = 0, c = 0;
		TestDb testdao = new TestDb(database);

		EQuestion question = getQuestion(questionId_in);
		if (question == null)
		{
			return -1;
		}
		if (!question.isdraf && question.weight == 1)
		{
			a = -1;
		}
		if (!question.isdraf && question.weight == 2)
		{
			b = -1;
		}
		if (!question.isdraf && question.weight == 3)
		{
			c = -1;
		}

		try
		{
			String sql3 = "Select unit FROM question where qid = ? and state != ? and ctime = (Select max(ctime) from question where state != ? and  qid = ? )";
			PreparedStatement stmt3 = database.prepareStatement(sql3);
			stmt3.setInt(2, ConfigCount.DELETED);
			stmt3.setLong(1, questionId_in);
			stmt3.setLong(4, questionId_in);
			stmt3.setInt(3, ConfigCount.DELETED);
			ResultSet mRs3 = stmt3.executeQuery();
			if (mRs3.next())
			{
				unit = mRs3.getInt(1);
			}
			if (unit == -1)
			{
				return -1;
			}
			mRs3.close();
			stmt3.close();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

		if (testdao.checkTest(unit, type, n_in, p_in, a, b, c) == null || testdao.checkTest(unit, type, n_in, p_in, a, b, c)[0] == -1)
		{
			return -1;
		}

		try
		{
			String sql = "UPDATE question SET state=?  WHERE qid = ? ; ";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(2, questionId_in);
			stmt.execute();
			stmt.close();
			sucess = 0;

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;

	}

	public boolean update(long questionid_in, EQuestion question_in)
	{
		boolean sucess;
		int unit = -1;
		EQuestion question = getQuestion(questionid_in);
		if (question == null)
		{
			throw new RuntimeException("Not font questionid");
		}
		try
		{
			String sql3 = "Select unit FROM question where qid = ? and state != ? and ctime = (Select max(ctime) from question where state != ? and  qid = ? )";
			PreparedStatement stmt3 = database.prepareStatement(sql3);
			stmt3.setInt(2, ConfigCount.DELETED);
			stmt3.setLong(1, questionid_in);
			stmt3.setLong(4, questionid_in);
			stmt3.setInt(3, ConfigCount.DELETED);
			ResultSet mRs3 = stmt3.executeQuery();
			if (mRs3.next())
			{
				unit = mRs3.getInt(1);
			}
			if (unit == -1)
			{
				return false;
			}
			mRs3.close();
			stmt3.close();
			String sql = "UPDATE question SET question.state=?  WHERE question.qid =? and question.ctime = (Select max(lon.ctime) from (Select * from question ) as lon where lon.qid =? )  ";
			String sql2 = "INSERT INTO question(qid,ctime,state,content,choose,answer,weight,creator,isdraf,unit,type) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
			//

			//
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(2, questionid_in);
			stmt.setLong(3, questionid_in);
			stmt.execute();
			stmt.close();

			//
			PreparedStatement stmt2 = database.prepareStatement(sql2);
			stmt2.setLong(1, questionid_in);
			stmt2.setLong(2, (new java.util.Date().getTime()));
			stmt2.setInt(3, question_in.state);
			stmt2.setString(4, question_in.content);
			stmt2.setString(5, question_in.choose);
			stmt2.setInt(6, question_in.answer);
			stmt2.setInt(7, question_in.weight);
			stmt2.setLong(8, question_in.creator);
			stmt2.setBoolean(9, question_in.isdraf);
			stmt2.setInt(10, unit);
			stmt2.setInt(11, question_in.type);
			sucess = stmt2.execute();

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public int update2(long questionid_in, EQuestion question_in, int n_in, int p_in)
	{
		int sucess;
		int unit = -1;
		EQuestion question = getQuestion(questionid_in);
		int a = 0, b = 0, c = 0;
		TestDb testdao = new TestDb(database);
		if (question == null)
		{
			return -1;
		}
		try
		{
			String sql3 = "Select unit FROM question where qid = ? and state != ? and ctime = (Select max(ctime) from question where state != ? and  qid = ? )";
			PreparedStatement stmt3 = database.prepareStatement(sql3);
			stmt3.setInt(2, ConfigCount.DELETED);
			stmt3.setLong(1, questionid_in);
			stmt3.setLong(4, questionid_in);
			stmt3.setInt(3, ConfigCount.DELETED);
			ResultSet mRs3 = stmt3.executeQuery();
			if (mRs3.next())
			{
				unit = mRs3.getInt(1);
			}
			if (unit == -1)
			{
				return -1;
			}
			mRs3.close();
			stmt3.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

		// change weight
		if (!question.isdraf && question.weight != question_in.weight)
		{
			if (question.weight == 1)
			{
				a = -1;
			}
			if (question.weight == 2)
			{
				b = -1;
			}
			if (question.weight == 3)
			{
				c = -1;
			}
			if (question_in.weight == 1)
			{
				a = 1;
			}
			if (question_in.weight == 2)
			{
				b = 1;
			}
			if (question_in.weight == 3)
			{
				c = 1;
			}
		}

		//change type
		if (!question.isdraf && question.type != question_in.type)
		{
			if (question.weight == 1)
			{
				a = -1;
			}
			if (question.weight == 2)
			{
				b = -1;
			}
			if (question.weight == 3)
			{
				c = -1;
			}

		}
		// change isdraf
		if (!question.isdraf && question_in.isdraf)
		{

			if (question.weight == 1)
			{
				a = -1;
			}
			if (question.weight == 2)
			{
				b = -1;
			}
			if (question.weight == 3)
			{
				c = -1;
			}
		}

		if (testdao.checkTest(unit, type, n_in, p_in, a, b, c) == null || testdao.checkTest(unit, type, n_in, p_in, a, b, c)[0] == -1)
		{
			return -1;
		}

		try
		{
			String sql = "UPDATE question SET question.state=?  WHERE question.qid =? and question.ctime = (Select max(lon.ctime) from (Select * from question ) as lon where lon.qid =? )  ";
			String sql2 = "INSERT INTO question(qid,ctime,state,content,choose,answer,weight,creator,isdraf,unit,type) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
			//

			//
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(2, questionid_in);
			stmt.setLong(3, questionid_in);
			stmt.execute();
			stmt.close();

			//
			PreparedStatement stmt2 = database.prepareStatement(sql2);
			stmt2.setLong(1, questionid_in);
			stmt2.setLong(2, (new java.util.Date().getTime()));
			stmt2.setInt(3, question_in.state);
			stmt2.setString(4, question_in.content);
			stmt2.setString(5, question_in.choose);
			stmt2.setInt(6, question_in.answer);
			stmt2.setInt(7, question_in.weight);
			stmt2.setLong(8, question_in.creator);
			stmt2.setBoolean(9, question_in.isdraf);
			stmt2.setInt(10, unit);
			stmt2.setInt(11, question_in.type);
			stmt2.execute();
			sucess = 0;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public EQuestion getQuestion(long questionid_in)
	{
		EQuestion questionEntityV2 = null;
		try
		{
			String sql = "SELECT ctime,state,content,choose,answer,weight,creator,isdraf,type FROM question" + " where qid = ? and ctime = (Select max(ctime) FROM question where qid = ? );";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, questionid_in);
			stmt.setLong(2, questionid_in);
			ResultSet mRs = stmt.executeQuery();

			if (mRs.next())
			{
				qid = questionid_in;
				ctime = new java.util.Date(mRs.getLong(1));
				state = mRs.getInt(2);
				content = mRs.getString(3);
				choose = mRs.getString(4);
				answer = mRs.getInt(5);
				weight = mRs.getInt(6);
				creator = mRs.getLong(7);
				isdraf = mRs.getBoolean(8);
				type = mRs.getInt(9);

				questionEntityV2 = new EQuestion(qid, type, ctime, state, content, choose, answer, weight, creator, isdraf);

			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return questionEntityV2;
	}

	public int getUnit(long questionid_in)
	{
		int unit = -1;
		try
		{
			String sql = "SELECT unit FROM question" + " where qid = ? and state != ? and ctime = (Select max(ctime) FROM question where qid = ? and state != ?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, questionid_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setLong(3, questionid_in);
			stmt.setInt(4, ConfigCount.DELETED);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				unit = mRs.getInt(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return unit;
	}

}
