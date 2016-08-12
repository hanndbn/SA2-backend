package com.tvi.apply.util.rar;

import com.tvi.apply.util.file.PrivateFileHelper;
import junit.framework.TestCase;

import java.util.List;

public class RarPackerTest extends TestCase
{

	public void testUnpackLevel1() throws Exception
	{
		RarPacker packer = new RarPacker();

		List<String> strings = packer.unpackLevel1("E:/a/c.rar");

		System.out.println(strings.toString());
	}
}