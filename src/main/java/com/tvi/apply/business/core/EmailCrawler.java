//dinhnv, modified by thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EEmail;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.mail.Email;
import com.tvi.apply.util.mail.IEmailHelper;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmailCrawler extends Thread implements IEmailCrawler
{
	private IDatabase database;
	private Calendar cal = Calendar.getInstance();

	public EmailCrawler(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public void run(final IEmailHelper helper, final IEmailMgt mgr, final long t)
	{
		Thread th1 = new Thread()
		{
			@Override
			public void run()
			{
				if (new File("D:\\dontrun.txt").exists() || new File("/home/tvi/dontrun.txt").exists())
					return; //just run in server
				cal = gettime();
				while (true)
				{
					try
					{

						List<String> listMail;
						long count = helper.count(cal);
						for (int k = 0; k < (count / 50) + 1; k++)
						{
							listMail = helper.scan(cal, 50, k); // mai xem lai cho nay, phan trang
							for (String aListMail : listMail)
							{
								String sql = "select id from email where messageid = ?";
								PreparedStatement stmt1 = database.prepareStatement(sql);
								ParamHelper.create(stmt1).set(aListMail);
								ResultSet rs1 = stmt1.executeQuery();
								//nếu chưa có mail
								if (!rs1.next()){
									System.out.println("? " + aListMail + "||" + count);
									Email e;
									EEmail em;
									e = helper.download(aListMail);
									em = convert(e);
									em.junk = false;
									mgr.createEmail(em,0);
									cal = em.sendtime;
								}
							}
						}
						helper.closeSession();
						Thread.sleep(t * 1000);

					} catch (Exception e1)
					{
						e1.printStackTrace();
						System.out.println("cal=" + cal.toString());
						//continue looping
					}
				}
			}
		};
		th1.start();
	}

	public static EEmail convert(Email e)
	{
		EEmail em = new EEmail();
		try
		{

			Calendar cal = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			em.to = e.to;
			em.from = e.from;
			if (e.cc != null)
			{
				for (int i = 0; i < e.cc.length; i++)
				{
					em.cc = em.cc + e.cc[i] + ";";
				}
			}

			if (e.bcc != null)
			{
				for (int i = 0; i < e.bcc.length; i++)
				{
					em.bcc = em.bcc + e.bcc[i] + ";";
				}
			}

			if (e.attachment != null)
			{
				em.attachment = new String[e.attachment.length];
				System.arraycopy(e.attachment, 0, em.attachment, 0, e.attachment.length);
			}

			em.messageid = e.messageid;
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
			Date date = sdf.parse(e.sendtime);
			cal2.setTime(date);
			em.sendtime = cal2;
			em.subject = e.subject;
			em.body = e.body;
			em.assignby = 1;
			em.ctime = cal;
			em.junk = true;
			em.quanlifiedby = 1;
			em.quanlifieddate = cal;
			em.state = 0;
			em.status = 0;

		} catch (ParseException ex)
		{
			throw new RuntimeException(ex);
		}
		return em;
	}

	public Calendar gettime()
	{
		Calendar cal = Calendar.getInstance();
		try
		{
			String sql = "select max(sendtime) from email";
			PreparedStatement stmt = database.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				cal.setTimeInMillis(rs.getTimestamp(1).getTime());
			}
		} catch (Exception ex)
		{
			cal.set(2014, Calendar.SEPTEMBER, 30, 0, 0, 0);
		}

		return cal;
	}

}
