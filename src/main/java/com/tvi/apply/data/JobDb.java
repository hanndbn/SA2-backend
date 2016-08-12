//dinhnv
package com.tvi.apply.data;

import com.tvi.apply.business.core.IJobMgt;
import com.tvi.apply.data.entity.EField;
import com.tvi.apply.data.entity.EJob;
import com.tvi.apply.data.entity.EJobSetting;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobDb implements IJobMgt
{
	private IDatabase database;
	private JobSettingDb jobsettingdb;

	public JobDb(IDatabase database)
	{
		this.database = database;
		this.jobsettingdb = new JobSettingDb(database);
	}

	@Override
	public List<EJob> listByOrg(long org, boolean archived, String keyword, int n, int p)
	{
		List<EJob> lJob = new ArrayList<EJob>();
		try
		{
			String key ;
			key = "";
			String sql = "select id,tag,picture,cvform,ctime,state,description,attachment,star,jobstatus," + "opentime,orgid,closedtime,nview,lmtime,salary,interest,contact,category,creator," + "quantity,endtime,title,color from apply2.job where (orgid=?" + key + ") and (tag like ? or" + " picture like ? or cvform like ? or description like ? or attachment " + "like ? or salary like ? or interest like ? or contact like ? or title" + " like ?) order by ctime desc limit ?,?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(org).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EJob job = new EJob();
				job.id = rh.l();
				job.tag = rh.s();
				job.picture = rh.s();
				job.cvform = rh.s();
				job.ctime = rh.t();
				job.state = rh.i();
				job.description = rh.s();
				job.attachment = rh.s();
				job.star = rh.b();
				job.jobstatus = rh.i();
				job.opentime = rh.t();
				job.orgid = rh.l();
				job.closedtime = rh.t();
				job.nview = rh.i();
				job.lmtime = rh.t();
				job.salary = rh.s();
				job.interest = rh.s();
				job.contact = rh.s();
				job.category = rh.i();
				job.creator = rh.l();
				job.quantity = rh.i();
				job.endtime = rh.t();
				job.title = rh.s();
				job.color = rh.i();
				job.orgid = org;

				lJob.add(job);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return lJob;
	}

	@Override
	public List<EJob> listJob(boolean archived, String keyword, int n, int p)
	{
		List<EJob> lJob = new ArrayList<EJob>();
		try
		{
			String key = "";
			if (archived == true)
			{
				key = "";
			} else
			{
				key = "(state <> -1) and";
			}
			String sql = "select id,tag,picture,cvform,ctime,state,description,attachment" + ",star,jobstatus,opentime,orgid,closedtime,nview,lmtime,salary,interest," + "contact,category,creator,quantity,endtime,title,color from apply2.job " + "where " + key + "(tag like ? or picture like ? or cvform like ? or description " + "like ? or attachment like ? or salary like ? or interest like ? " + "or contact like ? or title like ?) order by star desc, ctime desc limit ?,?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EJob job = new EJob();
				job.id = rh.l();
				job.tag = rh.s();
				job.picture = rh.s();
				job.cvform = rh.s();
				job.ctime = rh.t();
				job.state = rh.i();
				job.description = rh.s();
				job.attachment = rh.s();
				job.star = rh.b();
				job.jobstatus = rh.i();
				job.opentime = rh.t();
				job.orgid = rh.l();
				job.closedtime = rh.t();
				job.nview = rh.i();
				job.lmtime = rh.t();
				job.salary = rh.s();
				job.interest = rh.s();
				job.contact = rh.s();
				job.category = rh.i();
				job.creator = rh.l();
				job.quantity = rh.i();
				job.endtime = rh.t();
				job.title = rh.s();
				job.color = rh.i();
				lJob.add(job);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return lJob;
	}

	@Override
	public long countJob(boolean archived, String keyword)
	{
		long count = 0;
		try
		{
			String key ;
			if (archived)
			{
				key = "";
			} else
			{
				key = "(state <> -1) and";
			}
			String sql = "select count(id) from apply2.job where " + key + "(tag like ? or " + "picture like ? or cvform like ? or description like ? or attachment " + "like ? or salary like ? or interest like ? or contact like ? or " + "title like ?) order by ctime desc";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getLong(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count;
	}

	@Override
	public long createJob(EJob entity, EJobSetting ejs)
	{
		long sucess = -1;
		try
		{
			Calendar cal = Calendar.getInstance();
			String sql = "insert into job(tag,picture,cvform,ctime,state,description,attachment,star,jobstatus,opentime,orgid,closedtime,nview,lmtime,salary,interest,contact,category,creator,quantity,endtime,title,color) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ParamHelper.create(stmt).set(entity.tag).set(entity.picture).set(entity.cvform).set(cal).set(entity.state).set(entity.description).set(entity.attachment).set(entity.star).set(entity.jobstatus).set(entity.opentime).set(entity.orgid).set(entity.closedtime).set(entity.nview).set(entity.lmtime).set(entity.salary).set(entity.interest).set(entity.contact).set(entity.category).set(entity.creator).set(entity.quantity).set(entity.endtime).set(entity.title).set(entity.color);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
			{
				sucess = rs.getLong(1);

//				EJobSetting ejs = new EJobSetting();
//				ejs.sendrejectmail = false;
//				ejs.lmtime = Calendar.getInstance();
//				ejs.neng = 30;
//				ejs.niq = 30;
//				ejs.piq = 30;
//				ejs.peng = 30;
//				ejs.rcvmail = "";
//				ejs.rejectmail = "";
//				ejs.sendresmail = false;
//				ejs.resmail = "";
//				ejs.testmail = "";
				this.setJobSetting(sucess, ejs);
//				jobsettingdb.setSetting(sucess, ejs);
			}

			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	@Override
	public EJob readJob(long id)
	{
		EJob job = new EJob();

		try
		{
			String sql = "select tag,picture,cvform,ctime,state,description,attachment,star," + "jobstatus,opentime,orgid,closedtime,nview,lmtime,salary,interest,contact," + "category,creator,quantity,endtime,title,color from apply2.job where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				job.id = id;
				job.tag = rh.s();
				job.picture = rh.s();
				job.cvform = rh.s();
				job.ctime = rh.t();
				job.state = rh.i();
				job.description = rh.s();
				job.attachment = rh.s();
				job.star = rh.b();
				job.jobstatus = rh.i();
				job.opentime = rh.t();
				job.orgid = rh.l();
				job.closedtime = rh.t();
				job.nview = rh.i();
				job.lmtime = rh.t();
				job.salary = rh.s();
				job.interest = rh.s();
				job.contact = rh.s();
				job.category = rh.i();
				job.creator = rh.l();
				job.quantity = rh.i();
				job.endtime = rh.t();
				job.title = rh.s();
				job.color = rh.i();
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return job;

	}

	@Override
	public void deleteJob(long id)
	{

	}

	@Override
	public void updateJob(EJob entity)
	{
		try
		{
			String sql = "update  job set tag=?,picture=?,cvform=?,ctime=?,state=?,description=?,attachment=?,star=?,jobstatus=?,opentime=?,orgid=?,closedtime=?,nview=?,lmtime=?,salary=?,interest=?,contact=?,category=?,creator=?,quantity=?,endtime=?,title=?,color=? where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(entity.tag).set(entity.picture).set(entity.cvform).set(entity.ctime).set(entity.state).set(entity.description).set(entity.attachment).set(entity.star).set(entity.jobstatus).set(entity.opentime).set(entity.orgid).set(entity.closedtime).set(entity.nview).set(entity.lmtime).set(entity.salary).set(entity.interest).set(entity.contact).set(entity.category).set(entity.creator).set(entity.quantity).set(entity.endtime).set(entity.title).set(entity.color).set(entity.id);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<EField> listFieldByJob(long jobid)
	{
		List<EField> lField = new ArrayList<EField>();
		try
		{
			String sql = "select id,name,ctime,lmtime,state,creator from apply2.field, apply2.use where (field.id=use.fid) and (use.jid=?)";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(jobid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EField field = new EField();
				field.id = rh.l();
				field.name = rh.s();
				field.ctime = rh.t();
				field.lmtime = rh.t();
				field.state = rh.i();
				field.creator = rh.l();
				lField.add(field);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return lField;
	}

	@Override
	public void updateJobField(long jobid, long[] fields)
	{

		List<EField> lField = new ArrayList<EField>();
		try
		{
			//remove list
			String sqldelete = "delete from `use` where jid = ?";
			PreparedStatement stmt = database.prepareStatement(sqldelete);
			ParamHelper.create(stmt).set(jobid);
			stmt.executeUpdate();

			//insert list

			if (fields.length != 0)
			{
				String values = ",";
				for (long f : fields)
				{
					values += ",(?,?)";
				}
				values = values.substring(1, values.length());
				String sqlinsert = "insert into `use`(jid, fid) values " + values.substring(1);
				stmt = database.prepareStatement(sqlinsert);
				ParamHelper ph = ParamHelper.create(stmt);
				for (long f : fields)
				{
					ph.set(jobid).set(f);
				}
				stmt.executeUpdate();
			}

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void setJobSetting(long jobid, EJobSetting jobSetting)
	{
		jobsettingdb.setSetting(jobid, jobSetting);
	}

	@Override
	public EJobSetting getJobSetting(long jobid)
	{
		return jobsettingdb.getSetting(jobid);
	}

	@Override
	public long countByOrg(long org, boolean archived, String keyword)
	{
		long count = 0;
		try
		{
			String key = "";
			if (archived)
			{
				key = "";
			} else
			{
				key = "state <> -1 and";
			}
			String sql = "select count(id) from apply2.job where (" + key + "orgid=?)" + " and (tag like ? or picture like ? or cvform like ? or " + "description like ? or attachment like ? or salary like ?" + " or interest like ? or contact like ? or title like ?) order by ctime desc";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(org).set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getLong(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count;
	}
}
