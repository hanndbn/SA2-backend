//thanhpk

package com.tvi.apply.util.file;

import com.google.common.base.CharMatcher;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Date;
import java.util.regex.Pattern;

public class PrivateFileHelper implements IFileHelper
{

	final int maxnamesign = 100;
	private final String basepath;
	private final String baseurl;

	public String convertToAbsolute(String path)
	{
		path = path.substring(this.baseurl.length());
		path = path.substring(8); //remove 'private/'
		return this.basepath + "/" + path;
	}

	@Override
	public String convertToRelative(String path)
	{
		if(!path.startsWith(basepath) )
			return null;

		path = path.substring(basepath.length());
		return this.baseurl + "/path=x_" + path;
	}

	public String toAbsolute(String path)
	{
		path = path.substring(this.baseurl.length());
		path = path.substring(8); //remove 'path=x_/'
		return this.basepath + "/" + path;
	}
	public String getBasepath()
	{
		return this.basepath;
	}

	public PrivateFileHelper(String basepath, String baseurl)
	{
		this.baseurl = baseurl;

		if (basepath.endsWith("/"))
		{
			this.basepath = basepath.substring(0, basepath.length() - 1) + "/private";
		} else
		{
			this.basepath = basepath + "/private";
		}
	}

	@Override
	public String readRelativePath(String path)
	{
		path = convertToAbsolute(path);
		return readAbsolutePath(path);
	}

	@Override
	public String readAbsolutePath(String path)
	{
		try
		{
			byte[] content = Files.readAllBytes(Paths.get(path));
			return new String(content, "UTF-8");
		} catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String saveFile(byte[] bytes, String extenstiontype, String name)
	{
		 name = CharMatcher.INVISIBLE.removeFrom(name);
		if (extenstiontype.equals("jsp"))
		{
			extenstiontype = "jspsec";
		}


		try
		{
			if(bytes == null) bytes = new byte[0];
			String filename = mkfile(name, extenstiontype);
			File file = new File(filename);
			Files.write(Paths.get(filename), bytes);
			return this.baseurl + "?path=x_" + file.getName();
		} catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}


	}

	@Override
	public void deleteRelativePath(String path)
	{
		path = convertToAbsolute(path);
		deleteAbsolutePath(path);
	}

	@Override
	public void deleteAbsolutePath(String path)
	{
		try
		{
			Files.delete(Paths.get(path));
		} catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private boolean isFilenameValid(String file)
	{
		File f = new File(file);
		try
		{
			f.getCanonicalPath();
			return true;
		} catch (IOException e)
		{
			return false;
		}
	}

	String mkfile(String name, String extention) throws UnsupportedEncodingException
	{
		if (extention == null) extention = "";
		if (!isFilenameValid(name))
		{
			byte[] decoded = Base64.decodeBase64(name.substring(9));
			if (isFilenameValid(new String(decoded, "UTF-8")))
			{
				name = new String(decoded, "UTF-8");
			} else
			{

				System.out.println("INVALID: " + name);
				if (name.length() <= 5) name = "invalid_name";
				else return mkfile(name.substring(1), extention);
			}
		}

		//if(extention== null || extention.equals("")) extention = "unk";
		//control the length of file
		if (name.length() > maxnamesign)
		{
			name = name.substring(0, maxnamesign);
		}

		long time = new Date().getTime();
		long threadid = Thread.currentThread().getId();

		String temp = Normalizer.normalize(name, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		name = pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d").replaceAll(" ","_");
		//filename format
		String filename = basepath + "/" + time + '_' + name + extention;

		//add _1, _2, _3 if the file name is aldready existed
		int fi = 1;

		try
		{
			while (Files.exists(Paths.get(filename)))
			{
				filename = basepath + "/" + time + "_" + name + "_" + fi + "." + extention;
				fi++;
			}
		} catch (InvalidPathException e)
		{
			name = "";
			while (Files.exists(Paths.get(filename)))
			{
				filename = basepath + "/" + time + "_" + fi + "." + extention;
				fi++;
			}
		}
		return filename;
	}
}
