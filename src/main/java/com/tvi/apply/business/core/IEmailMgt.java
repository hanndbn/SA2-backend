//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EEmail;

import java.util.List;

public interface IEmailMgt
{
	long createEmail( EEmail email, long boxid);

	List<EEmail> listCandidateEmail(long cid, int n, int p);

	long countCandidateEmail(long cid);

	List<EEmail> listUnreadEmail(long userid, int n, int p);

	long countUnreadEmail(long userid, String email);

	EEmail readEmail(long emaild);

	boolean isEmailReaded(long userid, long emailid);

	void markEmailAsReaded(long userid, long emailid);

	void markAllAsReaded(long userid);

	void markEmailAsUnreaded(long userid, long emailid);

	void junkMail(long emailid);

	void unjunkMail(long emailid);

	void cleanUnread(long userid );

}
