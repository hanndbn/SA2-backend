/*package com.tvi.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicFileSaveUtil implements IFileSaveHelper
{

	private final String basepath;
	private final String baseurl;

	public PublicFileSaveUtil(String basepath, String baseurl)
	{
		this.baseurl = baseurl;

		if (basepath.endsWith("/"))
		{
			this.basepath = basepath.substring(0, basepath.length() - 1) + "/public";
		} else
		{
			this.basepath = basepath + "/public";
		}

	}

	@Override
	public String saveFile(byte[] bytes, String extenstiontype)
	{
		//create a unique file name
		long time = new Date().getTime();
		long threadid = Thread.currentThread().getId();
		String filename = basepath + "/" + time + "_" + threadid + "." + extenstiontype;
		File file = new File(filename);
		int fi = 1;
		while (file.exists())
		{
			filename = time + "_" + threadid + "_" + fi + "." + extenstiontype;
			file = new File(filename);
			fi++;
		}

		try
		{
			//FileOutputStream out = new FileOutputStream(filename);
			//out.write(bytes);
			//out.close();
			Files.write(Paths.get(filename), bytes);
		} catch (IOException ex)
		{
			throw new RuntimeException("Cannot write to file");
		}

		return this.baseurl + "?path=o_" + file.getName();
	}

	@Override
	public String saveHTML(String content)
	{
		//create a unique file name
		long time = new Date().getTime();
		long threadid = Thread.currentThread().getId();
		String filename = basepath + "/" + time + "_" + threadid + ".html";
		File file = new File(filename);
		int fi = 1;
		while (file.exists())
		{
			filename = time + "_" + threadid + "_" + fi + ".html";
			file = new File(filename);
			fi++;
		}

		try
		{
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException ex)
		{
			throw new RuntimeException("Cannot write to file");
		}

		return filename;
	}

	@Override
	public void deleteRel(String path)
	{
		path = path.substring(this.baseurl.length());
		path = path.substring(8);
		path = this.basepath + "/" + path;
		deleteAbs(path);
	}

	@Override
	public void deleteAbs(String path)
	{
		try
		{
			Files.delete(Paths.get(path));
		} catch (IOException ex)
		{
			Logger.getLogger(PrivateFileSaveUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

    
}
*/