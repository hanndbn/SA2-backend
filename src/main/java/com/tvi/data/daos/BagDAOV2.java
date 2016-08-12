/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.CVBagEntity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class BagDAOV2 {

    private IDatabase database;
    private String name;
    private long id;
    private int state;
    private Date ctime;
    private long creator;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public List<CVBagEntity> listBagInRequest(long requestid_in) {
        List<CVBagEntity> list = new ArrayList<CVBagEntity>();
        try {
            String sql = "SELECT bid,name,ctime,state FROM Bag where request = ? and state != ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, requestid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getLong(1);
                name = mRs.getString(2);
                ctime = new java.util.Date(mRs.getLong(3));
                state = mRs.getInt(4);
                list.add(new CVBagEntity(name, id, state, ctime, creator));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public long add(long requestid, CVBagEntity bag_in) {
        long sucess = -1;
        try {
            String strSQL = "INSERT INTO Bag(name,ctime,state,request) VALUES(?,?,?,?);";
            PreparedStatement stmt = database.preparedStatement(strSQL,Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, bag_in.name);
            stmt.setLong(2,new java.util.Date().getTime());
            stmt.setInt(3, bag_in.state);
            stmt.setLong(4, requestid);
            stmt.execute();
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public boolean update(long bagid_in, CVBagEntity bag_in) {
        boolean sucess = false;
        try {
            int rid = -1;
            String sql2 = "SELECT request From bag where bid = ? and state != ? and ctime = (SELECT max(ctime) from bag where bid = ? and state != ?) ;";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setLong(1, bagid_in);
            stmt2.setInt(2, ConfigCount.DELETED);
            stmt2.setLong(3, bagid_in);
            stmt2.setInt(4, ConfigCount.DELETED);
            ResultSet mRs2 = stmt2.executeQuery();
            if (mRs2.next()) {
                rid = mRs2.getInt(1);
            } else { throw new RuntimeException("Khong Up date duoc");}
            mRs2.close();
            stmt2.close();

            String sql1 = "UPDATE bag SET state = ? where bid = ?";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setInt(1, ConfigCount.DELETED);
            stmt1.setLong(2, bagid_in);
            stmt1.execute();
            stmt1.close();

            String sql = "INSERT INTO Bag(bid,name,ctime,state,request) VALUES(?,?,?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, bagid_in);
            stmt.setString(2, bag_in.name);
            stmt.setLong(3, (new java.util.Date().getTime()));
            stmt.setInt(4, bag_in.state);
            stmt.setLong(5, rid);
            stmt.execute();
            stmt.close();
            sucess = true;
            
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public CVBagEntity getBag(long bagid) {
        CVBagEntity cvBag = null;
        try {
            String sql = "SELECT bid,name,ctime,state FROM Bag where bid = ? and state != ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, bagid);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                id = mRs.getLong(1);
                name = mRs.getString(2);
                ctime = new java.util.Date(mRs.getLong(3));
                state = mRs.getInt(4);
                cvBag = new CVBagEntity(name, id, state, ctime, creator);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cvBag;
    }

    public int getUnit(long bag_in) {
        int unit = -1;
        try {
            String sql = "SELECT Request.unitid "
                    + " FROM Request,bag Where request.rid = bag.request and bag.bid = ? and bag.state != ? and request.state != ?";

            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, bag_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setInt(3, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                unit = mRs.getInt(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return unit;
    }

    public long getRequest(long bag_in) {
        long rid = -1;
        try {
            String sql = "SELECT Request.rid "
                    + " FROM Request,bag Where request.rid = bag.request and bag.bid = ? and bag.state != ? and request.state != ?";

            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, bag_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setInt(3, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                rid = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rid;
    }

    public boolean delete (long bagid_in){
        boolean sucess = false;
        try {
            String sql1 = "UPDATE bag SET state = ? where bid = ?";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setInt(1, ConfigCount.DELETED);
            stmt1.setLong(2, bagid_in);
            stmt1.execute();
            stmt1.close();
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
    
    public long getFist (long requestid_in){
        long bagid = -1;
        try {
            String sql = "Select bid from bag where request = ? and state != ? order by ctime"  ;
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, requestid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()){
                bagid = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bagid;
    }
    
    public long getSecond (long requestid_in){
         long bagid = -1;
        try {
            String sql = "Select bid from bag where request = ? and state != ? order by ctime"  ;
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, requestid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()){
                if (mRs.next())
                bagid = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(BagDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bagid;
    }
}
