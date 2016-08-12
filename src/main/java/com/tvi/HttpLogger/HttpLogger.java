package com.tvi.HttpLogger;

import com.tvi.common.IDatabase;
import com.tvi.common.ILogger;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

public class HttpLogger implements ILogger {

    private final IDatabase database;
    private final SimpleDateFormat dateformater;

    public HttpLogger(IDatabase database) {
        this.dateformater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.database = database;
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            // get first ip from proxy ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    @Override
    public void log(HttpServletRequest request, HttpServletResponse response, long reqtime, long restime, long userid) {
        String size = response.getHeader("Content-Length");
        if (size == null) {
            size = "-1";
        }
        String useragent = StringEscapeUtils.escapeSql(getUserAgent(request));
        String querystring = StringEscapeUtils.escapeSql(request.getQueryString());
        if (querystring == null) {
            querystring = "";
        }
        if (querystring.length() > 250) {
            querystring = querystring.substring(0, 249);
        }

		if(useragent == null)
			useragent = "";

		if(useragent.length() > 250) {
			useragent = useragent.substring(0,249);
		}
        String reqt = this.dateformater.format(new Date(reqtime)) + "." + reqtime % 1000;

        String path = StringEscapeUtils.escapeSql(request.getPathInfo());
        String query = String.format("insert into log(ip,uid,time, restime,code,url, useragent,size,param) values('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s', '%8$s', '%9$s');",
                getIp(request), userid, reqt, restime, response.getStatus(), path, useragent, size, querystring);

        Statement statement = this.database.createStatement();
        try {
            statement.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(HttpLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void listLogByUser(long userid, int p, int ps, String s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
