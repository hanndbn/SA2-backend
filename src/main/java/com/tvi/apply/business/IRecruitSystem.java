//thanhpk

package com.tvi.apply.business;

import com.tvi.apply.data.entity.ECandidate;
import com.tvi.apply.data.entity.EJobSetting;
import com.tvi.apply.type.*;

import java.util.List;
import java.util.Map;

public interface IRecruitSystem
{
	long createUser(long caller, TUser user);

	void editUser(long caller, TUser user);

	TUser getUser(long caller, long userid);

	String loginUP(String username, String password);

	long matchAuth(String authcode);

	void logout(long caller, String authcode);

	List<TEmail> listNewEmail(long caller, long userid, int n, int p);

	List<TEmail> listJunkMail(long caller, int n, int p);

	TEmail viewEmail(long caller, long emailid);

	void markAsRead(long caller, long userid, long emailid);

	TEmail[] listUnreadEmail(long caller, long userid, String email, int n, int p);

	long countUnreadEmail(long caller, long userid, String email);

	void markAsUnread(long caller, long userid, long emailid);

	void junkMail(long caller, long emailid);

	void unjunkMail(long caller, long emailid);

	void sendMail(long caller, TEmail email);

	void cleanUnread(long caller, long user);

	long createJob(long caller, TJob job, EJobSetting ejs);

	void updateJob(long caller, TJob job);

	TJob getJob(long caller, long id);

	List<TJob> listJobByOrg(long caller, boolean archived, long org, String keyword, int n, int p);

	long countJobByOrg(long caller, boolean archived, long org, String keyword);

	List<TJob> listJob(long caller, boolean archived, String keyword, int n, int p);

	long countJob(long caller, boolean archived, String keyword);

	List<TActionLog> listActionLog(long caller, long userid, int n, int p);

	long countActionLog(long caller, long userid);

	long comment(long caller, long userid, long cid, String comment);

	void editComment(long caller, long commentid, String comment);

	void deleteComment(long caller, long commentid);

	long createCandidate(long caller, TCandidate candidate);

	void updateCandidate(long caller, TCandidate candidate);

	void deleteCandidate(long id);

	long addInfo(long caller, long cid, long fid, String value);

	void deleteInfo(long caller, long cid, long fid);

	void editInfo(long caller, long cid, long fid, String value);

	TCandidate viewCandidate(long caller, long id,ECandidate ecan);

	List<TEmail> listCandidateMail(long caller, long cid, int n, int p);

	List<TOrg> listOrg(long caller, boolean archived, String keyword, int n, int p);

	long createOrg(long caller, TOrg org);

	void editOrg(long caller, TOrg org);

	long countOrg(long caller, boolean archived, String keyword);

	TOrg getOrg(long caller, long id);

	List<TField> listField(long caller, boolean archived, String keyword, int n, int p);

	long createField(long caller, TField field);

	void editField(long caller, TField field);

	long countField(long caller, boolean archived, String keyword);

	List<TEmailTemplate> listTemplate(long caller, String keyword, int n, int p);

	long createTemplate(long caller, TEmailTemplate template);

	void updateTemplate(long caller, TEmailTemplate template);

	long countTemplate(long caller, String keyword);

	TTest2 startTest(long caller, String testcode);

	void submitTest(long caller, String testcode, List<Long> qs, List<String> choice, List<Integer> answers);

	//chú ý nếu ở thùng reserved thì ko gửi dc test
	String openTest(long caller, long cid, long type);

	void closeTest(long caller, long cid, long type);

	TField getField(long caller, long id);

	String packCv(long caller, long[] cids);

	void mailMerge(long caller, long[] to, TEmail emailtem);

	void sendCandidate(long userid, long eid, String email);

	void sendCandidate(long userid, long canid, String attachment, String email);

	List<TCandidate> listCandidateByJob(long caller, long[] job, boolean archived, int cvstate, String keyword, int n, int p,  String[] filter, String[] value, String[] operator, String[] orderby);

	long countCandidateByJob(long caller, long[] job, boolean archived, int cvstate, String keyword, String[] filter, String[] value, String[] operator);

	String exportCandidate(long userid, Long[] candidates, String s, Map<String,String> name);

	List<TQuestion> listEngQuestion(long caller, String keyword, int n, int p);

	long createEngQuestion(long caller, TQuestion question);

	long countEngQuestion(long caller,String keyword);

	List<TQuestion> listIQQuestion(long caller, String keyword, int n, int p);

	long createIQQuestion(long caller, TQuestion question);

	long countIQQuestion(long caller,String keyword);

	TQuestion readQuestion(long userid, long id);

	void editQuestion(long userid, TQuestion q);

	TJobSetting getJobSetting(long userid, long id);
	void setJobSetting(long userid, long id, TJobSetting jobsetting);

	long countCandidateMail(long userid, long cid);

	String getAttachment(long userid, long cid);
}
