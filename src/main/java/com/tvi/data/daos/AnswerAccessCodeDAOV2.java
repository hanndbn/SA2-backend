/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.util.StringUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class AnswerAccessCodeDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public long getAnswerByAnswerCode(String answerCode_in) {
        long sucess = -1;
        try {
            String sql = "Select taid From answeraccesscode WHERE code = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setString(1, answerCode_in);
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

    public String getAnswerCodeByAnswerId(long answerid_in) {
        String sucess = null;
        try {
            String sql = "Select code From answeraccesscode WHERE taid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, answerid_in);
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

    public boolean deleteAnswerCode(String answercode_in) {
        boolean sucess = false;
        try {
            String sql = "DELETE FROM Answeraccesscode WHERE code = ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setString(1, answercode_in);
            stmt.execute();
            sucess = true;

        } catch (SQLException ex) {
            Logger.getLogger(AnswerAccessCodeDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    
}
