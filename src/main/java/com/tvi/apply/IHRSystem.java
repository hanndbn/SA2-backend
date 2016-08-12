package com.tvi.apply;

import com.tvi.apply.commontype.CCVField;
import com.tvi.apply.commontype.Comment;
import com.tvi.apply.commontype.CTest;
import com.tvi.apply.commontype.CTest2;
import com.tvi.common.entity.*;
import java.util.List;

public interface IHRSystem
{

	long countRequest(long caller, String k);

	List<RequestEntityV2>  listRequest(long caller, int p, int ps, String k, String s[] );

	long createUserUP(long caller, String username, String password, UserEntityV2 user);

	//trả về chuỗi auth sẽ dc ghi trong cookie
	String loginUP(String username, String password);

	//trả về userid nếu tồn tại, trả về -1 nếu ko tồn tại
	long matchAuth(String auth);

	List<CCVField> listCVField(long caller, long cvid);
	
	RequestEntityV2 peekRequest(long caller, long requestid);

	void logout(long caller, long uid,String authcode);

	UserEntityV2 getUser(long caller, long uid);

	String resetPassword(long caller, long userid);

	void editUserUP(long caller, long uid, String username, String password, String oldpassword);

	List<UnitEntity> getUnitbyUser(long caller, long uid);

	CVEntityV2 getCV(long caller, long id);

	long createCV(long caller, long requestid, CVEntityV2 cv, String cvfile, List<InfoEntity> infos);

	void deleteCV(long caller, long cvid);

	long matchTestCode(long caller, String code);

	String matchUsername(long caller, long uid);
	
	List<CVEntityV2> listCV(long caller, long bagid, int p, int ps, String[] s);

	List<CVBagEntity> listBag(long caller, long requestid);

	long countRequest(long caller, int unit);

	long countCV(long caller, long bagid);

	void comment(long caller, long cvid, CVCommentEntity comment);

	List<Comment> listComment(long caller, long cvid);

	List<RequiredField> listField(long caller, int unit);

	List<RequiredField> listField2(long caller, long request);

	long createField(long caller, int unit, RequiredField field);

	void editField(long caller, long id, RequiredField field);

	void deleteField(long caller, long fieldid);

	List<QuestionEntityV2> listQuestion(long caller, int unit, int type, int p, int ps, String[] s);

	long countQuestion(long caller, int unit, int type);

	long createQuestion(long caller, int unit, QuestionEntityV2 question);

	CTest startTest(long caller, String code);
	
	CTest2 startTest2(long caller, String code);

	void submitTest(long caller, String testcode, List<Long> qs, List<String> choice, List<Integer> answers);

	void editCV(long caller, long id, CVEntityV2 cv);

	void editRequest(long caller, long requestid, RequestEntityV2 req);

	long createRequest(long caller, int unit, RequestEntityV2 request);

	void deleteRequest(long caller, long requestid);

	RequestEntityV2 getRequest(long caller, long requestid);

	List<RequestEntityV2> listRequestByUnit(long caller, int unit, int p, int ps, String[] s);

	void editBag(long caller, long bagid, CVBagEntity bag);

	CVBagEntity getBag(long caller, long bagid);

	String exportCV(long caller, long cvid, String template);

	String quickviewCV(long caller, long cvid);

	QuestionEntityV2 getQuestion(long caller, long qid);

	RequiredField getRequiredField(long caller, long fieldid);

	/*thêm vào danh sách unit của user*/
	void getInUnit(long caller, long userid, int unit);

	/*xóa unit từ danh sách unit của user*/
	void getOutUnit(long caller, long userid, int unit);

	void deleteQuestion(long caller, long questionid);

	void deleteBag(long caller, long bagid);

	void editQuestion(long caller, long qid, QuestionEntityV2 q);

	void editEmailForm(long caller, long efid, EmailFormEntity ef);

	List<EmailFormEntity> listEmailForm(long caller, int unit);

	EmailFormEntity getEmailForm(long caller, long id);

	long countCVRequest(long caller, long request);

	long getPrimaryBag(long caller, long request);

	long getSecondaryBag(long caller, long request);

	long createBag(long caller, long requestid, CVBagEntity bag);


	long createEmailForm(long caller, int unit, EmailFormEntity ef);

	void moveCV(long caller, long bagid, long cvid);

	String getConfig(long caller, int unitid, String ref);

	void setConfig(long caller, int unitid, String ref, String val);

	UnitEntity getUnitbyRequest(long caller, long requestid);
}
