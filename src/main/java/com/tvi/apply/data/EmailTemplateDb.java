//DINHNV
package com.tvi.apply.data;

import com.tvi.apply.business.core.IEmailTemplateMgt;
import com.tvi.apply.data.entity.EEmailTemplate;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailTemplateDb implements IEmailTemplateMgt {

    private IDatabase database;

    public EmailTemplateDb(IDatabase database) {
        this.database = database;
    }

    @Override
    public long createTemplate(EEmailTemplate entity) {
        long sucess = -1;

        try {
            Calendar cal=Calendar.getInstance();
            String sql = "insert into apply2.emailtemplate (ctime,state,lmtime,creator,name,content,attachment) values (?,?,?,?,?,?,?)";
            PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ParamHelper.create(stmt).set(cal).set(entity.state).set(entity.lmtime).set(entity.creator).set(entity.name).set(entity.content).set(entity.attachment);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                sucess = rs.getLong(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return sucess;

    }

    @Override
    public EEmailTemplate readTemplate(long id) {
        EEmailTemplate email = new EEmailTemplate();
        try {
            String sql = "select ctime,state,lmtime,creator,name,content,attachment from apply2.emailtemplate where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ResultHelper rh = new ResultHelper(rs);
                email.id = id;
                email.ctime = rh.t();
                email.state = rh.i();
                email.lmtime = rh.t();
                email.creator = rh.l();
                email.name = rh.s();
                email.content = rh.s();
                email.attachment = rh.s();
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return email;
    }

    @Override
    public void deleteTemplate(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTemplate(EEmailTemplate entity) {
        try {
            String sql = "update apply2.emailtemplate set ctime=?, state=?,lmtime=?,creator=?,name=?,content=?,attachment=? where id=?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set(entity.ctime).set(entity.state).set(entity.lmtime).set(entity.creator).set(entity.name).set(entity.content).set(entity.attachment).set(entity.id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<EEmailTemplate> listTemplate(String keyword, int n, int p) {
        List<EEmailTemplate> lEmail = new ArrayList<EEmailTemplate>();
        try {
            String sql = "select id,ctime,state,lmtime,creator,name,content,attachment from apply2.emailtemplate where name like ? or content like ? or attachment like ? order by ctime desc limit ?,?";
            PreparedStatement stmt = database.prepareStatement(sql);
            ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set(p * n).set(n);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ResultHelper rh = new ResultHelper(rs);
                EEmailTemplate email = new EEmailTemplate();
                email.id = rh.l();
                email.ctime = rh.t();
                email.state = rh.i();
                email.lmtime = rh.t();
                email.creator = rh.l();
                email.name = rh.s();
                email.content = rh.s();
                email.attachment = rh.s();
                lEmail.add(email);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lEmail;
    }

    @Override
    public long countTemplate(String keyword) {
        long count = 0;

        try {
            String sql = "select count(id) from apply2.emailtemplate where name like ? or content like ? or attachment like ? order by ctime desc";
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
        return count;
    }

}
