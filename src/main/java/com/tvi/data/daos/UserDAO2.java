/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.UserEntityV2;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class UserDAO2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public long add(UserEntityV2 User_in) {
        long sucess = -1;
        try {
            String sql = "INSERT INTO User(fullname,ctime,state) VALUES(?,?,?);";
            PreparedStatement stmt = database.preparedStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, User_in.fullname);
            stmt.setLong(2, (new java.util.Date().getTime()));
            stmt.setInt(3, User_in.state);
            stmt.executeUpdate();
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sucess;
    }
    
    public UserEntityV2 getUser(long uid_in) {
        //
        UserEntityV2 tempUserEntity = null;
        String fullName;
        Date cTime;
        int state;
        int unit;
        //
        try {
            String strSQL = "SELECT fullname,ctime,state FROM User WHERE uid = ? and state != ?";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(1, uid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                fullName = mRs.getString(1);
                cTime = new Date(mRs.getLong(2));
                state = mRs.getInt(3);
                tempUserEntity = new UserEntityV2(uid_in, fullName, cTime, state);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tempUserEntity;
    }
    
    
    public boolean update(UserEntityV2 User_in) {
        boolean sucess = false;
        try {
            String sql = "UPDATE User SET fullname=?, ctime=?, state=? WHERE uid=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(4, User_in.id);
            stmt.setString(1, User_in.fullname);
            stmt.setLong(2, (User_in.ctime.getTime()));
            stmt.setInt(3, User_in.state);
            stmt.execute();
            stmt.close();
            sucess = true;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public boolean moveUserToUnit (long userId_in, int unitId_in){
        boolean sucess = false;
        try {
            
            String sql = "INSERT INTO "
                    + database.getDatabaseName() +".In(userid,unitid,ctime) VALUES(?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, userId_in);
            stmt.setInt(2, unitId_in);
            stmt.setLong(3, (new java.util.Date().getTime()));
            sucess = stmt.execute();
            
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public boolean moveUserOutUnit (long userId_in, int unitId_in){
        boolean sucess = false;
        try {
            String sql = "DELETE FROM "
                    +database.getDatabaseName()+".In WHERE unitid = ? and userid = ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(2, userId_in);
            stmt.setInt(1, unitId_in);
            sucess =  stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    

}


