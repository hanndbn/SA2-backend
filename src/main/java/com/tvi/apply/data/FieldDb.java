//DINHNV
package com.tvi.apply.data;

import com.tvi.apply.data.entity.EField;
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

public class FieldDb {

    private IDatabase database;

    public FieldDb(IDatabase database) {
        this.database = database;
    }

    public long createField(EField entity) {
        long sucess = -1;
        try {
            Calendar cal=Calendar.getInstance();
            String sql = "insert into apply2.field(name,ctime,lmtime,state,creator) values(?,?,?,?,?)";
            PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ParamHelper.create(stmt).set(entity.name).set(cal).set(entity.lmtime).set(entity.state).set(entity.creator);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                sucess = rs.getLong(1);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return sucess;
    }

    public EField readField(long id) {
        EField field = new EField();
        try {

            String sql = "select name,ctime,lmtime,state,creator from apply2.field where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ResultHelper hr = new ResultHelper(rs);
                field.id = id;
                field.name = hr.s();
                field.ctime = hr.t();
                field.lmtime = hr.t();
                field.state = hr.i();
                field.creator = hr.l();
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return field;
    }

    public void deleteField(long id) {
    }

    public void updateField(EField entity) {
        try {
            String sql = "update apply2.field set name=?,ctime=?,lmtime=?,state=?,creator=? where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(entity.name).set(entity.ctime).set(entity.lmtime).set(entity.state).set(entity.creator).set(entity.id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<EField> listField(boolean archived, String keyword, int n, int p) {
        List<EField> eField = new ArrayList<EField>();
        if (archived == true) {
            try {
                String sql = "select id,name,ctime,lmtime,state,creator from apply2.field where name like ? order by ctime desc limit ?,?";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%"+keyword+"%").set(n * p).set(n);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    ResultHelper rh = new ResultHelper(rs);
                    EField field = new EField();
                    field.id = rh.l();
                    field.name = rh.s();
                    field.ctime = rh.t();
                    field.lmtime = rh.t();
                    field.state = rh.i();
                    field.creator = rh.l();
                    eField.add(field);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } else {

            try {
                String sql = "select id,name,ctime,lmtime,state,creator from apply2.field where (state <> -1) and name like ? order by ctime desc limit ?,?";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%" + keyword + "%").set(n * p).set(n);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    ResultHelper rh = new ResultHelper(rs);
                    EField field = new EField();
                    field.id = rh.l();
                    field.name = rh.s();
                    field.ctime = rh.t();
                    field.lmtime = rh.t();
                    field.state = rh.i();
                    field.creator = rh.l();
                    eField.add(field);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return eField;
    }

    public long countField(boolean archived, String keyword) {
        long count = -1;
        if (archived == true) {
            try {
                String sql = "select count(id) from apply2.field where name like ? order by ctime desc";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%"+keyword+"%");
                ResultSet rs = stmt.executeQuery();
                rs.next();
                count = rs.getLong(1);
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } else {
            try {
                String sql = "select count(id) from apply2.field where (state <> -1) and name like ? order by ctime desc";
                PreparedStatement stmt = database.prepareStatement(sql);
                ParamHelper.create(stmt).set("%"+keyword+"%");
                ResultSet rs = stmt.executeQuery();
                rs.next();
                count = rs.getLong(1);
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return count;
    }
}
