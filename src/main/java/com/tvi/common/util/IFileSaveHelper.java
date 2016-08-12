
package com.tvi.common.util;


public interface IFileSaveHelper
{
	String saveFile(byte[] bytes, String fileextention);
	String saveHTML(String content);
	void deleteRel(String path);
	void deleteAbs(String path);
}
