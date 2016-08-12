
package com.tvi.apply.business;

public interface IAuth
{
	long matchAuth(String auth);

	String login(long userid);

	void logout(String auth);
}
