//DINHNV
package com.tvi.apply.data;

import com.tvi.apply.business.core.ICandidateMgt;
import com.tvi.apply.data.entity.ECandidate;
import com.tvi.apply.data.entity.ECandidateLog;
import com.tvi.apply.data.entity.EComment;
import com.tvi.apply.data.entity.EEmail;
import com.tvi.apply.data.entity.EInfo;
import com.tvi.apply.util.database.IDatabase;
import com.tvi.apply.util.database.ParamHelper;
import com.tvi.apply.util.database.ResultHelper;
import com.tvi.apply.util.mail.Email;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.FolderClosedException;
import javax.mail.Header;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.sun.mail.imap.protocol.Item;
import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.RFC822DATA;
import com.sun.mail.imap.protocol.RFC822SIZE;
import com.sun.mail.imap.protocol.UID;


public class CandidateDb implements ICandidateMgt
{

	private IDatabase database;
	private String defaulttext = "chưa nhập";
	public String mailaddress;

	public CandidateDb(IDatabase database, String mailadress)
	{
		this.database = database;
		this.mailaddress = mailadress;
	}

	//liệt kê log, theo thời gian gần nhất trước
	public List<ECandidateLog> listCandidateLog(long cid, int n, int p)
	{
		List<ECandidateLog> listLog = new ArrayList<ECandidateLog>();
		try
		{
			String sql = "select id, `ctime`, `action`, `subject`, `tag` from apply2.candidatelog where cid=? order by ctime desc, lmtime desc limit ?,?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cid).set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				ECandidateLog log = new ECandidateLog();
				log.id = rh.l();
				log.ctime = rh.t();
				log.action = rh.s();
				log.subject = rh.s();
				log.tag = rh.s();
				listLog.add(log);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return listLog;
	}

	@Override
	public long logCandidate(long cid, ECandidateLog log)
	{
		long sucess = -1;
		try
		{
			Calendar cal = Calendar.getInstance();
			String sql = "INSERT INTO `apply2`.`candidatelog` (`cid`, `ctime`, `action`, `subject`, `tag`) VALUES (?,?,?,?,?)";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ParamHelper.create(stmt).set(cid).set(cal).set(log.action).set(log.subject).set(log.tag);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
			{
				sucess = rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public long countCandidateLog(long cid)
	{
		long sucess = -1;
		try
		{
			String sql = "select count(id) from apply2.candidatelog where cid=? order by ctime desc, lmtime desc";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				sucess = rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;

	}

	public List<EInfo> listCandidateInfo(long cid)
	{
		List<EInfo> listInfor = new ArrayList<EInfo>();
		try
		{
			String sql = "select `fid`, `value`, `ctime`, `lmtime`, `state`, `creator` from apply2.info where cid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EInfo e = new EInfo();
				e.fid = rh.i();
				e.id = cid;
				e.value = rh.s();
				e.ctime = rh.t();
				e.lmtime = rh.t();
				e.state = rh.i();
				e.creator = rh.l();
				listInfor.add(e);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return listInfor;

	}

	//cho state=0
	@Override

	public long createCandidateInfo(long cid, long fid, String info, long creator)
	{
		long sucess = -1;
		try
		{
			String sql = "INSERT INTO `apply2`.`info` (`fid`, `cid`, `value`, `ctime`, `lmtime`, `state`, `creator`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			Calendar curtime = Calendar.getInstance();
			ParamHelper.create(stmt).set(fid).set(cid).set(info).set(curtime).set(curtime).set(0).set(creator);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
			{
				rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;

	}

	//state = 0
	@Override
	public void updateCandidateInfo(long fid, long cid, String info)
	{
		try
		{
			String sql = "update `info` set `value`=?, `ctime`=?, `state`=? where `fid`=? and cid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			Calendar curtime = Calendar.getInstance();
			ParamHelper.create(stmt).set(info).set(curtime).set(curtime).set(0).set(fid).set(cid);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	//dxoa 1 ban ghi trong truong infor
	@Override
	public void deleteCandidateInfo(long fid, long cid)
	{
		try
		{
			String sql = "delete from info where fid=? and cid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(fid).set(cid);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

	}

	@Override
	public void sendAttachment(long canid, String path, String email)
	{
		try
		{
			//xác định xem attachment ở email nào
			String sql = "select eid from attachment where path=? and";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(path);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			long emailid = rs.getLong("eid");
			EEmail e = readEmail(emailid);
			e.from = email;
			createEmail(e, 0);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<EComment> listCandidateComment(long cid)
	{
		List<EComment> lCom = new ArrayList<EComment>();
		try
		{
			String sql = "select id, `ctime`, `state`, `lmtime`, `comment`, `creator`, `priority` from apply2.comment where cid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EComment e = new EComment();
				e.id = rh.l();
				e.ctime = rh.t();
				e.state = rh.i();
				e.lmtime = rh.t();
				e.comment = rh.s();
				e.creator = rh.l();
				e.priority = rh.i();
				lCom.add(e);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return lCom;
	}

	@Override
	public long commentCandidate(long cid, EComment comment)
	{
		long sucess = -1;
		try
		{
			Calendar cal = Calendar.getInstance();
			String sql = "INSERT INTO `apply2`.`comment` (`ctime`, `state`, `lmtime`, `comment`, `creator`, `cid`, `priority`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ParamHelper.create(stmt).set(cal).set(comment.state).set(comment.lmtime).set(comment.comment).set(comment.creator).set(cid).set(comment.priority);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
			{
				sucess = rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;

	}

	@Override
	public void updateComment(long cid, EComment comment)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			String sql = "update `comment` set `ctime`=?, `state`=?, `lmtime`=?, `comment`=?, `creator`=?, `cid`=?, `priority`=? where `id`=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cal).set(comment.state).set(comment.lmtime).set(comment.comment).set(comment.creator).set(cid).set(comment.priority).set(comment.id);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void deleteComment(long commentid)
	{
		try
		{
			String sql = "delete from apply2.comment where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(commentid);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/*liệt kê cv trong job, nếu truyền tham số đầu tiên là null thì search all*/
	public long createCandidate(ECandidate entity)
	{
		long sucess = -1;
		try
		{
			String sql = "INSERT INTO `apply2`.`candidate` (`fullname`, `email`, `phone`, `color`, `star`, `jobid`, `engtestate`, `iqteststate`, `eng`, `iq`, `attachment`, `resumestatus`, `state`, `lmtime`, `ctime`, `birth`, `gender`, `junk`,cjt,curemp,salary,twe,spec,level,tech) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ParamHelper.create(stmt).set(entity.fullname).set(entity.email).set(entity.phone).set(entity.color).set(entity.star).set(entity.jobid).set(entity.englishteststate).set(entity.iqteststate).set(entity.eng).set(entity.iq).set(entity.attachment).set(entity.resumestatus).set(entity.state).set(entity.lmtime).set(entity.ctime).set(entity.birth).set(entity.gender).set(entity.junk).set(entity.cjt).set(entity.curemp).set(entity.salary).set(entity.twe).set(entity.spec).set(entity.level).set(entity.tech);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
			{
				sucess = rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return sucess;
	}

	public ECandidate readCandidateByMail(String mail)
	{
		ECandidate cadidate = new ECandidate();
		try
		{
			String sql = "select id, `fullname`, `email`, `phone`, `color`, `star`, `jobid`, `engtestate`, `iqteststate`, `eng`, `iq`, `attachment`," + " `resumestatus`, `state`, `lmtime`, `ctime`, `birth`, `gender`, `junk`,cjt,curemp,salary,twe,spec,level,tech from candidate where email=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(mail);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				cadidate.id = rh.i();
				cadidate.fullname = rh.s();
				cadidate.email = rh.s();
				cadidate.phone = rh.s();
				cadidate.color = rh.i();
				cadidate.star = rh.b();
				cadidate.jobid = rh.l();
				cadidate.englishteststate = rh.i();
				cadidate.iqteststate = rh.i();
				cadidate.eng = rh.i();
				cadidate.iq = rh.i();
				cadidate.attachment = rh.s();
				cadidate.resumestatus = rh.i();
				cadidate.state = rh.i();
				cadidate.lmtime = rh.t();
				cadidate.ctime = rh.t();
				cadidate.birth = rh.t();
				cadidate.junk = rh.b();
				cadidate.cjt = rh.s();
				cadidate.curemp = rh.s();
				cadidate.salary = rh.s();
				cadidate.twe = rh.s();
				cadidate.spec = rh.s();
				cadidate.level = rh.s();
				cadidate.tech = rh.s();
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return cadidate;
	}

	public ECandidate readCandidate(long id)
	{
		ECandidate cadidate = new ECandidate();
		try
		{
			String sql = "select `fullname`, `email`, `phone`, `color`, `star`, `jobid`, `engtestate`, `iqteststate`, `eng`, `iq`, `attachment`," + " `resumestatus`, `state`, `lmtime`, `ctime`, `birth`, `gender`, `junk`,cjt,curemp,salary,twe,spec,level,tech from candidate where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				cadidate.id = id;
				cadidate.fullname = rh.s();
				cadidate.email = rh.s();
				cadidate.phone = rh.s();
				cadidate.color = rh.i();
				cadidate.star = rh.b();
				cadidate.jobid = rh.l();
				cadidate.englishteststate = rh.i();
				cadidate.iqteststate = rh.i();
				cadidate.eng = rh.i();
				cadidate.iq = rh.i();
				cadidate.attachment = rh.s();
				cadidate.resumestatus = rh.i();
				cadidate.state = rh.i();
				cadidate.lmtime = rh.t();
				cadidate.ctime = rh.t();
				cadidate.birth = rh.t();
				cadidate.gender = rh.b();
				cadidate.junk = rh.b();
				cadidate.cjt = rh.s();
				cadidate.curemp = rh.s();
				cadidate.salary = rh.s();
				cadidate.twe = rh.s();
				cadidate.spec = rh.s();
				cadidate.level = rh.s();
				cadidate.tech = rh.s();
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return cadidate;

	}

	public void updateCandidate(ECandidate entity)
	{
		try
		{
			String sql = "update candidate set `fullname`=?, `email`=?, `phone`=?, `color`=?, `star`=?, `jobid`=?, `engtestate`=?, `iqteststate`=?, `eng`=?, `iq`=?, `attachment`=?, `resumestatus`=?, state=?, `lmtime`=?, `ctime`=?, `birth`=?, `gender`=?, `junk`=?,cjt=?,curemp=?,salary=?,twe=?,spec=?,level=?,tech=? where `id`=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(entity.fullname).set(entity.email).set(entity.phone).set(entity.color).set(entity.star).set(entity.jobid).set(entity.englishteststate).set(entity.iqteststate).set(entity.eng).set(entity.iq).set(entity.attachment).set(entity.resumestatus).set(entity.state).set(entity.lmtime).set(entity.ctime).set(entity.birth).set(entity.gender).set(entity.junk).set(entity.cjt).set(entity.curemp).set(entity.salary).set(entity.twe).set(entity.spec).set(entity.level).set(entity.tech).set(entity.id);
			stmt.executeUpdate();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	public void deleteCandidate(long id)
	{
		try {
			String sql = "delete from candidate where `id`=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void matchCVfromTest(long testid)
	{

	}

	@Override
	public boolean haveSent(String email)
	{
		try
		{
			String sql = "select count(id) from email where `from`=? and `to`=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(mailaddress).set(email);
			ResultSet resultSet = stmt.executeQuery();
			resultSet.next();
			return resultSet.getInt(1) > 0;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<EEmail> listCandidateEmail(long cid, int n, int p)
	{
		List<EEmail> email = new ArrayList<EEmail>();
		try
		{
			String canemail = readCandidate(cid).email;
			String sql = "select id,`from`, `to`, `subject`, `sendtime`, `ctime`, `state`, `messageid`, `status`, `assignby`, `qualifiedtime`," + " `qualifiedby`, `junk`, `body` from email where `from`=? or `to`=? order by ctime desc limit ?,? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(canemail).set(canemail).set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				EEmail e = new EEmail();
				ResultHelper rh = new ResultHelper(rs);
				e.id = rh.l();
				e.from = rh.s();
				e.to = rh.s();
				e.subject = rh.s();
				e.sendtime = rh.t();
				e.ctime = rh.t();
				e.state = rh.i();
				e.messageid = rh.s();
				e.status = rh.i();
				e.assignby = rh.l();
				e.quanlifieddate = rh.t();
				e.quanlifiedby = rh.l();
				e.junk = rh.b();
				e.body = rh.s();
				email.add(e);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return email;

	}

	private long countFromEmail(String email)
	{
		long count = -1;
		try
		{
			String sql = "select count(id) from email where `from`=? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(email);
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
	public long countCandidateEmail(long cid)
	{
		long count = -1;
		try
		{
			String canemail = readCandidate(cid).email;
			String sql = "select count(id) from email where `from`=? or `to`=? ";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(canemail).set(canemail);
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
	public List<EEmail> listUnreadEmail(long userid, int n, int p)
	{
		List<EEmail> lEmail = new ArrayList<EEmail>();
		try
		{
			String sql = "select id,`from`, `to`, `subject`, `sendtime`, `ctime`, `state`, `messageid`, `status`,`assignby`, `qualifiedtime`, `qualifiedby`, `junk`, `body` from apply2.email,apply2.UNREAD where (email.id=UNREAD.eid) and (UNREAD.uid=?) limit ?,?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(userid).set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				EEmail e = new EEmail();
				ResultHelper rh = new ResultHelper(rs);
				e.id = rh.l();
				e.from = rh.s();
				e.to = rh.s();
				e.subject = rh.s();
				e.sendtime = rh.t();
				e.ctime = rh.t();
				e.state = rh.i();
				e.messageid = rh.s();
				e.status = rh.i();
				e.assignby = rh.l();
				e.quanlifieddate = rh.t();
				e.quanlifiedby = rh.l();
				e.junk = rh.b();
				e.body = rh.s();
				lEmail.add(e);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return lEmail;
	}

	@Override
	public long countUnreadEmail(long userid, String email)
	{
		long count = -1;
		try
		{
			//long ce = countFromEmail(email);
			String sql = "select count(N.id) from  ( (select * from email where `from`= ? and junk=false) as N) left join  ((select* from unread where uid=?)  as T ) on  (N.id=T.eid  ) where T.uid is null";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(email).set(userid);

			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getLong(1);
			}

			rs.close();
			stmt.close();

			return count;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private void fixEmail(long emailid, String address, long cid) throws SQLException
	{

		//change owner of the mail to cid
		String sql = "update email set `from`=?, `cid` = ?  where id=?";
		PreparedStatement stmt = database.prepareStatement(sql);
		ParamHelper.create(stmt).set(address).set(cid).set(emailid);
		stmt.executeUpdate();

		//mark candidate as junk
		String sql4 = "update candidate set junk = true, email = ? where id = ?";
		PreparedStatement stmt4 = database.prepareStatement(sql4);
		ParamHelper.create(stmt4).set(address).set(cid);
		stmt4.executeUpdate();
	}

	private void fixEmailEx(long emailid, EEmail email)throws SQLException
	{
		String sql2 = "update email set `from` = ?, `to` =?, `subject` = ?, `body` = ? where id=?";
		PreparedStatement stmt2 = database.prepareStatement(sql2);
		ParamHelper.create(stmt2).set(email.from).set(email.to).set(email.subject).set(email.body).set(emailid);
		stmt2.close();
		String sql1 = "select id from attachment where eid = ?";
		PreparedStatement stmt1 = database.prepareStatement(sql1);
		ParamHelper.create(stmt1).set(emailid);
		ResultSet rs1 = stmt1.executeQuery();
		if (!rs1.next()){
			//add attachment
			for (int i = 0; i < email.attachment.length; i++)
			{
				String sql3 = "insert into attachment (path, eid) VALUES (?,?)";
				PreparedStatement stmt3 = database.prepareStatement(sql3);
				ParamHelper.create(stmt3).set(email.attachment[i]).set(emailid);
				stmt3.executeUpdate();
				stmt3.close();
			}
		}
		stmt1.close();
	}

	@Override
	public EEmail readEmail(long emaild)
	{
		EEmail e = new EEmail();
		try
		{
			String sql = "select id,`from`, `to`, `subject`, `sendtime`, `ctime`, `state`, `messageid`, `status`, `assignby`, `qualifiedtime`, `qualifiedby`, `junk`, `body`, `cid` from apply2.email where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(emaild);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				e.id = rh.l();
				e.from = rh.s();
				e.to = rh.s();
				e.subject = rh.s();
				e.sendtime = rh.t();
				e.ctime = rh.t();
				e.state = rh.i();
				e.messageid = rh.s();
				e.status = rh.i();
				e.assignby = rh.l();
				e.quanlifieddate = rh.t();
				e.quanlifiedby = rh.l();
				e.junk = rh.b();
				e.body = rh.s();
				e.candidateid = rh.l();
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return e;
	}

	@Override
	public boolean isEmailReaded(long userid, long emailid)
	{
		long count = -1;
		try
		{
			String sql = "select count(eid) from unread where uid=? and eid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(userid).set(emailid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getLong(1);
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count == 0;
	}

	@Override
	public void markEmailAsReaded(long userid, long emailid)
	{
		try
		{
			String sql = "insert into unread(eid,uid) select * from ( select ?,?) as tmp where not exists (select * from unread where eid=? and uid = ?)";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(emailid).set(userid).set(emailid).set(userid);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void markAllAsReaded(long userid)
	{
		try
		{
			String sql = "delete from apply2.UNREAD where uid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(userid);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

	}

	public long createCandidateByEmail(String address, EEmail email, long jobid) throws SQLException
	{

		ECandidate entity = new ECandidate();
		entity.spec = defaulttext;
		entity.level = defaulttext;
		entity.tech = defaulttext;
		entity.state = 0;
		entity.junk = true;
		entity.ctime = email.ctime;
		entity.attachment = "";
		entity.birth = Calendar.getInstance();
		entity.color = 0;
		entity.creator = 0;
		entity.email = email.from;
		entity.eng = 0;
		entity.englishteststate = 0;
		entity.fullname = defaulttext;
		entity.gender = true;
		entity.iq = 0;
		entity.iqteststate = 0;
		entity.jobid = jobid;
		entity.lmtime = Calendar.getInstance();
		entity.phone = defaulttext;
		entity.resumestatus = 0;
		entity.star = false;
		entity.cjt = defaulttext;
		entity.curemp = defaulttext;
		entity.salary = defaulttext;
		entity.twe = defaulttext;

		return createCandidate(entity);

	}

	@Override
	public long createEmail(EEmail email, long jobid)
	{
		try
		{
			long emi;

			String sql1 = "select id from email where messageid = ?";
			PreparedStatement stmt1 = database.prepareStatement(sql1);
			ParamHelper.create(stmt1).set(email.messageid);
			ResultSet rs1 = stmt1.executeQuery();
			String address;
			//nếu chưa có mail
			if (!rs1.next())
			{
				//tạo email chưa trỏ đến cid nào cả

				long cid = -1;
				if (email.from.equals(mailaddress))
				{
					address = email.to;
				} else if (email.to.equals(mailaddress))
				{
					address = email.from;
				} else
				{
					address = email.from;
				}

				String sql2 = "insert into email (`from`, `to`, `subject`, `sendtime`, `ctime`, `state`, `messageid`, `status`," + " `cid`, `assignby`, `qualifiedtime`, `qualifiedby`, `junk`, `body`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement stmt2 = database.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
				ParamHelper.create(stmt2).set(email.from).set(email.to).set(email.subject).set(email.sendtime).set(email.ctime).set(email.state).set(email.messageid).set(email.status).set(cid).set(email.assignby).set(email.quanlifieddate).set(email.quanlifiedby).set(email.junk).set(email.body);
				stmt2.executeUpdate();
				ResultSet rs2 = stmt2.getGeneratedKeys();
				rs2.next();
				emi = rs2.getLong(1);

				//add attachment
				for (int i = 0; i < email.attachment.length; i++)
				{
					String sql3 = "insert into attachment (path, eid) VALUES (?,?)";
					PreparedStatement stmt3 = database.prepareStatement(sql3);
					ParamHelper.create(stmt3).set(email.attachment[i]).set(emi);
					stmt3.executeUpdate();
				}

			} else
			{
				emi = rs1.getLong("id");
				address = email.from;
				fixEmailEx(emi, email);
			}

			String selectcandidate = "select id from candidate where email=? and jobid = ?";
			PreparedStatement stmt = database.prepareStatement(selectcandidate);
			ParamHelper.create(stmt).set(address).set(jobid);
			ResultSet resultSet = stmt.executeQuery();
			long candidateid;
			if (!resultSet.next()) //ko có ứng viên
			{
				candidateid = createCandidateByEmail(address, email, jobid);
			} else
			{
				candidateid = resultSet.getLong("id");
			}

			//sua cid
			fixEmail(emi, address, candidateid);

			return emi;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private String buildOrderBy(String[] orderby)
	{
		orderby = orderby == null ? new String[0] : orderby;
		String sqlorder = "";
		if (orderby.length == 0)
		{
			sqlorder = " , ctime desc";
		}

		for (String anOrderby : orderby)
		{
			sqlorder += " , " + anOrderby.substring(1) + (anOrderby.charAt(1) == '-' ? " desc " : " asc ");
		}

		return sqlorder;
	}

	private void buildSearchCondition(ParamHelper ph, String[] filter, String[] value)
	{
		filter = filter == null ? new String[0] : filter;
		value = value == null ? new String[0] : value;
		for (int i = 0; i < filter.length; i++)
		{
			if (value[i].equals("")) continue;
			if (filter[i].equals("iq") || filter[i].equals("eng"))
			{
				Integer inte = Integer.parseInt(value[i]);
				ph.set(inte.intValue());
			} else if (filter[i].equals("gender"))
			{
				Boolean inte = Boolean.parseBoolean(value[i]);
				ph.set(inte);
			} else if (filter[i].equals("birth") || filter[i].equals("ctime"))
			{
				Calendar cal = Calendar.getInstance();
				Long longv = Long.parseLong(value[i]);
				cal.setTimeInMillis(longv);
				ph.set(cal);
			} else
			{
				ph.set(value[i]);
			}
		}
	}

	private String buildSearchCondition(String[] filter, String[] value, String[] operator)
	{
		filter = filter == null ? new String[0] : filter;
		value = value == null ? new String[0] : value;
		operator = operator == null ? new String[0] : operator;
		String condition = "";
		for (int i = 0; i < filter.length; i++)
		{
			if (value[i].equals("")) continue;
			if (operator[i].equals("like"))
			{
				value[i] = "%" + value[i] + "%";
			}
			condition += " and " + filter[i] + " " + operator[i] + " ? ";
		}

		return condition;
	}

	@Override
	public List<ECandidate> listCandidateByJob(long userid, long[] job, boolean archived, int cvstate, String keyword, int n, int p, String[] filter, String[] value, String[] operator, String[] orderby)
	{
		job = job == null ? new long[0] : job;

		// order by
		String sqlorder = buildOrderBy(orderby);

		//search condition
		String condition = buildSearchCondition(filter, value, operator);

		List<ECandidate> eCal = new ArrayList<ECandidate>();

		try
		{
			String key = "";
			String keyState;
			if (archived && cvstate == -1)
			{
				key = "";
				keyState = "";
			} else if (!archived && cvstate == -1)
			{
				key = " state<>-1";
				keyState = "";
			} else if (archived)
			{
				keyState = " candidate.resumestatus=" + cvstate;
			} else
			{
				key = " state<>-1 and ";
				keyState = " resumestatus=" + cvstate;
			}

			String sql = "select candidate.id as candidateid, fullname, `email`, `phone`, `color`, `star`, `jobid`, `engtestate`, `iqteststate`, `eng`, `iq`, `attachment`,`resumestatus`, candidate.`state`, candidate.`lmtime`, candidate.`ctime`, `birth`, `gender`, `junk`,cjt,curemp,salary,twe,spec,level,tech from candidate LEFT JOIN `comment` on candidate.id = `comment`.cid LEFT JOIN `info` on candidate.id = info.cid WHERE (`comment`" +
					".`comment` like ?" +
					" or info.`value` like ? or fullname like ? or email like ?) " + ((key + keyState).equals("") ? "" : " and ") + key + keyState;

			if (job.length >= 1)
			{
				sql = sql + " and jobid in(";
				for (long ignored : job)
				{
					sql = sql + "?,";
				}
				StringBuilder sqlbul = new StringBuilder(sql);
				sqlbul.deleteCharAt(sqlbul.length() - 1);
				sql = sqlbul.toString() + ")";
			}

			sql = "select N.* , max(IsNull(eid)) as v from (select L.* , email.id as emailid from (" + sql + ") as L left join email on candidateid = email.cid) as N \n" +
					"left join unread on emailid = eid and unread.uid = ? ";

			sql += condition + " GROUP BY candidateid order by junk desc, star desc" + sqlorder + ", v " +
			"desc limit ?,?";

			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper ph = ParamHelper.create(stmt);

			ph.set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");
			for (long aJob : job)
			{
				ph.set(aJob);
			}

			//userid
			ph.set(userid);

			buildSearchCondition(ph, filter, value);

			ph.set(p * n).set(n);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ECandidate e = new ECandidate();
				ResultHelper rh = new ResultHelper(rs);
				e.id = rh.l();
				e.fullname = rh.s();
				e.email = rh.s();
				e.phone = rh.s();
				e.color = rh.i();
				e.star = rh.b();
				e.jobid = rh.l();
				e.englishteststate = rh.i();
				e.iqteststate = rh.i();
				e.eng = rh.i();
				e.iq = rh.i();
				e.attachment = rh.s();
				e.resumestatus = rh.i();
				e.state = rh.i();
				e.lmtime = rh.t();
				e.ctime = rh.t();
				e.birth = rh.t();
				e.gender = rh.b();
				e.junk = rh.b();
				e.cjt = rh.s();
				e.curemp = rh.s();
				e.salary = rh.s();
				e.twe = rh.s();
				e.spec = rh.s();
				e.level = rh.s();
				e.tech = rh.s();
				eCal.add(e);
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return eCal;
	}

	@Override
	public long countCandidateByJob(long[] job, boolean archived, int cvstate, String keyword, String[] filter, String[] value, String[] operator)
	{
		job = job == null ? new long[0] : job;

		//search condition
		String condition = buildSearchCondition(filter, value, operator);

		long count = 0;
		try
		{
			String key = "";
			String keyState;
			if (archived && cvstate == -1)
			{
				key = "";
				keyState = "";
			} else if (!archived && cvstate == -1)
			{
				key = " state<>-1";
				keyState = "";
			} else if (archived)
			{
				keyState = " candidate.resumestatus=" + cvstate;
			} else
			{
				key = " state<>-1 and ";
				keyState = " resumestatus=" + cvstate;
			}


			String sql = " select count(distinct candidate.id) from candidate LEFT JOIN `comment` on candidate.id = `comment`.cid LEFT JOIN `info` on candidate.id = info.cid WHERE ( `comment`.`comment` like ? or info.`value` like ? or fullname like ? or email like ? ) " + ((key + keyState).equals("") ? "" : " and ") + key + keyState;

			sql += condition;

			if (job.length >= 1)
			{
				sql = sql + " and jobid in(";
				for (long aJob : job)
				{
					sql = sql + "?,";
				}
				StringBuilder sqlbul = new StringBuilder(sql);
				sqlbul.deleteCharAt(sqlbul.length() - 1);
				sql = sqlbul.toString() + ")";
			}

			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper ph = ParamHelper.create(stmt);

			ph.set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%").set("%" + keyword + "%");


			buildSearchCondition(ph, filter, value);
			for (long aJob : job)
			{
				ph.set(aJob);

			}
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getLong(1);
			}

		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return count;
	}

	@Override
	public void markEmailAsUnreaded(long userid, long emailid)
	{
		try
		{
			String sql = "delete from unread where eid = ? and  uid = ?";
			//String sql = "insert into unread(eid,uid) values(?,?)";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(emailid).set(userid);
			stmt.executeUpdate();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void junkMail(long emailid)
	{
		try
		{
			String sql = "update email set junk=1 where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(emailid);
			stmt.executeUpdate();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void unjunkMail(long emailid)
	{
		try
		{
			String sql = "update email set junk=0 where id=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(emailid);
			stmt.executeUpdate();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void cleanUnread(long userid)
	{
		try
		{
			String sql = "delete from unread where uid=?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(userid);
			stmt.executeUpdate();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<EEmail> listUnreadEmail(long userid, String email, int n, int p)
	{

		long count = -1;
		try
		{
			long ce = countFromEmail(email);
			//select count(*) from email
			//
			//SELECT count(*) from  # order by sendtime desc limit 1, 3
			String sql = "select id, `subject`, sendtime, ctime, messageid, body, junk  from ( (select * from email where `from`= ? and junk =false) as N) left join  ((select* from unread where uid=?)  as T ) on  (N.id=T.eid  ) where T.uid is null order by sendtime desc limit ?,?";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(email).set(userid).set(n * (p)).set(n);

			List<EEmail> els = new ArrayList<EEmail>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				EEmail e = new EEmail();
				e.id = rh.l();
				e.subject = rh.s();
				e.sendtime = rh.t();
				e.ctime = rh.t();
				e.messageid = rh.s();
				e.body = rh.s();
				e.junk = rh.b();
				els.add(e);
			}

			rs.close();
			stmt.close();

			return els;
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}

	}

	@Override
	public List<String[]> listCandidateAttachments(long cid)
	{
		List<String[]> atts = new ArrayList<String[]>();
		try
		{
			String sql = "select path, eid from attachment, email where cid=? and email.id = eid";
			PreparedStatement stmt = database.prepareStatement(sql);
			ParamHelper.create(stmt).set(cid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				ResultHelper rh = new ResultHelper(rs);
				String path = rh.s();
				String eid = rh.l() + "";
				atts.add(new String[]{eid, path});
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return atts;
	}

}
