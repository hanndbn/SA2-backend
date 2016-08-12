//DINHNV
package com.tvi.apply.data;

import com.tvi.apply.data.entity.EActionLog;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ActionLogDb {

    private IDatabase database;

    public ActionLogDb(IDatabase database) {
        this.database = database;
    }

    public long add(long userid, EActionLog entity) {
        long sucess = -1;
        try {
            Calendar cal = Calendar.getInstance();
            String sql = "insert into apply2.actionlog(ctime,uid,action,subject,tag) values (?,?,?,?,?)";
            PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ParamHelper.create(stmt).set(cal).set(userid).set(entity.action).set(entity.subject).set(entity.tag);
            stmt.executeUpdate();
            ResultSet result = stmt.getGeneratedKeys();
            if (result.next()) {
                sucess = result.getLong(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return sucess;
    }

    public List<EActionLog> listActionLog(long userid, int n, int p) {
        List<EActionLog> actionLog = new LinkedList<EActionLog>();
        try {
            String sql = "select id,ctime,action,subject,tag from actionlog where uid like ? order by ctime desc limit ?,?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set("%" + userid + "%").set(p * n).set(n);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EActionLog actionlog = new EActionLog();
                ResultHelper rh = new ResultHelper(rs);
                actionlog.id = rh.l();
                actionlog.ctime = rh.t();
                actionlog.action = rh.s();
                actionlog.subject = rh.s();
                actionlog.tag = rh.s();
                actionLog.add(actionlog);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return actionLog;
    }

    public long countActionLog(long uid) {
        long count = -1;
        try {

            String sql = "select count(id) from actionlog where uid like ? order by ctime desc";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set("%" + uid + "%");
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                count = result.getLong(1);
            }
            result.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return count;
    }

}
