
package com.tvi.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ILogger
{
	void log( HttpServletRequest request, HttpServletResponse response, long reqtime, long restime,long userid );
	void listLogByUser(long userid, int p, int ps,String s);
}
