/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.EmailFormEntity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class EmailFormDAO {

    private IDatabase database;

    private long id;
    private String title;
    private String body;
    private String signature;
    private int type;
    private long creator;
    private Date ctime;
    private int state;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public long add(int unit_in, EmailFormEntity emailform_in) {
        long sucess = -1;
        try {
            String strSQL = "INSERT INTO Emailform(title,body,signature,type,creator,ctime,state,unit) VALUES(?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = database.preparedStatement(strSQL,Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, emailform_in.title);
            stmt.setString(2, emailform_in.body);
            stmt.setString(3, emailform_in.signature);
            stmt.setInt(4, emailform_in.type);
            stmt.setLong(5, emailform_in.creator);
            stmt.setLong(6, (new java.util.Date().getTime()));
            stmt.setInt(7, emailform_in.state);
            stmt.setInt(8, unit_in);
            stmt.execute();
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sucess;
    }

    public boolean update(long formid_in, EmailFormEntity emailform_in) {
        boolean sucess = false;
        try {
            int unit = -1;
            String sql1 = "Select unit from emailform "
                    + "where efid = ? and state != ? and "
                    + "ctime = (Select max(ctime)from emailform where efid = ? and state != ? )";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setLong(1, formid_in);
            stmt1.setInt(2, ConfigCount.DELETED);
            stmt1.setLong(3, formid_in);
            stmt1.setInt(4, ConfigCount.DELETED);
            ResultSet mRs1 = stmt1.executeQuery();
            if (mRs1.next()) {
                unit = mRs1.getInt(1);
            } else {throw new RuntimeException("Khong lay duoc unit");}
            mRs1.close();
            stmt1.close();

            String sql2 = "Update Emailform SET state = ? where efid = ?";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setInt(1, ConfigCount.DELETED);
            stmt2.setLong(2, formid_in);
            stmt2.execute();
            stmt2.close();

            String sql = "INSERT INTO Emailform(title,body,signature,type,creator,ctime,state,unit,efid) VALUES(?,?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(9, formid_in);
            stmt.setString(1, emailform_in.title);
            stmt.setString(2, emailform_in.body);
            stmt.setString(3, emailform_in.signature);
            stmt.setInt(4, emailform_in.type);
            stmt.setLong(5, emailform_in.creator);
            stmt.setLong(6, (new java.util.Date().getTime()));
            stmt.setInt(7, emailform_in.state);
            stmt.setInt(8, unit);
            sucess = stmt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public boolean delete(long formid_in) {
        boolean sucess = false;
        try {
            String sql2 = "Update Emailform SET state = ? where efid = ?";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setInt(1, ConfigCount.DELETED);
            stmt2.setLong(2, formid_in);
            sucess = stmt2.execute();
            stmt2.close();
        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public List<EmailFormEntity> getAll(int unit_in) {
        List<EmailFormEntity> listEmailform = new ArrayList<EmailFormEntity>();
        try {
            String sql = "SELECT efid,title,body,signature,type,creator,ctime,state,unit FROM Emailform where unit = ? and state != ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setInt(1, unit_in);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getInt(1);
                title = mRs.getString(2);
                body = mRs.getString(3);
                signature = mRs.getString(4);
                type = mRs.getInt(5);
                creator = mRs.getLong(6);
                ctime = new java.sql.Date(mRs.getLong(7));
                state = mRs.getInt(8);
                listEmailform.add(new EmailFormEntity(id, title, body, signature, type, creator, ctime, state));
            }

        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listEmailform;
    }
    
    
    public EmailFormEntity getEmailFormEntity (long emailform_in){
        EmailFormEntity emailform = null;
        try {
            String sql = "SELECT efid,title,body,signature,type,creator,ctime,state,unit FROM Emailform where efid = ? and state != ? and ctime = (Select max(ctime) from Emailform where efid = ? and state != ? ) ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setLong(1, emailform_in);
            stmt.setInt(4, ConfigCount.DELETED);
            stmt.setLong(3, emailform_in);
            ResultSet mRs = stmt.executeQuery();
               if (mRs.next()) {
                id = mRs.getInt(1);
                title = mRs.getString(2);
                body = mRs.getString(3);
                signature = mRs.getString(4);
                type = mRs.getInt(5);
                creator = mRs.getLong(6);
                ctime = new java.sql.Date(mRs.getLong(7));
                state = mRs.getInt(8);
                emailform = new EmailFormEntity(id, title, body, signature, type, creator, ctime, state);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
         return emailform;
                
    
    }
  
    
    public EmailFormEntity getEmailFormEntity (int unit_in,int type_in){
        EmailFormEntity emailform = null;
        try {
            String sql = "SELECT efid,title,body,signature,type,creator,ctime,state,unit FROM Emailform where unit = ? and type = ? and state != ? and ctime = (Select max(ctime) from Emailform where unit = ? and type = ? and state != ? ) ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(3, ConfigCount.DELETED);
            stmt.setInt(1, unit_in);
            stmt.setInt(2, type_in);
            stmt.setInt(6, ConfigCount.DELETED);
            stmt.setInt(4, unit_in);
            stmt.setInt(5, type_in);
            ResultSet mRs = stmt.executeQuery();
               if (mRs.next()) {
                id = mRs.getInt(1);
                title = mRs.getString(2);
                body = mRs.getString(3);
                signature = mRs.getString(4);
                type = mRs.getInt(5);
                creator = mRs.getLong(6);
                ctime = new java.sql.Date(mRs.getLong(7));
                state = mRs.getInt(8);
                emailform = new EmailFormEntity(id, title, body, signature, type, creator, ctime, state);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
         return emailform;
                
    
    }
    
    
    public int getUnit (long emailform_in){
       int unit = -1;
        try {
            String sql = "SELECT unit FROM Emailform where efid = ? and state != ? and ctime = (Select max(ctime) from Emailform where efid = ? and state != ? ) ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setLong(1, emailform_in);
            stmt.setInt(4, ConfigCount.DELETED);
            stmt.setLong(3, emailform_in);
            ResultSet mRs = stmt.executeQuery();
               if (mRs.next()) {
                unit = mRs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailFormDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
         return unit;
                
    
    }
}


