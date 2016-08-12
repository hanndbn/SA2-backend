//thanhpk
package com.tvi.apply.business.core;


import com.tvi.apply.data.FieldDb;
import com.tvi.apply.data.entity.EField;
import com.tvi.apply.util.database.IDatabase;

import java.util.List;

public class FieldMgr implements IFieldMgt
{
	public IDatabase db;
	private FieldDb fieldDb;

	public FieldMgr(IDatabase db)
	{
		this.db = db;
		fieldDb = new FieldDb(db);
	}

	@Override
	public long createField(EField entity)
	{
		return fieldDb.createField(entity);
	}

	@Override
	public EField readField(long id)
	{
		return fieldDb.readField(id);
	}

	@Override
	public void deleteField(long id)
	{
		fieldDb.deleteField(id);
	}

	@Override
	public void updateField(EField entity)
	{
		fieldDb.updateField(entity);
	}

	@Override
	public List<EField> listField(boolean archived, String keyword, int n, int p)
	{
		return fieldDb.listField(archived, keyword, n, p);
	}

	@Override
	public long countField(boolean archived, String keyword)
	{
		return fieldDb.countField(archived, keyword);
	}
}
