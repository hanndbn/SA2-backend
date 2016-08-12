package com.tvi.apply.util.file;


public interface IFileHelper
{
	String readRelativePath(String path);

	String readAbsolutePath(String path);

	//trả về file name mới
	String saveFile(byte[] bytes, String fileextention, String name);

	void deleteRelativePath(String path);

	void deleteAbsolutePath(String path);

	String convertToAbsolute(String url);

	String convertToRelative(String filepath);


	String toAbsolute(String substring);

	String getBasepath();
}
