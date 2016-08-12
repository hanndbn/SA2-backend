package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.*;
import com.tvi.apply.type.TCandidate;

import javax.persistence.Tuple;
import java.util.List;

public interface ICandidateMgt extends IEmailMgt
{

	//List<ECandidate> listFullCandidate(long userid, long[] job, boolean archived, int cvstate, String keyword, int n, int p,  String[] filter, String[] value, String[] operator, String[] orderby, List<>);

	List<EEmail> listUnreadEmail(long userid, String email, int n, int p);

	List<String[]> listCandidateAttachments(long cid);

	//liệt kê log, theo thời gian gần nhất trước
	List<ECandidateLog> listCandidateLog(long cid, int n, int p);

	long logCandidate(long cid, ECandidateLog log);

	long countCandidateLog(long cid);

	List<EInfo> listCandidateInfo(long cid);

	long createCandidateInfo(long cid, long fid, String info, long creator);

	void updateCandidateInfo(long fid, long cid, String info);

	void deleteCandidateInfo(long fid, long cid);

	void sendAttachment(long canid, String path, String email);

	List<EComment> listCandidateComment(long cid);

	long commentCandidate(long cid, EComment comment);

	void updateComment(long cid, EComment comment);

	void deleteComment(long commentid);

	List<ECandidate> listCandidateByJob(long userid, long[] job, boolean archived, int cvstate, String keyword, int n, int p,  String[] filter, String[] value, String[] operator, String[] orderby);
	long countCandidateByJob(long[] job, boolean archived, int cvstate, String keyword, String[] filter, String[] value, String[] operator);

	long createCandidate(ECandidate entity);

	ECandidate readCandidate(long id);

	void deleteCandidate(long id);

	void updateCandidate(ECandidate entity);

	void matchCVfromTest(long testid);

	boolean haveSent(String email);
}
