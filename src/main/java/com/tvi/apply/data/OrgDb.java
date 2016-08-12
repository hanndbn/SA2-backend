//DinhNV
package com.tvi.apply.data;

import com.tvi.apply.data.entity.EOrg;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrgDb {

    private IDatabase database;

    public OrgDb(IDatabase database) {
        this.database = database;
    }

    public long createOrg(EOrg entity) {
        long sucess = -1;
        try {
            Calendar cal=Calendar.getInstance();
            String sql = "insert into apply2.org(name,picture,ctime,lmtime,state,description) values(?,?,?,?,?,?)";
            PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ParamHelper.create(stmt).set(entity.name).set(entity.picture).set(cal).set(entity.lmtime).set(entity.state).set(entity.description);
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

    public EOrg readOrg(long id) {
        EOrg org = new EOrg();
        try {

            String sql = "select name,picture,ctime,lmtime,state,description from apply2.org where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ResultHelper rh = new ResultHelper(rs);
                org.id = id;
                org.name = rh.s();
                org.picture = rh.s();
                org.ctime = rh.t();
                org.lmtime = rh.t();
                org.state = rh.i();
                org.description = rh.s();
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return org;
    }

    public void deleteOrg(long id) {

    }

    public void updateOrg(EOrg entity) {
        try {
            String sql = "update apply2.org set name=?,picture=?,ctime=?,lmtime=?,state=?,description=?  where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(entity.name).set(entity.picture).set(entity.ctime).set(entity.lmtime).set(entity.state).set(entity.description).set(entity.id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<EOrg> listOrg(boolean archived, String keyword, int n, int p) {
        List<EOrg> lorg = new ArrayList<EOrg>();
        if (archived == true) {
            try {
                String sql = "select id,name,picture,ctime,lmtime,state,description from apply2.org where name like ? or picture like ? or description like ? order by ctime desc limit ?,?";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set(p * n).set(n);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    EOrg org = new EOrg();
                    ResultHelper rh = new ResultHelper(rs);
                    org.id = rh.l();
                    org.name = rh.s();
                    org.picture = rh.s();
                    org.ctime = rh.t();
                    org.lmtime = rh.t();
                    org.state = rh.i();
                    org.description = rh.s();

                    lorg.add(org);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } else {
            try {
                String sql = "select id,name,picture,ctime,lmtime,state,description from apply2.org where (state <> -1) and name like ? or picture like ? or description like ? order by ctime desc limit ?,?";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set(p * n).set(n);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    EOrg org = new EOrg();
                    ResultHelper rh = new ResultHelper(rs);
                    org.id = rh.l();
                    org.name = rh.s();
                    org.picture = rh.s();
                    org.ctime = rh.t();
                    org.lmtime = rh.t();
                    org.state = rh.i();
                    org.description = rh.s();

                    lorg.add(org);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return lorg;
    }

    public long countOrg(boolean archived, String keyword) {
        long count = -1;
        if (archived = true) {
            try {
                String sql = "select count(id) from apply2.org where name like ? or picture like ? or description like ? order by ctime desc";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    count = rs.getLong(1);
                }
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            try {
                String sql = "select count(id) from apply2.org where (state <> -1) and name like ? or picture like ? or description like ? order by ctime desc";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    count = rs.getLong(1);
                }
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return count;
    }
}
