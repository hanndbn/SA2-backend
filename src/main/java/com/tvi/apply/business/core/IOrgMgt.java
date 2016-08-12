package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EOrg;

import java.util.List;

public interface IOrgMgt
{
	long createOrg(EOrg entity);

	EOrg readOrg(long id);

	void deleteOrg(long id);

	void updateOrg(EOrg entity);

	List<EOrg> listOrg(boolean archived, String keyword, int n, int p);

	long countOrg(boolean archived, String keyword);
}
