package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CAFEITVN.COM
 */

@Deprecated
public class TpDAO {

    private IDatabase database;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean deleteFollowUser(long uid_in) {
        boolean sucess = false;
        try {
            String sql = "DELETE FROM Tp WHERE uid = ? ;";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, uid_in);
            stmt.executeUpdate();
            sucess = true;
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TpDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

}
