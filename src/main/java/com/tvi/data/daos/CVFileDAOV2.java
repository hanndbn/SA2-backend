/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.mysql.jdbc.Util;
import com.tvi.common.IDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class CVFileDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean add(long cvid_in, String data_in) {
        boolean sucess = false;
        try {
            String sql = "INSERT INTO CVFILE(cvid,file,ctime) Values (?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setString(2, data_in);
            stmt.setLong(3, (new java.util.Date().getTime()));
            sucess = stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVFileDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public String getFile(long cvid_in){
        String sucess = null;
        try {
            String sql = "SELECT file from cvfile where cvid = ? and ctime = (SELECT max(ctime) from cvfile where cvid = ?)";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setLong(2, cvid_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()){
                sucess = mRs.getString(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVFileDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
}
