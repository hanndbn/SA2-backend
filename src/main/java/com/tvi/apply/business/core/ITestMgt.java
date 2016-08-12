package com.tvi.apply.business.core;
import com.tvi.apply.data.entity.EQuestion;
import com.tvi.apply.data.entity.ETest;
import com.tvi.apply.data.entity.ETestAnswer;

import java.util.List;

public interface ITestMgt
{
	long addQuestion(int unit, EQuestion q);

	long countQuestion(int unit, int type);

	List<EQuestion> listQuestion(int unit, int type, int p, int ps, String[] s);

	void deleteQuestion(long questionid);

	void editQuestion(long questionid, EQuestion question);

	EQuestion getQuestion(long questionid);

	/*tạo một test mới và gen một chuỗi trong test code*/
	long createTest(int unit, int type, int n, int p);

	/*trả về testcode tương ứng với test*/
	String matchTestCode(long testid);

	String matchAnswerCode(long answerid);

	long matchAnswer(String code);
	/*trả về testid tương ứng với testcode*/

	long matchTest(String code);

	void deleteTestCode(String code);

	void deleteAnswerCode(String code);

	ETest getTest(long testid);
	
	List<EQuestion> getTestQuestion(long testid);

	/*trả về answerid mới, đồng thời tạo một chuỗi access code*/
	long startTest(long testid);

	/*chú ý, tính điểm luôn cho testanswer, nếu nộp quá thời gian 60s -> coi như 0 điểm*/ 
	void submitAnswer(long answerid, List<Long> qid, List<Integer> answers);

	ETestAnswer getAnswer(long answerid);

	/*trả về testid của answer tương ứng*/
	long getTestbyAnswer(long answerid);
	
	int getQuestionsUnit(long qid);
	
	/* trả về 0 nều thành công -1 nếu k thành công*/
	int tryEditQuestion(long questionid, EQuestion question, int n, int p);
	
	/* trả về 0 nều thành công -1 nếu k thành công*/
	int tryDeleteQuestion(long questionid,  int n, int p);
	
	/* trả về số lượng test có thể sinh với 2 tham số n, p */
	int estimateTest(int unitid,int type, int n, int p);

	long getUserFromTest(long testid);

	long getUserFromAnswer(long testid);

	void addUserTest(long userid, long testid);

	void addUserAnswer(long userid, long answerid);

	long getUserTest(long userid, int type);
}
