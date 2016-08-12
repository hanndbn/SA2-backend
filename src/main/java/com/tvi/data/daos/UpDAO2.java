/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.util.StringUtil;
import com.tvi.data.entity.UpEntity2;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class UpDAO2 {

    private IDatabase database;

    private long uid;

    private String username;

    private byte[] password;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean add(UpEntity2 upEntity_in) {
        boolean sucess = false;
        try {
            String strSQL = "INSERT INTO up (uid,username,password) VALUES (?,?,?)";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(1, upEntity_in.uid);
            stmt.setString(2, upEntity_in.username);
            stmt.setBytes(3, upEntity_in.password);
            stmt.execute();
            stmt.close();
            sucess = true;
        } catch (SQLException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public boolean update(UpEntity2 upEntity_in) {
        boolean sucess = false;
        try {
            String sql = "UPDATE Up SET  username=?, password=? WHERE uid=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(3, upEntity_in.uid);
            stmt.setString(1, upEntity_in.username);
            stmt.setBytes(2, upEntity_in.password);
            stmt.execute();
            stmt.close();
            sucess = true;
        } catch (SQLException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sucess;
    }

    public long checkUp(String username_in, String password_in) {
        long sucess = -1;
        try {
            String sql = "SELECT uid from up WHERE username=? and password=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setString(1, username_in);
            stmt.setBytes(2, StringUtil.createPassword(password_in));
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;

    }

    public String resertPassword(long userId_in) {
        String newPass = null;
        try {
            
            String sql = "UPDATE Up SET  password=? WHERE uid=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(2, userId_in);
            newPass = StringUtil.randomString();
            stmt.setBytes(1, StringUtil.createPassword(newPass));
            stmt.execute();
            stmt.close();
            
            String sql1 = "SELECT uid from up WHERE uid = ? ";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setLong(1,userId_in);
            ResultSet mRs1 = stmt1.executeQuery();
            if (!mRs1.next()){
                newPass = null;
            }
            mRs1.close();
            stmt1.close();

        } catch (SQLException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newPass;
    }

    public long checkUp(String username_in) {
        long sucess = -1;
        try {
            String sql = "SELECT uid from up WHERE username=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setString(1, username_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UpDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    
    public String getUserName (long uid_in) {
        //
        
        String fullName = null;
        //
        try {
            String strSQL = "SELECT username FROM Up WHERE uid = ?";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(1, uid_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                fullName = mRs.getString(1);  
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fullName;
    }
    
}
