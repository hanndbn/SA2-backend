package com.tvi.CaptchaProvider;

import com.tvi.common.entity.CaptchaHead;
import com.tvi.common.ICaptchaProvider;
import com.tvi.common.IDatabase;
import com.tvi.common.util.CaptchaImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class CaptchaProvider implements ICaptchaProvider
{

	private final IDatabase database;
	
	public CaptchaProvider(IDatabase database)
	{
		this.database = database;
	}

	private static String encodetoBase64(BufferedImage image, String type)
	{
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = Base64.encodeBase64String(imageBytes);
			bos.close();
		} catch (IOException e)
		{
			throw new RuntimeException("cant create captcha image");
		}
		return imageString;
	}

	@Override
	public CaptchaHead create(String ip)
	{
		CaptchaImage ci = new CaptchaImage();

		String imagedata = encodetoBase64(ci.getCaptchaImage(), "png");
		String cap = ci.getCaptchaString();

		CallableStatement cs = database.prepareCall("call createCaptcha(?,?)");
		long capid = -1;
		try
		{
			cs.setString(1, cap);
			cs.setString(2, ip);
			ResultSet rs = cs.executeQuery();
			rs.next();
			capid = rs.getLong(1);
			rs.close();
		} catch (SQLException e)
		{
			throw new RuntimeException("cannot create captcha:" + e.toString());
		}
		CaptchaHead ch = new CaptchaHead(capid, "data:image/png;base64," + imagedata);
		return ch;
	}

	@Override
	public boolean validate(long capid, String captcha,String ip)
	{
		CallableStatement cs = database.prepareCall("{call checkCaptcha(?,?,?)}");
		int ret = -1;
		try
		{
			cs.setLong(1, capid);
			cs.setString(2, captcha);
			cs.setString(3, ip);
			ResultSet rs = cs.executeQuery();
			rs.next();
			ret = rs.getInt(1);
			if (ret == 0)
			{
				return true;
			}
			return false;
		} catch (SQLException e)
		{
			throw new RuntimeException("cannot validate captcha:" + e.toString());
		}
	}

}
