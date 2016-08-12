package com.tvi.apply.util.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILogExtracter
{
	Log extract(HttpServletRequest request,HttpServletResponse response, long reqtime, long restime, long userid);
}
