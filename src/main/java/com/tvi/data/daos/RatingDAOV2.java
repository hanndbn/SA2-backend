/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.entity.CVRatingEntityV2;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
@Deprecated
public class RatingDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public long add(long cvid_in, CVRatingEntityV2 rating_in) {
            long sucess = -1;
        try {
            String strSQL = "INSERT INTO Rating(cvid,iq,pres,arch,sum,total,rating,pote,eng,state,ctime) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = database.prepareStatement(strSQL);
            stmt.setLong(2, cvid_in);
            stmt.setInt(3, rating_in.iq);
            stmt.setInt(4, rating_in.pres);
            stmt.setInt(5, rating_in.arch);
            stmt.setInt(6, rating_in.sum);
            stmt.setInt(7, rating_in.total);
            stmt.setInt(8, rating_in.rating);
            stmt.setInt(9, rating_in.pote);
            stmt.setInt(10, rating_in.eng);
            stmt.setInt(11, rating_in.state);
            stmt.setTimestamp(12, new java.sql.Timestamp(new java.util.Date().getTime()));
            stmt.execute();            
            
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()){
                sucess = mRs.getLong(1);            
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(RatingDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
}

