//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.*;
import com.tvi.apply.data.entity.EQuestion;
import com.tvi.apply.data.entity.ETest;
import com.tvi.apply.data.entity.ETestAnswer;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.data.AnswerAccessCodeDb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestMgr implements ITestMgt
{

	QuestionDb questiondao = new QuestionDb();
	TestDb testdb;// TestDb();
	AnswerDb answerdb;//= new AnswerDb();
	CVTestDb cvtestdb;
	CVTestAnswerDb cvanswerdb;
	AnswerAccessCodeDb answeraccesscodedb;//= new AnswerAccessCodeDb();
	TestAccessCodeDb testaccesscodedb;// = new TestAccessCodeDb();
	QuestionInTestDb questionintestdb;// = new QuestionInTestDb();
	TestAnswerDb testanswerdb;// = new TestAnswerDb();

	public TestMgr(IDatabase database)
	{
		cvtestdb = new CVTestDb();
		cvtestdb.setDatabase(database);
		cvanswerdb = new CVTestAnswerDb(database);
		questiondao.setDatabase(database);
		testdb = new TestDb(database);
		answerdb = new AnswerDb(database);
		answeraccesscodedb = new AnswerAccessCodeDb(database);
		testanswerdb = new TestAnswerDb(database);
		testaccesscodedb = new TestAccessCodeDb(database);
		questionintestdb = new QuestionInTestDb(database);
	}

	@Override
	public long addQuestion(int unit, EQuestion q)
	{
		return questiondao.add(unit, q);
	}

	@Override
	public long countQuestion(int unit, int type)
	{
		return questiondao.countQuestion(unit, type);
	}

	@Override
	public List<EQuestion> listQuestion(int unit, int type, int p, int ps, String[] s)
	{
		return questiondao.getqQuestionEntitys(unit, type, p, ps, s);
	}

	@Override
	public void deleteQuestion(long questionid)
	{
		questiondao.delete(questionid);
	}

	@Override
	public void editQuestion(long questionid, EQuestion question)
	{
		questiondao.update(questionid, question);
	}

	@Override
	public EQuestion getQuestion(long questionid)
	{
		return questiondao.getQuestion(questionid);
	}

	@Override
	public long startTest(long testid)
	{
		return testanswerdb.addAnswerWithAnswercode(testid);
	}

	@Override
	public void submitAnswer(long answerid, List<Long> qid, List<Integer> answers)
	{
		testanswerdb.submit(answerid, qid, answers);
	}

	@Override
	public ETestAnswer getAnswer(long answerid)
	{
		return testanswerdb.getAnswer(answerid);
	}

	@Override
	public long createTest(int unit, int type, int n, int p)
	{
		return testdb.addTestWithTestCode(unit, type, n, p);
	}

	@Override
	public long matchTest(String code)
	{
		return testaccesscodedb.getTestByTestCode(code);
	}

	@Override
	public String matchTestCode(long testid)
	{
		return testaccesscodedb.getTestCodeByTestId(testid);
	}

	@Override
	public String matchAnswerCode(long answerid)
	{
		return answeraccesscodedb.getAnswerCodeByAnswerId(answerid);
	}

	@Override
	public long matchAnswer(String code)
	{
		return answeraccesscodedb.getAnswerByAnswerCode(code);
	}

	@Override
	public void deleteTestCode(String code)
	{
		testaccesscodedb.deleteTestCode(code);
	}

	@Override
	public void deleteAnswerCode(String code)
	{
		answeraccesscodedb.deleteAnswerCode(code);
	}

	@Override
	public ETest getTest(long testid)
	{
		return testdb.getTest(testid);
	}

	@Override
	public List<EQuestion> getTestQuestion(long testid)
	{
		//get Qid
		List<EQuestion> listQuestion = new ArrayList<EQuestion>();
		List<Long> listQid = questionintestdb.getQuestion(testid);
		//get question
		for (Long aListQid : listQid)
		{
			listQuestion.add(questiondao.getQuestion(aListQid));
		}
		long seed = System.nanoTime();
		Collections.shuffle(listQuestion, new Random(seed));

		return listQuestion;
	}

	@Override
	public long getTestbyAnswer(long answerid)
	{
		return testanswerdb.getTestByAnswer(answerid);
	}

	@Override
	public int getQuestionsUnit(long qid)
	{
		return questiondao.getUnit(qid);
	}

	@Override
	public int tryEditQuestion(long questionid, EQuestion question, int n, int p)
	{
		return questiondao.update2(questionid, question, n, p);
	}

	@Override
	public int tryDeleteQuestion(long questionid, int n, int p)
	{
		return questiondao.delete2(questionid, n, p);
	}

	@Override
	public int estimateTest(int unit, int type, int n, int p)
	{
		return testdb.countTest(unit, type, n, p);
	}

	@Override
	public long getUserFromTest(long testid)
	{
		return cvtestdb.getCV(testid);
	}

	@Override
	public long getUserFromAnswer(long testid)
	{
		return cvanswerdb.getCV(testid);
	}

	@Override
	public void addUserTest(long userid, long testid)
	{
		cvtestdb.add(userid, testid);
	}

	@Override
	public void addUserAnswer(long userid, long answerid)
	{
		cvanswerdb.add(userid, answerid);
	}

	@Override
	public long getUserTest(long userid, int type)
	{
		return testdb.getTestID(userid, type);
	}
}
