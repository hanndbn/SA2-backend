/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tvi.common.manager;

import com.tvi.common.IDatabase;
import com.tvi.common.IEmailFormMgt;
import com.tvi.common.entity.EmailFormEntity;
import com.tvi.data.daos.EmailFormDAO;
import java.util.List;

/**
 *
 * @author Manh
 */
public class EmailFormMgt implements IEmailFormMgt {
    
    EmailFormDAO emailformdao = new EmailFormDAO();

    public EmailFormMgt(IDatabase database) {
        emailformdao.setDatabase(database);
    }
    
    
    @Override
    public long createEmailForm(int unit, EmailFormEntity form) {
        return emailformdao.add(unit, form);
    }

    @Override
    public void deleteEmailForm(long formid) {
       emailformdao.delete(formid);
    
    }

    @Override
    public List<EmailFormEntity> listEmailForm(int unit) {
        return emailformdao.getAll(unit);
    }

    @Override
    public void editEmailForm(long formid, EmailFormEntity form) {
         emailformdao.update(formid, form);
    }


    @Override
    public EmailFormEntity getForm(long formid) {
       return emailformdao.getEmailFormEntity(formid);
    }

    @Override
    public EmailFormEntity getForm(int unitid, int type) {
        return emailformdao.getEmailFormEntity(unitid, type);
    }

    @Override
    public String[] getFieldMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEmailFormsUnit(long emailformid) {
        return emailformdao.getUnit(emailformid);
    }
    
}
