/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.entity.RoleEntity;
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
public class RoleDAOV2 {

    private IDatabase database;
    private long id;
    private String name;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public List<RoleEntity> getAllRoleOfUser(long userId_in) {
        List<RoleEntity> listRole = new ArrayList<RoleEntity>();
        try {
            String sql = "SELECT role.rid , role.name From role, membership "
                    + "WHERE role.rid = membership.rid and membership.uid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, userId_in);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getLong(1);
                name = mRs.getString(2);
                listRole.add(new RoleEntity(id, name));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listRole;
    }
    
        public ArrayList<String> actionOfUser(long userId_in) {
        ArrayList<String> listAction = new ArrayList<String>();
        List<RoleEntity> listRole = getAllRoleOfUser(userId_in);
        String sql = "SELECT action.name From action,perm,role"
                + " WHERE role.rid = ? "
                + "and role.rid = perm.rid "
                + "and perm.aid = action.aid "
                + "and action.aid NOT IN (SELECT action.aid FROM negperm,role"
                + " WHERE role.rid = ? "
                + "and role.rid = negperm.rid  "
                + "and action.aid = ne" + ")";

        for (RoleEntity i : listRole) {
            try {
                PreparedStatement stmt = database.prepareStatement(sql);
                stmt.setLong(1, i.rid);
                stmt.setLong(2, i.rid);
                ResultSet mRs = stmt.executeQuery();
                while (mRs.next()) {
                    listAction.add(mRs.getString(1));
                }
                mRs.close();
                stmt.close();
                
            } catch (SQLException ex) {
                Logger.getLogger(RoleDAOV2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listAction;
    }
public boolean checkAccess (long userid__in,String action_in ){
    boolean sucess = false;
    try {
            String sql = "call action (?,?);";
            PreparedStatement stmt = database.prepareCall(sql);
            stmt.setLong(1, userid__in);
            stmt.setString(2, action_in);
            ResultSet mRs = stmt.executeQuery();
            if(mRs.next()){
                mRs.getLong(1);
                sucess = true;
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    return sucess;
}
}
