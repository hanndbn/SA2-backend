package com.tvi.apply.util.rar;

import java.util.List;

public interface IRarPacker
{
	String pack(List<String> path, String outname);
	List<String> unpackLevel1(String path);
	boolean isCompressedFile(String path);

}
