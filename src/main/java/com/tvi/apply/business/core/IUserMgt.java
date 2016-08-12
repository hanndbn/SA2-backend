//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.*;
import com.tvi.apply.type.TUser;

import java.util.List;

public interface IUserMgt
{
	//liệt kê log, theo thời gian gần nhất trước
	List<EAccessLog> listAccessLog(long userid, int n, int p);

	long createUser(EUser user);

	void editUser(EUser user);

	long countAccessLog(long uid);

	void logAccess(long userid, EAccessLog entity);

	// return an Auth ( a unique 256 bit string)
	String login(String username, String password);

	void logout(String authcode);

	long matchAuth(String auth);

	List<EActionLog> listActionLog(long userid, int n, int p);

	long countActionLog(long uid);

	void logAction(long userid, EActionLog entity);

	EUser readUser(long id);

	EUser getUserByUsername(String username);

	List<EUser> listUser(String keyword, int n, int p);

	long countUser(String keyword);

	void updateUser(EUser entity);

	//void deleteUser(long id);

	void access(long userid, String action);

	TUser getUser(long userid);
/*
	ERole readRole(long id);

	List<ERole> listRole(int n, int p);

	List<ERole> countRole(int n, int p);

	void updateRole(ERole entity);

	void deleteRole(long id);

	EAction readAction(long id);

	List<EAction> listAction(int n, int p);

	List<EAction> countAction(int n, int p);

	void updateAction(EAction entity);

	void deleteAction(long id);

	void addPerm(long uid, long aid);

	void deletePerm(long uid, long aid);

	void addNegPerm(long uid, long aid);

	void deleteNegPerm(long uid, long aid);
	*/

}
