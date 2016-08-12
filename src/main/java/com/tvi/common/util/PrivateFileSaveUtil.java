package com.tvi.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrivateFileSaveUtil implements IFileSaveHelper
{

	private final String basepath;
	private final String baseurl;

	public PrivateFileSaveUtil(String basepath, String baseurl)
	{
		this.baseurl = baseurl;

		if (basepath.endsWith("/")) {
			this.basepath = basepath.substring(0, basepath.length() - 1) + "/private";
		} else {
			this.basepath = basepath + "/private";
		}
	}

	@Override
	public String saveFile(byte[] bytes, String extenstiontype)
	{

		if (extenstiontype == "jsp") {
			extenstiontype = "jspsec";
		}

		String filename = mkfile(extenstiontype);
		File file = new File(filename);
		try {
			//FileOutputStream out = new FileOutputStream(filename);
			//out.write(bytes);
			//out.close();
			Files.write(Paths.get(filename), bytes);
		} catch (IOException ex) {
			throw new RuntimeException("Cannot write to file");
		}

		return this.baseurl + "?path=x_" + file.getName();
	}

	@Override
	public String saveHTML(String content)
	{
		String filename = mkfile("html");

		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException ex) {
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
		try {
			Files.delete(Paths.get(path));
		} catch (IOException ex) {
			Logger.getLogger(PrivateFileSaveUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	String mkfile(String extention)
	{
		long time = new Date().getTime();
		long threadid = Thread.currentThread().getId();
		String filename = basepath + "/" + time + "_" + threadid + "." + extention;

		int fi = 1;
		while (Files.exists(Paths.get(filename))) {
			filename = basepath + "/" + time + "_" + threadid + "_" + fi + "." + extention;

			fi++;
		}
		return filename;

	}
}
