/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class QuestionInTestDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean add(long tid_in, long qid_in) {
        boolean sucess = false;
        try {
            String strSQL = "INSERT INTO Questionintest(tid,qid) VALUES(?,?);";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(1, tid_in);
            stmt.setLong(2, qid_in);
            sucess = stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(QuestionInTestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public List<Long> getQuestion(long testid_in){
        List<Long> listqid = new ArrayList<Long>();
        try {
            String sql  = "SELECT Questionintest.qid FROM Questionintest,Question where Questionintest.tid = ? and question.qid = Questionintest.qid and question.state != ?  ; ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, testid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()){
                listqid.add(mRs.getLong(1));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(QuestionInTestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listqid;
    } 
    
}
