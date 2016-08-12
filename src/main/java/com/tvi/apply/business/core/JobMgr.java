package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EField;
import com.tvi.apply.data.entity.EJob;
import com.tvi.apply.data.entity.EJobSetting;
import com.tvi.apply.util.database.IDatabase;

import java.util.List;

public class JobMgr implements IJobMgt
{
	private IDatabase db;

	public JobMgr(IDatabase db)
	{
		this.db = db;
	}


	@Override
	public List<EJob> listByOrg(long org, boolean archived, String keyword, int n, int p)
	{
		return null;
	}

	@Override
	public long countByOrg(long org, boolean archived, String keyword)
	{
		return 0;
	}

	@Override
	public List<EJob> listJob(boolean archived, String keyword, int n, int p)
	{
		return null;
	}

	@Override
	public long countJob(boolean archived, String keyword)
	{
		return 0;
	}

	@Override
	public long createJob(EJob entity, EJobSetting ejs)
	{
		return 0;
	}

	@Override
	public EJob readJob(long id)
	{
		return null;
	}

	@Override
	public void deleteJob(long id)
	{

	}

	@Override
	public void updateJob(EJob entity)
	{

	}

	@Override
	public List<EField> listFieldByJob(long jobid)
	{
		return null;
	}

	@Override
	public void updateJobField(long jobid, long[] fields)
	{

	}

	@Override
	public void setJobSetting(long jobid, EJobSetting jobSetting)
	{

	}

	@Override
	public EJobSetting getJobSetting(long jobid)
	{
		return null;
	}
}
