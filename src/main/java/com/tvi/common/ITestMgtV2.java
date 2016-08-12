package com.tvi.common;

import com.tvi.common.entity.QuestionEntityV2;
import com.tvi.common.entity.TestAnswerEntity;
import com.tvi.common.entity.TestEntity;
import java.util.List;

public interface ITestMgtV2
{

	long addQuestion(int unit, QuestionEntityV2 q);

	long countQuestion(int unit, int type);

	List<QuestionEntityV2> listQuestion(int unit, int type, int p, int ps, String[] s);

	void deleteQuestion(long questionid);

	void editQuestion(long questionid, QuestionEntityV2 question);

	QuestionEntityV2 getQuestion(long questionid);

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

	TestEntity getTest(long testid);
	
	List<QuestionEntityV2> getTestQuestion(long testid);

	/*trả về answerid mới, đồng thời tạo một chuỗi access code*/
	long startTest(long testid);

	/*chú ý, tính điểm luôn cho testanswer, nếu nộp quá thời gian 60s -> coi như 0 điểm*/ 
	void submitAnswer(long answerid, List<Long> qid, List<Integer> answers);

	TestAnswerEntity getAnswer(long answerid);

	/*trả về testid của answer tương ứng*/
	long getTestbyAnswer(long answerid);
	
	int getQuestionsUnit(long qid);
	
	/* trả về 0 nều thành công -1 nếu k thành công*/
	int tryEditQuestion(long questionid, QuestionEntityV2 question, int n, int p);
	
	/* trả về 0 nều thành công -1 nếu k thành công*/
	int tryDeleteQuestion(long questionid,  int n, int p);
	
	/* trả về số lượng test có thể sinh với 2 tham số n, p */
	int estimateTest(int unitid,int type, int n, int p);
}
