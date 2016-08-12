
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.entity.UnitEntity;
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
public class UnitDAO2 {

    private IDatabase database;
    private int id;
    private String name;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public List<UnitEntity> getUnitByUserID(long userId_in) {
        List<UnitEntity> listUnit = new ArrayList<UnitEntity>();
        try {
            String sql = "SELECT  unit.uid, unit.name From unit,"
                    + database.getDatabaseName() + ".in WHERE unit.uid = "
                    + database.getDatabaseName() + ".in.unitid and "
                    + database.getDatabaseName() + ".in.userid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, userId_in);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getInt(1);
                name = mRs.getString(2);
                listUnit.add(new UnitEntity(id, name));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UnitDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listUnit;
    }

    public UnitEntity getUnit(int id) {
        try{
            String sql = "SELECT  unit.uid, unit.name From unit WHERE uid = ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getInt(1);
                name = mRs.getString(2);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UnitDAO2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new UnitEntity(id, name);
    }

}
