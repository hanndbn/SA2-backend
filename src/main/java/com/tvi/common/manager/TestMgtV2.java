/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.common.manager;

import com.tvi.common.IDatabase;
import com.tvi.common.ITestMgtV2;
import com.tvi.common.entity.QuestionEntityV2;
import com.tvi.common.entity.TestAnswerEntity;
import com.tvi.common.entity.TestEntity;
import com.tvi.data.daos.AnswerAccessCodeDAOV2;
import com.tvi.data.daos.AnswerDAOV2;
import com.tvi.data.daos.QuestionDAOV2;
import com.tvi.data.daos.QuestionInTestDAOV2;
import com.tvi.data.daos.TestAccessCodeV2;
import com.tvi.data.daos.TestAnswerDAOV2;
import com.tvi.data.daos.TestDAOV2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Manh
 */
public class TestMgtV2 implements ITestMgtV2 {

    QuestionDAOV2 questiondao = new QuestionDAOV2();
    TestDAOV2 testdao = new TestDAOV2();
    AnswerDAOV2 answerdao = new AnswerDAOV2();
    AnswerAccessCodeDAOV2 answeraccesscodedao = new AnswerAccessCodeDAOV2();
    TestAccessCodeV2 testaccesscodedao = new TestAccessCodeV2();
    QuestionInTestDAOV2 questionintestdao = new QuestionInTestDAOV2();
    TestAnswerDAOV2 testanswerdao = new TestAnswerDAOV2();

    public TestMgtV2(IDatabase database) {
        questiondao.setDatabase(database);
        testdao.setDatabase(database);
        answerdao.setDatabase(database);
        answeraccesscodedao.setDatabase(database);
        testanswerdao.setDatabase(database);
        testaccesscodedao.setDatabase(database);
        questionintestdao.setDatabase(database);
    }

    @Override
    public long addQuestion(int unit, QuestionEntityV2 q) {
        return questiondao.add(unit, q);
    }

    @Override
    public long countQuestion(int unit, int type) {
        return questiondao.countQuestion(unit, type);
    }

    @Override
    public List<QuestionEntityV2> listQuestion(int unit, int type, int p, int ps, String[] s) {
        return questiondao.getqQuestionEntitys(unit, type, p, ps, s);
    }

    @Override
    public void deleteQuestion(long questionid) {
        questiondao.delete(questionid);
    }

    @Override
    public void editQuestion(long questionid, QuestionEntityV2 question) {
        questiondao.update(questionid, question);
    }

    @Override
    public QuestionEntityV2 getQuestion(long questionid) {
        return questiondao.getQuestion(questionid);
    }

    @Override
    public long startTest(long testid) {
        return testanswerdao.addAnswerWithAnswercode(testid);
    }

    @Override
    public void submitAnswer(long answerid, List<Long> qid, List<Integer> answers) {
        testanswerdao.submit(answerid, qid, answers);
    }

    @Override
    public TestAnswerEntity getAnswer(long answerid) {
        return testanswerdao.getAnswer(answerid);
    }

    @Override
    public long createTest(int unit, int type, int n, int p) {
        return testdao.addTestWithTestCode(unit, type, n, p);
    }

    @Override
    public long matchTest(String code) {
        return testaccesscodedao.getTestByTestCode(code);
    }

    @Override
    public String matchTestCode(long testid) {
        return testaccesscodedao.getTestCodeByTestId(testid);
    }

    @Override
    public String matchAnswerCode(long answerid) {
        return answeraccesscodedao.getAnswerCodeByAnswerId(answerid);
    }

    @Override
    public long matchAnswer(String code) {
        return answeraccesscodedao.getAnswerByAnswerCode(code);
    }

    @Override
    public void deleteTestCode(String code) {
        testaccesscodedao.deleteTestCode(code);
    }

    @Override
    public void deleteAnswerCode(String code) {
        answeraccesscodedao.deleteAnswerCode(code);
    }

    @Override
    public TestEntity getTest(long testid) {
        return testdao.getTest(testid);
    }

    @Override
    public List<QuestionEntityV2> getTestQuestion(long testid) {
        //get Qid
        List<QuestionEntityV2> listQuestion = new ArrayList<QuestionEntityV2>();
        List<Long> listQid = questionintestdao.getQuestion(testid);
        //get question
        for (int i = 0; i < listQid.size(); i++) {
            listQuestion.add(questiondao.getQuestion(listQid.get(i)));
        }
        long seed = System.nanoTime();
        Collections.shuffle( listQuestion, new Random(seed));

        return listQuestion;
    }

    @Override
    public long getTestbyAnswer(long answerid) {
        return testanswerdao.getTestByAnswer(answerid);
    }

    @Override
    public int getQuestionsUnit(long qid) {
        return questiondao.getUnit(qid);
    }

    @Override
    public int tryEditQuestion(long questionid, QuestionEntityV2 question, int n, int p) {
        return questiondao.update2(questionid, question, n, p);
    }

    @Override
    public int tryDeleteQuestion(long questionid, int n, int p) {
        return questiondao.delete2(questionid, n, p);
    }

    @Override
    public int estimateTest(int unit, int type, int n, int p) {
        return testdao.countTest(unit, type, n, p);
    }
}
