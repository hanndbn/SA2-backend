package com.tvi.apply.util.log;

import com.tvi.apply.util.database.IDatabase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HttpLogger implements ILogExtracter
{
	private final IDatabase database;
	private final SimpleDateFormat dateformater;

	public HttpLogger(IDatabase database)
	{
		this.dateformater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.database = database;
	}

	private String getIp(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Real-IP");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip))
		{
			// get first ip from proxy ip
			int index = ip.indexOf(',');
			if (index != -1)
			{
				return ip.substring(0, index);
			} else
			{
				return ip;
			}
		}
		return request.getRemoteAddr();
	}


	@Override
	public Log extract(HttpServletRequest request, HttpServletResponse response, long reqtime, long restime, long userid)
	{

		String size = response.getHeader("Content-Length");

		size = size == null ? "-1" : size;

		String useragent = request.getHeader("User-Agent");

		String querystring = request.getQueryString();

		if (querystring == null)
		{
			querystring = "";
		}

		if (querystring.length() > 250)
		{
			querystring = querystring.substring(0, 249);
		}

		if (useragent == null) useragent = "";

		if (useragent.length() > 250)
		{
			useragent = useragent.substring(0, 249);
		}

		String path = request.getPathInfo();

		Calendar requesttime = Calendar.getInstance();
		requesttime.setTimeInMillis(reqtime);
		return new Log(path, requesttime, querystring, response.getStatus(), (int) restime, getIp(request), useragent, Integer.parseInt(size));

	}
}
