/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tvi.common.manager;

import com.tvi.common.IDatabase;
import com.tvi.common.IRequiredFieldMgt;
import com.tvi.common.entity.RequiredField;
import com.tvi.data.daos.RequiredFieldDAOV2;
import java.util.List;

/**
 *
 * @author Manh
 */
public class RequiredFieldMgt implements IRequiredFieldMgt {

    /**
     * Phai hoi xem state the nao
     */
    RequiredFieldDAOV2 requiredFieldDAO = new RequiredFieldDAOV2();

    public RequiredFieldMgt(IDatabase database) {
        requiredFieldDAO.setDatabase(database);
    }
    
    
    
    @Override
    public void editField(long fieldid, RequiredField field) {
        requiredFieldDAO.update(fieldid, field);
    }
    
    @Override
    public void deleteField(long id) {
        requiredFieldDAO.delete(id);
    }

    @Override
    public List<RequiredField> listField(int unit) {
        return requiredFieldDAO.listField(unit);
    }

    @Override
    public long createField(int unit, RequiredField field) {
        return requiredFieldDAO.add(unit, field);
    }

    @Override
    public RequiredField getField(long fid) {
      return requiredFieldDAO.getRequiredField(fid);
    }

    @Override
    public int getRequiredFieldsUnit(long fieldid) {
        return requiredFieldDAO.getUnit(fieldid);
    }
    
}
