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
public class ConfigDAOV2 {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean add(int unit_in, String ref_in, String val_in) {
        boolean sucess = false;
        String val = null;
        val = getConfig(unit_in, ref_in);
        if (val != null) {
            try {
                String sql = "UPDATE Config SET  val=? WHERE ref=? and unit=? ;";
                PreparedStatement stmt = database.prepareStatement(sql);
                stmt.setString(1, val_in);
                stmt.setString(2, ref_in);
                stmt.setInt(3, unit_in);
                stmt.execute();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigDAOV2.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            String sql = "INSERT INTO Config(ref,val,unit) VALUES(?,?,?);";
            try {
                PreparedStatement stmt = database.prepareStatement(sql);
                stmt.setString(1, ref_in);
                stmt.setString(2, val_in);
                stmt.setInt(3, unit_in);
                stmt.execute();
                sucess = true;
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigDAOV2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return sucess;
    }

    public String getConfig(int unit_in, String ref_in) {
        String sucess = null;
        try {
            String sql = "SELECT val FROM Config where unit =? and ref = ?;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, unit_in);
            stmt.setString(2, ref_in);
            ResultSet mRs = stmt.executeQuery();
            if (mRs.next()) {
                sucess = mRs.getString(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

}
