
package com.tvi.common.util;

import java.io.InputStream;

public interface ID2HtmlConverter
{
	String convert(InputStream is);
	void clean(String path);
}
