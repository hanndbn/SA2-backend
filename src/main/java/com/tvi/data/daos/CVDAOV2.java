/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.CVEntityV2;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Manh
 */
public class CVDAOV2 {

    private IDatabase database;

    private long id;

    private Date ctime;

    private int state;

    private int level;

    private String filename;

    private String name;

    private String email;

    private boolean cantesteng;

    private boolean cantestiq;

    private boolean iqtested;

    private boolean engtested;

    private int iq;

    private int pres;

    private int arch;

    private int sum;

    private int total;

    private int pote;

    private int eng;

    private boolean receivedmail;

    private boolean iqtestresultmail;

    private boolean engtestresultmail;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public CVEntityV2 getCVEntity(long cvid_in) {
        CVEntityV2 cv = null;
        try {
            String sql = "SELECT cvid,ctime,state,level,name,filename,email,"
                    + "engtested,cantestiq,cantesteng,iqtested,eng,iq,pote,total,sum,ach,pres,engtestresultmail,iqtestresultmail,receivedmail FROM Cv "
                    + "where cvid = ? and state != ? and ctime = (Select max(ctime) from cv where  cvid = ? and state != ? ) ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setLong(3, cvid_in);
            stmt.setInt(4, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                id = mRs.getLong(1);
                ctime = new Date(mRs.getLong(2));
                state = mRs.getInt(3);
                level = mRs.getInt(4);
                name = mRs.getString(5);
                filename = mRs.getString(6);
                email = mRs.getString(7);
                engtested = mRs.getBoolean(8);
                cantestiq = mRs.getBoolean(9);
                cantesteng = mRs.getBoolean(10);
                iqtested = mRs.getBoolean(11);
                eng = mRs.getInt(12);
                iq = mRs.getInt(13);
                pote = mRs.getInt(14);
                total = mRs.getInt(15);
                sum = mRs.getInt(16);
                arch = mRs.getInt(17);
                pres = mRs.getInt(18);
                engtestresultmail = mRs.getBoolean(19);
                iqtestresultmail = mRs.getBoolean(20);
                receivedmail = mRs.getBoolean(21);

                cv = new CVEntityV2(id, ctime, state, level, filename, name, email, cantesteng, cantestiq, iqtested, engtested, iq, pres, arch, sum, total, pote, eng, receivedmail, iqtestresultmail, engtestresultmail);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cv;
    }

    public long add(long bagid_in, CVEntityV2 cv_in) {
        long cvid = -1;
        try {
            String sql = "INSERT INTO Cv(ctime,state,level,name,filename,email,bagid,engtested,cantestiq,cantesteng,iqtested,eng,iq,pote,total,sum,ach,pres,engtestresultmail,iqtestresultmail,receivedmail) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = database.preparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, (new java.util.Date().getTime()));
            stmt.setInt(2, cv_in.state);
            stmt.setInt(3, cv_in.level);
            stmt.setString(4, cv_in.name);
            stmt.setString(5, cv_in.filename);
            stmt.setString(6, cv_in.email);
            stmt.setLong(7, bagid_in);
            stmt.setBoolean(8, cv_in.engtested);
            stmt.setBoolean(9, cv_in.cantestiq);
            stmt.setBoolean(10, cv_in.cantesteng);
            stmt.setBoolean(11, cv_in.iqtested);
            stmt.setInt(12, cv_in.eng);
            stmt.setInt(13, cv_in.iq);
            stmt.setInt(14, cv_in.pote);
            stmt.setInt(15, cv_in.total);
            stmt.setInt(16, cv_in.sum);
            stmt.setInt(17, cv_in.arch);
            stmt.setInt(18, cv_in.pres);
            stmt.setBoolean(19, cv_in.engtestresultmail);
            stmt.setBoolean(20, cv_in.iqtestresultmail);
            stmt.setBoolean(21, cv_in.receivedmail);
            stmt.execute();
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()) {
                cvid = mRs.getLong(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cvid;
    }

    public boolean update(long cvid_in, CVEntityV2 cv_in) {
        boolean sucess = false;
        try {
            long bagid = -1;
            String sql2 = "SELECT bagid From cv where cvid = ? and state != ? and ctime = (SELECT max(ctime) from cv where cvid = ? and state != ?) ;";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setLong(1, cvid_in);
            stmt2.setInt(2, ConfigCount.DELETED);
            stmt2.setLong(3, cvid_in);
            stmt2.setInt(4, ConfigCount.DELETED);
            ResultSet mRs2 = stmt2.executeQuery();
            if (mRs2.next()) {
                bagid = mRs2.getLong(1);
            }
            mRs2.close();
            stmt2.close();
            String sql1 = "UPDATE cv SET state = ? where cvid = ?";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setInt(1, ConfigCount.DELETED);
            stmt1.setLong(2, cvid_in);
            stmt1.execute();
            stmt1.close();

            String sql = "INSERT INTO Cv(cvid,ctime,state,level,name,filename,email,bagid,engtested,cantestiq,cantesteng,iqtested,eng,iq,pote,total,sum,ach,pres,engtestresultmail,iqtestresultmail,receivedmail) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setLong(2, (new java.util.Date().getTime()));
            stmt.setInt(3, cv_in.state);
            stmt.setInt(4, cv_in.level);
            stmt.setString(5, cv_in.name);
            stmt.setString(6, cv_in.filename);
            stmt.setString(7, cv_in.email);
            stmt.setLong(8, bagid);
            stmt.setBoolean(9, cv_in.engtested);
            stmt.setBoolean(10, cv_in.cantestiq);
            stmt.setBoolean(11, cv_in.cantesteng);
            stmt.setBoolean(12, cv_in.iqtested);

            stmt.setInt(13, cv_in.eng);
            stmt.setInt(14, cv_in.iq);
            stmt.setInt(15, cv_in.pote);
            stmt.setInt(16, cv_in.total);
            stmt.setInt(17, cv_in.sum);
            stmt.setInt(18, cv_in.arch);
            stmt.setInt(19, cv_in.pres);
            stmt.setBoolean(20, cv_in.engtestresultmail);
            stmt.setBoolean(21, cv_in.iqtestresultmail);
            stmt.setBoolean(22, cv_in.receivedmail);

            stmt.execute();
            stmt.close();
            sucess = true;
        } catch (SQLException ex) {
            Logger.getLogger(RequestDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public boolean detele(long cvid_in ) {
        TestAnswerDAOV2 TestAnswerDAOV2_in = new TestAnswerDAOV2();
        TestAnswerDAOV2_in.setDatabase(database);
        TestDAOV2 TestDAOV2_in = new TestDAOV2();
        TestDAOV2_in.setDatabase(database);
        boolean sucess = false;
        try {
            //update state
            String sql1 = "UPDATE cv SET state = ? where cvid = ?";
            PreparedStatement stmt1 = database.prepareStatement(sql1);
            stmt1.setInt(1, ConfigCount.DELETED);
            stmt1.setLong(2, cvid_in);
            stmt1.execute();
            stmt1.close();

            //update all follow field 
            String sql2 = "Select taid from cvtestanswer where cvid = ?";
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setLong(1, cvid_in);
            ResultSet mRs2 = stmt2.executeQuery();
            while (mRs2.next()){
                TestAnswerDAOV2_in.delete(mRs2.getLong(1));
            }
            mRs2.close();
            stmt2.close();
            
            
            String sql3 = "Select testid from cvtest where cvid = ?";
            PreparedStatement stmt3 = database.prepareStatement(sql3);
            stmt3.setLong(1, cvid_in);
            ResultSet mRs3 = stmt3.executeQuery();
            while (mRs3.next()){
                TestDAOV2_in.delete(mRs3.getLong(1));
            }
            mRs3.close();
            stmt3.close();
            sucess = true;

        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    /**
     *
     * @param bagid_in
     * @param p_in
     * @param ps_in
     * @param s_in
     * @return finished 4.1
     */
    public List<CVEntityV2> getCVInBag(long bagid_in, int p_in, int ps_in, String[] s_in) {
        List<CVEntityV2> cv = new ArrayList<CVEntityV2>();
        try {
            PreparedStatement stmt = null;
            String sql = "SELECT cvid, ctime,state,level,name,filename,email,engtested,eng,iq,pote,total,sum,ach,pres"
                        + ",engtestresultmail,iqtestresultmail,receivedmail,cantestiq,cantesteng,iqtested "
                        + "FROM Cv where state != ? and bagid = ? ";
            if (s_in == null || s_in.length < 1) {
                sql = sql + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setLong(2, bagid_in);
                stmt.setInt(1, ConfigCount.DELETED);
                stmt.setInt(3, p_in * ps_in);
                stmt.setInt(4, ps_in);

            }
            else {
                sql = sql + " order by ";
                for (int i = 0; i < s_in.length - 1; i++) {
                    sql =sql +StringEscapeUtils.escapeSql( s_in[i].replaceAll("[^\\w]", "") )+ " desc , ";
                }
                sql = sql +  StringEscapeUtils.escapeSql(s_in[s_in.length -1].replaceAll("[^\\w]", "")) + " desc " + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setLong(2, bagid_in);
                stmt.setInt(1, ConfigCount.DELETED);
                stmt.setInt(3, p_in * ps_in);
                stmt.setInt(4, ps_in);
            }
            if (stmt != null) {
                ResultSet mRs = stmt.executeQuery();
                while (mRs.next()) {
                    id = mRs.getLong(1);
                    ctime = new java.util.Date(mRs.getLong(2));
                    state = mRs.getInt(3);
                    level = mRs.getInt(4);
                    name = mRs.getString(5);
                    filename = mRs.getString(6);
                    email = mRs.getString(7);
                    engtested = mRs.getBoolean(8);
                    eng = mRs.getInt(9);
                    iq = mRs.getInt(10);
                    pote = mRs.getInt(11);
                    total = mRs.getInt(12);
                    sum = mRs.getInt(13);
                    arch = mRs.getInt(14);
                    pres = mRs.getInt(15);
                    engtestresultmail = mRs.getBoolean(16);
                    iqtestresultmail = mRs.getBoolean(17);
                    receivedmail = mRs.getBoolean(18);
                    cantestiq = mRs.getBoolean(19);
                    cantesteng = mRs.getBoolean(20);
                    iqtested = mRs.getBoolean(21);
                    cv.add(new CVEntityV2(id, ctime, state, level, filename, name, email, cantesteng, cantestiq, iqtested, engtested, iq, pres, arch, sum, total, pote, eng, receivedmail, iqtestresultmail, engtestresultmail));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cv;
    }

    /**
     *
     * @param bagid_in
     * @param p_in
     * @param ps_in
     * @param s_in
     * @return
     *
     * finished 4.1
     */
    public List<CVEntityV2> getCV(long bagid_in, int p_in, int ps_in, String[] s_in) {
        List<CVEntityV2> cv = new ArrayList<CVEntityV2>();
        try {
            PreparedStatement stmt = null;
            String sql = "SELECT Cv.cvid, Cv.ctime,Cv.state,Cv.level,Cv.name,Cv.filename,Cv.email"
                        + ",Cv.engtested,Cv.eng,Cv.iq,Cv.pote,Cv.total,Cv.sum,Cv.ach,Cv.pres"
                        + ",Cv.engtestresultmail,Cv.iqtestresultmail,Cv.receivedmail,Cv.cantestiq,Cv.cantesteng,Cv.iqtested "
                        + "FROM Cv, bag where Cv.state != ? and bag.state != ? and bag.bid = cv.bagid and bag.bid = ?";
                
            if (s_in == null||s_in.length<1) {
                sql = sql + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setLong(3, bagid_in);
                stmt.setInt(2, ConfigCount.DELETED);
                stmt.setInt(1, ConfigCount.DELETED);
                stmt.setInt(4, p_in * ps_in);
                stmt.setInt(5, ps_in);
            }
            else {
					//PREVENTING SQL INJECTION
                sql = sql + " order by ";
                for (int i = 0; i < s_in.length - 1; i++) {
                    sql =sql + StringEscapeUtils.escapeSql(s_in[i].replaceAll("[^\\w]", "")) + " desc, ";
                }
                sql = sql + StringEscapeUtils.escapeSql( s_in[s_in.length -1].replaceAll("[^\\w]", "")) + " desc " + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setLong(3, bagid_in);
                stmt.setInt(2, ConfigCount.DELETED);
                stmt.setInt(1, ConfigCount.DELETED);
                stmt.setInt(4, p_in * ps_in);
                stmt.setInt(5, ps_in);
            }
            if (stmt != null) {
                ResultSet mRs = stmt.executeQuery();
                while (mRs.next()) {
                    id = mRs.getLong(1);
                    ctime = new java.util.Date(mRs.getLong(2));
                    state = mRs.getInt(3);
                    level = mRs.getInt(4);
                    name = mRs.getString(5);
                    filename = mRs.getString(6);
                    email = mRs.getString(7);
                    engtested = mRs.getBoolean(8);
                    eng = mRs.getInt(9);
                    iq = mRs.getInt(10);
                    pote = mRs.getInt(11);
                    total = mRs.getInt(12);
                    sum = mRs.getInt(13);
                    arch = mRs.getInt(14);
                    pres = mRs.getInt(15);
                    engtestresultmail = mRs.getBoolean(16);
                    iqtestresultmail = mRs.getBoolean(17);
                    receivedmail = mRs.getBoolean(18);
                    cantestiq = mRs.getBoolean(19);
                    cantesteng = mRs.getBoolean(20);
                    iqtested = mRs.getBoolean(21);
                    cv.add(new CVEntityV2(id, ctime, state, level, filename, name, email, cantesteng, cantestiq, iqtested, engtested, iq, pres, arch, sum, total, pote, eng, receivedmail, iqtestresultmail, engtestresultmail));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cv;
    }

    public boolean update(long cvid_in, long bagid_in)  {
        boolean sucess = false;
        try {
            String sql = "UPDATE Cv SET bagid=? WHERE cvid=? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, bagid_in);
            stmt.setLong(2, cvid_in);
            sucess = stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public int getUnit(long cvid_in) {
        int unit = -1;
        try {
            String sql = "SELECT Request.unitid "
                    + " FROM Request,cv,bag Where request.rid = bag.request and bag.bid = cv.bagid and cv.cvid = ? and cv.state != ? and bag.state != ? and request.state != ?";

            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setInt(3, ConfigCount.DELETED);
            stmt.setInt(4, ConfigCount.DELETED);
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

    public long getRequest(long cvid_in) {
        long rid = -1;
        try {
            String sql = "SELECT Request.rid "
                    + " FROM Request,cv,bag Where request.rid = bag.request and bag.bid = cv.bagid and cv.cvid = ? and cv.state != ? and bag.state != ? and request.state != ?";

            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setInt(3, ConfigCount.DELETED);
            stmt.setInt(4, ConfigCount.DELETED);
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

    public long countCVinBag(long bagid_in) {
        long count = -1;
        try {
            String sql = "SELECT count(cvid)"
                    + "FROM Cv where state != ? and bagid = ? ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(2, bagid_in);
            stmt.setInt(1, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                count = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;

    }

    public long getBagId(long cvid_in) {
        long bagid = -1;
        try {
            String sql = "Select bagid from cv"
                    + " where cvid = ? and state != ? and ctime = (Select max(ctime) from cv where  cvid = ? and state != ? ) ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cvid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setLong(3, cvid_in);
            stmt.setInt(4, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                bagid = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CVDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bagid;
    }
}
