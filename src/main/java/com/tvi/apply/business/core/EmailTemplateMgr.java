//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.EmailTemplateDb;
import com.tvi.apply.data.entity.EEmailTemplate;
import com.tvi.apply.util.database.IDatabase;

import java.util.List;

public class EmailTemplateMgr implements IEmailTemplateMgt
{
	//public IDatabase db;
	private EmailTemplateDb temdb;

	public EmailTemplateMgr(IDatabase db)
	{
		//this.db = db;
		temdb = new EmailTemplateDb(db);
	}

	@Override
	public long createTemplate(EEmailTemplate entity)
	{
		return temdb.createTemplate(entity);
	}

	@Override
	public EEmailTemplate readTemplate(long id)
	{
		return  temdb.readTemplate(id) ;
	}

	@Override
	public void deleteTemplate(long id)
	{
		temdb.deleteTemplate(id);
	}

	@Override
	public void updateTemplate(EEmailTemplate entity)
	{
		temdb.updateTemplate(entity);
	}

	@Override
	public List<EEmailTemplate> listTemplate(String keyword, int n, int p)
	{
		return temdb.listTemplate(keyword, n, p);
	}

	@Override
	public long countTemplate(String keyword)
	{
		return temdb.countTemplate(keyword);
	}
}
