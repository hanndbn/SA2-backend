package com.tvi.common;

import com.tvi.common.entity.RequiredField;
import java.util.List;

public interface IRequiredFieldMgt
{

	List<RequiredField> listField(int unit);

	long createField(int unit, RequiredField field);

	void editField(long fieldid, RequiredField field);

	RequiredField getField(long fid);

	void deleteField(long fieldid);

	int getRequiredFieldsUnit(long fieldid);

}
