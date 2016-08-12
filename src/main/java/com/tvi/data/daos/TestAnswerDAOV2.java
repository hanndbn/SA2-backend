/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.TestAnswerEntity;
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
public class TestAnswerDAOV2
{

	private IDatabase database;

	private long id;

	private Date ctime;

	private List<Long> questions;

	private List<Integer> answers = new ArrayList<Integer>();

	private int point;

	private Date stime;

	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public long getTestAnswer(long cvTd_id, int type_in)
	{

		long id = -1;
		try
		{
			String sql = "SELECT Testanswer.taid,Testanswer.ctime,Testanswer.state,Testanswer.testid,Testanswer.point " + "FROM Testanswer,CVTestAnswer,Test where Testanswer.taid = CVTestAnswer.taid and  Testanswer.state != ? and CVTestAnswer.cvid = ? and Testanswer.testid = Test.tid and Test.type = ? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(2, cvTd_id);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setInt(3, type_in);

			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				id = mRs.getLong(1);
			}
		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;
	}

	public long getTestByAnswer(long answerid_in)
	{
		long testid = -1;
		try
		{
			String sql = "Select testid from testanswer where taid = ? and state != ? and " + "ctime = (Select max(ctime) from testanswer where taid = ? and state != ? )";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, answerid_in);
			stmt.setInt(2, ConfigCount.DELETED);
			stmt.setLong(3, answerid_in);
			stmt.setInt(4, ConfigCount.DELETED);
			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				testid = mRs.getLong(1);
			}
			mRs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return testid;
	}

	public TestAnswerEntity getAnswer(long answerid_in)
	{
		TestAnswerEntity test = null;
		QuestionInTestDAOV2 questionInTestDAOV2_in = new QuestionInTestDAOV2();
		questionInTestDAOV2_in.setDatabase(database);
		AnswerDAOV2 answerDAOV2_in = new AnswerDAOV2();

		answerDAOV2_in.setDatabase(database);
		try
		{
			//get TestId
			long tid = getTestByAnswer(answerid_in);
			//get List Question
			questions = questionInTestDAOV2_in.getQuestion(tid);

			answers = new ArrayList<Integer>();
			for (int i = 0; i < questions.size(); i++)
			{
				answers.add(answerDAOV2_in.getAnswerByQuestion(answerid_in, questions.get(i)));
			}

			String sql = "SELECT ctime,point,stime FROM Testanswer where state != ? and taid = ? and ctime = (Select max(ctime) from testanswer where state != ? and taid = ? );";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(2, answerid_in);
			stmt.setInt(1, ConfigCount.DELETED);
			stmt.setLong(4, answerid_in);
			stmt.setInt(3, ConfigCount.DELETED);

			ResultSet mRs = stmt.executeQuery();
			if (mRs.next())
			{
				ctime = new java.util.Date(mRs.getLong(1));
				stime = new java.util.Date(mRs.getLong(3));
				point = mRs.getInt(2);
				test = new TestAnswerEntity(id, ctime, questions, answers, point, stime);
			}
		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return test;
	}

	public boolean update(long answerid_in, int point)
	{
		boolean sucess = false;
		try
		{
			long stime = -1;
			String sql1 = "Select stime from Testanswer WHERE taid=? and state != ? and ctime= (Select max(ctime) from testanswer WHERE taid=? and state != ?  ) ";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setLong(1, answerid_in);
			stmt1.setLong(3, answerid_in);
			stmt1.setInt(2, ConfigCount.DELETED);
			stmt1.setInt(4, ConfigCount.DELETED);

			ResultSet mRs = stmt1.executeQuery();
			if (mRs.next())
			{
				stime = mRs.getLong(1);
			}
			mRs.close();
			stmt1.close();

			String sql = "UPDATE Testanswer SET Testanswer.point=?  WHERE Testanswer.taid=? and Testanswer.state != ? and ctime= (Select max(lon.ctime) from (Select * from Testanswer ) as lon WHERE lon.taid=? and lon.state != ?  ) ";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(2, answerid_in);
			stmt.setLong(4, answerid_in);
			stmt.setInt(3, ConfigCount.DELETED);
			stmt.setInt(5, ConfigCount.DELETED);
			if (new java.util.Date().getTime() - stime > 60000)
			{
				stmt.setInt(1, 0);
			} else
			{
				stmt.setInt(1, point);
			}
			stmt.execute();
			stmt.close();
			sucess = true;
		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	public int submit(long answerid_in, List<Long> qid_in, List<Integer> answers_in)
	{
		AnswerDAOV2 answerDAOV2 = new AnswerDAOV2();
		answerDAOV2.setDatabase(database);
		int point = 0;
		for (int i = 0; i < qid_in.size(); i++)
		{
			point += answerDAOV2.add(answerid_in, qid_in.get(i), answers_in.get(i));
		}
		update(answerid_in, point);
		return point;
	}

	public long add(long testid_in)
	{
		long sucess = -1;
		int p = 0;
		try
		{
			String sql1 = "Select sum(question.weight) From question Where question.qid in (Select questionintest.qid from questionintest where questionintest.tid = ? ) ";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setLong(1, testid_in);
			ResultSet mRs1 = stmt1.executeQuery();
			if (mRs1.next())
			{
				p = mRs1.getInt(1);
			}
			mRs1.close();
			stmt1.close();

			String sql = "INSERT INTO Testanswer(ctime,state,testid,point,stime) VALUES(?,?,?,?,?);";
			PreparedStatement stmt = database.preparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, (new java.util.Date().getTime()));
			stmt.setInt(2, 0);
			stmt.setLong(3, testid_in);
			stmt.setInt(4, 0);
			stmt.setLong(5, (new java.util.Date().getTime() + p * 60000));
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
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

	public long addAnswerWithAnswercode(long testid_in)
	{
		long taid = add(testid_in);
		try
		{
			String sql = "INSERT INTO answeraccesscode(taid,code) VALUES(?,?);";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, taid);
			String code = StringUtil.randomNumberString();
			stmt.setBytes(2, code.getBytes());
			stmt.execute();
			stmt.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return taid;
	}

	public boolean delete(long answerid)
	{
		boolean sucess = false;
		try
		{
			String sql1 = "UPDATE testanswer SET state = ? where taid = ?";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			stmt1.setInt(1, ConfigCount.DELETED);
			stmt1.setLong(2, answerid);
			sucess = stmt1.execute();
			stmt1.close();

			String sql = "DELETE FROM Answeraccesscode WHERE taid = ? ;";
			PreparedStatement stmt = database.prepareStatement(sql);
			stmt.setLong(1, answerid);
			stmt.execute();
			sucess = true;


		} catch (SQLException ex)
		{
			Logger.getLogger(TestAnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sucess;
	}

}
