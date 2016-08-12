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
public class TestAccessCodeV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public long getTestByTestCode(String testCode_in) {
        long sucess = -1;
        try {
            String sql = "Select testid From Testaccesscode WHERE code = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setString(1, testCode_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public String getTestCodeByTestId(long testid_in) {
        String sucess = null;
        try {
            String sql = "Select code From Testaccesscode WHERE testid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, testid_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                sucess = mRs.getString(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sucess != null && sucess.length() > 68) {
            return sucess.substring(0, 68);
        }
        return sucess;
    }

    public boolean deleteTestCode(String testcode_in) {
        boolean sucess = false;
        try {
			try
			{
				throw new Exception("deleting " + testcode_in);
			}catch (Exception ex) {
				Logger.getLogger(TestAccessCodeV2.class.getName()).log(Level.SEVERE, null, ex);
			}
            String sql = "DELETE FROM Testaccesscode WHERE code = ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setBytes(1, testcode_in.getBytes());            
            stmt.execute();
            sucess = true;

        } catch (SQLException ex) {
            Logger.getLogger(TestAccessCodeV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
}
