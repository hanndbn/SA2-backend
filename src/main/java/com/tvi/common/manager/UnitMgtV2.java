
package com.tvi.common.manager;

import com.tvi.common.IDatabase;
import com.tvi.common.IUnitMgtV2;
import com.tvi.common.entity.RoleEntity;
import com.tvi.common.entity.UnitEntity;
import com.tvi.common.entity.UserEntityV2;
import com.tvi.common.type.AccessDeny;
import com.tvi.common.util.StringUtil;
import com.tvi.data.daos.ConfigDAOV2;
import com.tvi.data.daos.RoleDAOV2;
import com.tvi.data.daos.UnitDAO2;
import com.tvi.data.daos.UpDAO2;
import com.tvi.data.daos.UserDAO2;
import com.tvi.data.entity.UpEntity2;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class UnitMgtV2 implements IUnitMgtV2{
    
    UserDAO2 userdao = new UserDAO2();
    UpDAO2 updao = new UpDAO2();
    RoleDAOV2 roledao = new RoleDAOV2();
    UnitDAO2 unitdao = new UnitDAO2();
    ConfigDAOV2 configdao = new ConfigDAOV2();
    
    public UnitMgtV2(IDatabase database) {

        updao.setDatabase(database);
        userdao.setDatabase(database);
        roledao.setDatabase(database);
        unitdao.setDatabase(database);
        configdao.setDatabase(database);
    }
    
    

    @Override
    public long createUP(String username, String password, UserEntityV2 user) {
        long id = -1;
        long id2 = -1;
        try {
            id2 = updao.checkUp(username);
            if (id2 != -1) return -1;
            id = userdao.add(user);
            updao.add(new UpEntity2(id, username, StringUtil.createPassword(password)));
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UnitMgtV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UnitMgtV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public void editUP(long uid, String username, String password) {
        try {
            updao.update(new UpEntity2(uid, username, StringUtil.createPassword(password)));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UnitMgtV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UnitMgtV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public long matchUP(String username, String password) {
       return updao.checkUp(username, password);
    }

    @Override
    public UserEntityV2 getUser(long userid) {
        return userdao.getUser(userid);
    }

    @Override
    public void editUser(long uid, UserEntityV2 user) {
        userdao.update(user);
    }

    @Override
    public String resetPassword(long userid) {
        return updao.resertPassword(userid);
    }

    @Override
    public List<RoleEntity> listRolebyUser(long uid) {
        return roledao.getAllRoleOfUser(uid);
    }

    @Override
    public List<UnitEntity> listUnitbyUser(long uid) {
        return unitdao.getUnitByUserID(uid);
    }

    @Override
    public void access(long uid, String action) throws AccessDeny, RuntimeException {
        if (!roledao.checkAccess(uid, action)) throw new AccessDeny(action);        
    }

    @Override
    public String getConfig(int unitid, String ref) {
        return configdao.getConfig(unitid, ref);
    }

    @Override
    public void setConfig(int unitid, String ref, String val) {
        configdao.add(unitid, ref, val);
    }

    @Override
    public void getInUnit(long userid, int unit) {
        userdao.moveUserToUnit(userid, unit);
    }

    @Override
    public void getOutUnit(long userid, int unit) {
        userdao.moveUserOutUnit(userid, unit);
    }

    @Override
    public String getUsername(long userid) {
        return  updao.getUserName(userid);
    }

    @Override
    public UnitEntity getUnit(int id) {
         return unitdao.getUnit(id);    }

}

