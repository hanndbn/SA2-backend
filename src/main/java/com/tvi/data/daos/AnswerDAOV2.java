/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.TestAnswerEntity;
import com.tvi.common.util.StringUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class AnswerDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public int getAnswerByQuestion(long answerid_in, long qid_in) {
        int answer = -1;
        try {
            String sql = "Select answer from Answer where taid = ? and qid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, answerid_in);
            stmt.setLong(2, qid_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                answer = mRs.getInt(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }

    public int add(long answerid_in, long qid_in, int answer) {
        int point = 0;
        try {
            
            //
            String sql = "INSERT INTO Answer(taid,qid,answer) VALUES(?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, answerid_in);
            stmt.setLong(2, qid_in);
            stmt.setInt(3, answer);
            stmt.execute();
            stmt.close();
            
            //
            String sql2 = "Select weight from question where qid = ? and answer = ? and state != ? and "
                    + "ctime = (Select max(ctime) from question where qid = ? and answer = ? and state != ? ) ";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setLong(1, qid_in);
            stmt2.setInt(2, answer);
            stmt2.setInt(3, ConfigCount.DELETED);
            stmt2.setLong(4, qid_in);
            stmt2.setInt(5, answer);
            stmt2.setInt(6, ConfigCount.DELETED);    
            ResultSet mRs2 = stmt2.executeQuery();
            if (mRs2.next()){
                point = mRs2.getInt(1);
            }     
            mRs2.close();
            stmt2.close();
            //
            
                    
        } catch (SQLException ex) {
            Logger.getLogger(AnswerDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return point;
    }
    
    
    

}
