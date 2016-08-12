/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

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
public class CVTestDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean add(long cvid_in, long testid_in) {
        boolean sucess = false;
        try {
            String sql = "INSERT INTO Cvtest(testid,cvid) VALUES(?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, testid_in);
            stmt.setLong(2, cvid_in);
            sucess = stmt.execute();
            stmt.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(CVTestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public long getCV (long testid_in){
        long cv = -1;
        try {
            String sql = "Select cvid from cvtest where testid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, testid_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()){
                cv = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVTestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return  cv;
    }
}
