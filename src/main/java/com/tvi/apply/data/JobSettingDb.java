//thanhpk
package com.tvi.apply.data;

import com.tvi.apply.data.entity.EJobSetting;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class JobSettingDb
{
	private IDatabase database;

	public JobSettingDb(IDatabase database)
	{
		this.database = database;
	}

	public void setSetting(long jobid, EJobSetting js)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			if (getSetting(jobid) == null)
			{
				String sql = "insert into jobsetting(jid,rcvmail,testmail, resmail,rejectmail,sendresmail,sendrejectmail,piq,niq,lmtime,neng,peng) " +
						"values                      (  ? ,?      ,?       ,?       ,?         ,?          ,?             ,?  ,?  ,?     ,?   ,?)";
				PreparedStatement stmt = database.prepareStatement(sql);
				ParamHelper.create(stmt).set(jobid).set(js.rcvmail).set(js.testmail).set(js.resmail).set(js.rejectmail).set(js.sendresmail).set(js.sendrejectmail).set(js.piq).set(js.niq).set(js.lmtime).set(js.neng).set(js.peng);
				stmt.executeUpdate();
				stmt.close();
			} else
			{
				String sql = "update jobsetting set rcvmail= ?, testmail = ?, resmail = ?, rejectmail = ?, sendresmail = ?, sendrejectmail = ?, piq = ?, niq = ?, lmtime = ?, neng = ?,peng = ? where jid = ?";
				PreparedStatement stmt = database.prepareStatement(sql);
				ParamHelper.create(stmt).set(js.rcvmail).set(js.testmail).set(js.resmail).set(js.rejectmail).set(js.sendresmail).set(js.sendrejectmail).set(js.piq).set(js.niq).set(js.lmtime).set(js.neng).set(js.peng).set(jobid);
				stmt.executeUpdate();
				stmt.close();
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public EJobSetting getSetting(long jobid)
	{
		try
		{
			EJobSetting jobSetting;

			String sql = "select rcvmail,testmail, resmail,rejectmail,sendresmail, sendrejectmail, piq,niq,lmtime,neng,peng from jobsetting where jid = ?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(jobid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				ResultHelper r = new ResultHelper(rs);
				jobSetting = new EJobSetting();

				jobSetting.rcvmail = r.s();
				jobSetting.testmail = r.s();
				jobSetting.resmail = r.s();
				jobSetting.rejectmail = r.s();
				jobSetting.sendresmail = r.b();
				jobSetting.sendrejectmail = r.b();
				jobSetting.piq = r.i();
				jobSetting.niq = r.i();
				jobSetting.lmtime = r.t();
				jobSetting.neng = r.i();
				jobSetting.peng = r.i();
			} else
			{
				jobSetting = null;
			}
			rs.close();
			stmt.close();
			return jobSetting;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}