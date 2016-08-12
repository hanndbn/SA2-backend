package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EField;

import java.util.List;

public interface IFieldMgt
{
	long createField(EField entity);

	EField readField(long id);

	void deleteField(long id);

	void updateField(EField entity);

	List<EField> listField(boolean archived, String keyword, int n, int p);

	long countField(boolean archived, String keyword);
}
