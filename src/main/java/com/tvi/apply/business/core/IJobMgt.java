package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EField;
import com.tvi.apply.data.entity.EJob;
import com.tvi.apply.data.entity.EJobSetting;

import java.util.List;

public interface IJobMgt
{
	List<EJob> listByOrg(long org, boolean archived, String keyword, int n, int p);

	long countByOrg(long org, boolean archived, String keyword);

	List<EJob> listJob(boolean archived, String keyword, int n, int p);

	long countJob(boolean archived, String keyword);

	long createJob(EJob entity, EJobSetting ejs);

	EJob readJob(long id);

	void deleteJob(long id);

	void updateJob(EJob entity);

	List<EField> listFieldByJob(long jobid);

	void updateJobField(long jobid, long[] fields);

	void setJobSetting(long jobid, EJobSetting jobSetting);

	EJobSetting getJobSetting(long jobid);

}
