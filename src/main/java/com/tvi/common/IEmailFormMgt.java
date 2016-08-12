
package com.tvi.common;

import com.tvi.common.entity.EmailFormEntity;
import java.util.List;

public interface IEmailFormMgt
{
	long createEmailForm(int unit, EmailFormEntity form);
	
	void deleteEmailForm(long formid);
	
	List<EmailFormEntity> listEmailForm(int unit);
	
	void editEmailForm(long formid, EmailFormEntity form);
	
	EmailFormEntity getForm(long formid);
	
	/*trả về form của unit có kiểu là type*/
	EmailFormEntity getForm(int unitid, int type);
	
	/*trả về toàn bộ bảng FieldMap*/
	String[] getFieldMap();
	
	int getEmailFormsUnit(long emailformid);
}
