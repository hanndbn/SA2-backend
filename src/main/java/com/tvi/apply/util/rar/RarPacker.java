//dinhnv modified by thanhpk
package com.tvi.apply.util.rar;

import java.io.*;
import java.util.List;

import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class RarPacker implements IRarPacker
{
	@Override
	public List<String> unpackLevel1(String path)
	{
		File arch = new File(path);

		if (!arch.exists())
		{
			throw new RuntimeException("the path does not exit: " + path);
		}

		File dest = new File(path.replace('.', '_'));

		if (!dest.exists() || !dest.isDirectory())
		{
			dest.mkdir();
		}

		extractArchive(arch, dest);
		List<String> fs = new ArrayList<String>();
		loopFiles(fs, dest.listFiles());
		return fs;
	}

	@Override
	public boolean isCompressedFile(String path)
	{
		File file= new File(path);
		try
		{
			return isRarFile(file) && FilenameUtils.getExtension(file.getAbsolutePath()).equals("zip") || isZipFile(file) && FilenameUtils.getExtension(file.getAbsolutePath()).equals("rar") ;
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void extractArchive(File archive, File destination)
	{
		try
		{

			if (isZipFile(archive))
			{
				unZip(archive.getAbsolutePath(), destination.getAbsolutePath());
			} else if (isRarFile(archive))
			{

				Archive arch = new Archive(new FileVolumeManager(archive));
				FileHeader fh;

				while ( null != (fh = arch.nextFileHeader()) )
				{
					if (fh.isEncrypted())
					{
						System.out.println("file is encrypted cannot extract: " + fh.getFileNameString());
						continue;
					}

					if (fh.isDirectory())
					{
						createDirectory(fh, destination);
					} else
					{
						File f = createFile(fh, destination);

						OutputStream stream = new FileOutputStream(f);
						arch.extractFile(fh, stream);
						stream.close();
					}
				}
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static File createFile(FileHeader fh, File destination)
	{
		File f;
		String name;
		if (fh.isFileHeader() && fh.isUnicode())
		{
			name = fh.getFileNameW();
		} else
		{
			name = fh.getFileNameString();
		}
		f = new File(destination, name);
		if (!f.exists())
		{
			try
			{
				f = makeFile(destination, name);
			} catch (IOException e)
			{
				throw new RuntimeException("error creating the new file: " + f.getName(), e);
			}
		}
		return f;
	}

	private static File makeFile(File destination, String name) throws IOException
	{
		String[] dirs = name.split("\\\\");

		String path = "";
		int size = dirs.length;
		if (size == 1)
		{
			return new File(destination, name);
		} else if (size > 1)
		{
			for (int i = 0; i < dirs.length - 1; i++)
			{
				path = path + File.separator + dirs[i];
				new File(destination, path).mkdir();
			}
			path = path + File.separator + dirs[dirs.length - 1];
			File f = new File(destination, path);
			f.createNewFile();
			return f;
		} else
		{
			return null;
		}
	}

	private static void createDirectory(FileHeader fh, File destination)
	{
		File f;
		if (fh.isDirectory() && fh.isUnicode())
		{
			f = new File(destination, fh.getFileNameW());
			if (!f.exists())
			{
				makeDirectory(destination, fh.getFileNameW());
			}
		} else if (fh.isDirectory())
		{
			f = new File(destination, fh.getFileNameString());
			if (!f.exists())
			{
				makeDirectory(destination, fh.getFileNameString());
			}
		}
	}

	private static void makeDirectory(File destination, String fileName)
	{
		String[] dirs = fileName.split("\\\\");

		String path = "";
		for (String dir : dirs)
		{
			path = path + File.separator + dir;
			new File(destination, path).mkdir();
		}
	}

	@Override
	public String pack(List<String> path, String outname)
	{
		
		String str = path.get(0);
		List<File> sourceFiles = new ArrayList<File>();
		String str2 = "";
		int values = 0;
		for (int i = str.length() - 1; i >= 0; i--)
		{
			if (str.charAt(i) == '/')
			{
				values = i;
				//  System.out.println("" + str.charAt(i));
				break;
			}
		}
		for (int i = 0; i < values; i++)
		{
			str2 = str2 + str.charAt(i);
		}

		//  System.out.println("str2: " + str2);
		for (String fileName : path)
		{
			File f = new File(fileName);
			if (f.exists()) sourceFiles.add(f);
		}

		if (compress(sourceFiles, outname, str2))
		{
			System.out.println(path.size() + " file(s) compressed.");
		} else
		{
			System.out.println("Fail to compress files!");
		}
		outname = str2 + "/" + outname;
		return outname;
	}

	public static boolean compress(List<File> sourceFiles, String zipFile, String zipDestination)
	{
		boolean success = true;
		try
		{
			String zipFilePath = zipDestination.concat("/").concat(zipFile);
			FileOutputStream fos = new FileOutputStream(zipFilePath);
			ZipOutputStream zos = new ZipOutputStream(fos);

			byte[] buffer = new byte[1024];
			for (File f : sourceFiles)
			{
				FileInputStream fis = new FileInputStream(f);
				zos.putNextEntry(new ZipEntry(f.getName()));
				int len;
				while ((len = fis.read(buffer)) > 0)
				{
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}

			zos.close();
			fos.close();
		} catch (Exception e)
		{
			success = false;
		}
		return success;
	}

	public static boolean isZipFile(File file) throws IOException
	{
		if (file.isDirectory())
		{
			return false;
		}
		if (!file.canRead())
		{
			throw new IOException("Cannot read file " + file.getAbsolutePath());
		}
		if (file.length() < 4)
		{
			return false;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		int test = in.readInt();
		in.close();
		return test == 0x504b0304;
	}

	public boolean isRarFile(File file) throws IOException
	{
		if (file.isDirectory())
		{
			return false;
		}
		if (!file.canRead())
		{
			throw new IOException("Cannot read file " + file.getAbsolutePath());
		}
		if (file.length() < 7)
		{
			return false;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		long test = in.readInt();

		if( test == 0x52617221)
		{
			test = in.readInt() & 0xffffff00;
			in.close();
			return test == 0x1A070000;
		}
		in.close();
		return false;
	}

	public void unZip(String zipFile, String outputFolder)
	{
		byte[] buffer = new byte[1024];

		try
		{
			//create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists())
			{
				folder.mkdir();
			}

			//get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null)
			{

				String fileName = ze.getName();

				File newFile = new File(outputFolder + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public void loopFiles(List<String> filenames, File[] files)
	{
		for (File file : files)
		{
			if (file.isDirectory())
			{
				loopFiles(filenames, file.listFiles()); // Calls same method again.
			} else
			{
				filenames.add(file.getAbsolutePath());
			}
		}
	}

}
