//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.OrgDb;
import com.tvi.apply.data.entity.EOrg;
import com.tvi.apply.util.database.IDatabase;

import java.util.List;

public class OrgMgr implements IOrgMgt
{
	public OrgDb orgdb;

	public OrgMgr(IDatabase database)
	{
		orgdb = new OrgDb(database);
	}

	@Override
	public long createOrg(EOrg entity)
	{
		return orgdb.createOrg(entity);
	}

	@Override
	public EOrg readOrg(long id)
	{
		return orgdb.readOrg(id);
	}

	@Override
	public void deleteOrg(long id)
	{
		orgdb.deleteOrg(id);
	}

	@Override
	public void updateOrg(EOrg entity)
	{
		orgdb.updateOrg(entity);
	}

	@Override
	public List<EOrg> listOrg(boolean archived, String keyword, int n, int p)
	{
		return orgdb.listOrg(archived, keyword, n, p);
	}

	@Override
	public long countOrg(boolean archived, String keyword)
	{
		return orgdb.countOrg(archived, keyword);
	}
}
