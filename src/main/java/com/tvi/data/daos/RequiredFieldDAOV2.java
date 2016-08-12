/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.RequiredField;
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
public class RequiredFieldDAOV2 {

    private IDatabase database;
    private long id;
    private String title;
    private int state;
    private Date ctime;
    private long creator;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    /**
     *
     * @param unit_in
     * @param p_in trang
     * @param ps_in phan trang
     * @param s_in Chuoi de sap xep. Neu s_in = null khong sap sep
     * @return
     *
     * finished 4.1
     */
    public List<RequiredField> listField(int unit_in, int p_in, int ps_in, String[] s_in) {

        List<RequiredField> listRequiredFields = new ArrayList<RequiredField>();
        try {
            String sql;
            PreparedStatement stmt = null;
            if (s_in == null||s_in.length < 1) {
                sql = "SELECT fieldid,title,state,ctime,creator FROM Requiredfield where unit = ? and state != ? ;";
                sql = sql + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setInt(1, unit_in);
                stmt.setInt(2, ConfigCount.DELETED);
                stmt.setInt(3, p_in * ps_in);
                stmt.setInt(4, ps_in);

            } else {
                sql = "SELECT fieldid,title,state,ctime,creator FROM Requiredfield where unit = ? and state != ? order by ";
                sql = sql + " order by ";
                for (int i = 0; i < s_in.length - 1; i++) {
						    sql =sql + StringEscapeUtils.escapeSql(s_in[i].replaceAll("[^\\w]", "")) + " desc, ";
                }
                sql = sql +  StringEscapeUtils.escapeSql(s_in[s_in.length -1].replaceAll("[^\\w]", "")) + " desc " + " limit ?,? ; ";
                stmt = database.prepareStatement(sql);
                stmt.setLong(1, unit_in);
                stmt.setInt(2, ConfigCount.DELETED);
                stmt.setInt(3, p_in * ps_in);
                stmt.setInt(4, ps_in);
            }

            if (stmt != null) {
                ResultSet mRs = stmt.executeQuery();
                while (mRs.next()) {
                    id = mRs.getLong(1);
                    title = mRs.getString(2);
                    state = mRs.getInt(3);
                    ctime = new java.util.Date(mRs.getLong(4));
                    creator = mRs.getLong(5);
                    listRequiredFields.add(new RequiredField(id, title, state, ctime, creator));
                }
                mRs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listRequiredFields;
    }

    public List<RequiredField> listField(int unit_in) {
        List<RequiredField> listRequiredFields = new ArrayList<RequiredField>();
        try {
            String sql = "SELECT fieldid,title,state,ctime,creator FROM Requiredfield where unit = ? and state != ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(1, unit_in);
            stmt.setInt(2, ConfigCount.DELETED);

            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getLong(1);
                title = mRs.getString(2);
                state = mRs.getInt(3);
                ctime = new java.util.Date(mRs.getLong(4));
                creator = mRs.getLong(5);
                listRequiredFields.add(new RequiredField(id, title, state, ctime, creator));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listRequiredFields;
    }

    public long add(int unit, RequiredField requiredfield_in) {
        long sucess = -1;
        try {
            String strSQL = "INSERT INTO Requiredfield(title,state,ctime,creator,unit) VALUES(?,?,?,?,?);";
            PreparedStatement stmt = database.preparedStatement(strSQL,Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, requiredfield_in.title);
            stmt.setInt(2, requiredfield_in.state);
            stmt.setLong(3, (new java.util.Date().getTime()));
            stmt.setLong(4, requiredfield_in.creator);
            stmt.setInt(5, unit);
            stmt.execute();
            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()) {
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public long countRequiredField(long unit_in) {
        long count = -1;
        try {
            String sql = "Select count(fieldid) Form question where unit = ? and state != ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, unit_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                count = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    /**
     *
     * @param fieldid_in long
     * @param requiredField_in RequiredField Finished 4.1
     */
    public void update(long fieldid_in, RequiredField requiredField_in) {
        int unit = -1;
        try {
            String sql = "UPDATE Requiredfield SET  Requiredfield.state=?  WHERE Requiredfield.fieldid= ? and Requiredfield.state != ? and Requiredfield.ctime= (Select max(lon.ctime) from (Select * from Requiredfield) as lon where lon.state != ? and lon.fieldid=? )  ";
            String sql2 = "INSERT INTO Requiredfield (fieldid,title,state,ctime,creator,unit) VALUES(?,?,?,?,?,?);";
            String sql3 = "Select unit FROM Requiredfield where fieldid = ? and state != ? and ctime = (Select max(ctime) from Requiredfield where state != ? and  fieldid = ? );";
           
            PreparedStatement stmt3 = database.prepareStatement(sql3);
            stmt3.setInt(2, ConfigCount.DELETED);
            stmt3.setLong(1, fieldid_in);
            stmt3.setLong(4, fieldid_in);
            stmt3.setInt(3, ConfigCount.DELETED);
            ResultSet mRs3 = stmt3.executeQuery();
            if (mRs3.next()) {
                unit = mRs3.getInt(1);
            }
            mRs3.close();
            stmt3.close();
            //
             //
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(1, ConfigCount.DELETED);
            stmt.setLong(2, fieldid_in);
            stmt.setInt(3, ConfigCount.DELETED);
            stmt.setLong(5, fieldid_in);
            stmt.setInt(4, ConfigCount.DELETED);
            stmt.execute();
            stmt.close();
            //
            
            //
            PreparedStatement stmt2 = database.prepareStatement(sql2);
            stmt2.setLong(1, fieldid_in);

            stmt2.setInt(3, requiredField_in.state);
            stmt2.setString(2, requiredField_in.title);
            stmt2.setLong(4, (new java.util.Date().getTime()));
            stmt2.setLong(5, requiredField_in.creator);
            stmt2.setInt(6, unit);
            stmt2.execute();

            stmt2.close();

        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param fieldid_in Finished 4.1
     */
    public void delete(long fieldid_in) {
        try {
            String sql =   "UPDATE Requiredfield SET  Requiredfield.state=?  WHERE Requiredfield.fieldid= ? and Requiredfield.state != ? and Requiredfield.ctime= (Select max(lon.ctime) from (Select * from Requiredfield) as lon where lon.state != ? and lon.fieldid=? )  ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(1, ConfigCount.DELETED);
            stmt.setInt(3, ConfigCount.DELETED);
            stmt.setLong(2, fieldid_in);
            stmt.setLong(5, fieldid_in);
            stmt.setInt(4, ConfigCount.DELETED);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public RequiredField getRequiredField(long fieldid_in) {
        RequiredField requiredField = null;

        try {
            String sql = "SELECT title,state,ctime,creator FROM Requiredfield where"
                    + " fieldid = ? and state != ? and ctime = (Select max(ctime) from Requiredfield where state != ? and  fieldid = ? )";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, fieldid_in);
            stmt.setInt(2, ConfigCount.DELETED);
            stmt.setLong(4, fieldid_in);
            stmt.setInt(3, ConfigCount.DELETED);

            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                id = fieldid_in;
                title = mRs.getString(1);
                state = mRs.getInt(2);
                ctime = new java.util.Date(mRs.getLong(3));
                creator = mRs.getLong(4);
                requiredField = new RequiredField(id, title, state, ctime, creator);
            }
            mRs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return requiredField;
    }

    public int getUnit(long fieldid_in) {
        int unit = -1;
        try {
            String sql3 = "SELECT unit FROM Requiredfield where fieldid = ? and state != ? and ctime = (SElect max(ctime) FROM Requiredfield where fieldid = ? and state != ?);";
            PreparedStatement stmt3 = database.prepareStatement(sql3);
            stmt3.setInt(2, ConfigCount.DELETED);
            stmt3.setLong(1, fieldid_in);
            stmt3.setLong(3, fieldid_in);
            stmt3.setInt(4, ConfigCount.DELETED);
            ResultSet mRs3 = stmt3.executeQuery();
            if (mRs3.next()) {
                unit = mRs3.getInt(1);
            }
            mRs3.close();
            stmt3.close();
        } catch (SQLException ex) {
            Logger.getLogger(RequiredFieldDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return unit;
    }

}
