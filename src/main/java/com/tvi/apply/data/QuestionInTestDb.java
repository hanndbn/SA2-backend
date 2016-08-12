package com.tvi.apply.data;

import com.tvi.apply.util.database.IDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionInTestDb
{

    private IDatabase database;

	public QuestionInTestDb(IDatabase database)
	{
		this.database = database;
	}

    public boolean add(long tid_in, long qid_in) {
        boolean sucess = false;
        try {
            String strSQL = "INSERT INTO questionintest(tid,qid) VALUES(?,?);";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(1, tid_in);
            stmt.setLong(2, qid_in);
            sucess = stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
			throw new RuntimeException(ex);
        }
        return sucess;
    }
    
    public List<Long> getQuestion(long testid_in){
        List<Long> listqid = new ArrayList<Long>();
        try {
            String sql  = "SELECT questionintest.qid FROM questionintest,question where questionintest.tid = ? and question.qid = questionintest.qid and question.state != ?  ; ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, testid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()){
                listqid.add(mRs.getLong(1));
            }
            
        } catch (SQLException ex) {
			throw new RuntimeException(ex);
        }
        return listqid;
    } 
    
}
