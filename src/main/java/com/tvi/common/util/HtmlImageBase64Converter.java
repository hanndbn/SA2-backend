
package com.tvi.common.util;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlImageBase64Converter implements IHtmlImageBase64Converter
{
	private final IFileSaveHelper filesaver;
	public HtmlImageBase64Converter(IFileSaveHelper filesaver)
	{
		this.filesaver = filesaver;
	}

	@Override
	public String convert(String html)
	{
		Document doc = Jsoup.parse(html);
		Elements newsHeadlines = doc.select("img");
		
		
		for(Element e : newsHeadlines)
		{
			String src = e.attr("src");
			if(src.startsWith("data:image/"))
			{
				src = src.substring(src.indexOf("base64,") + 7);
				if( Base64.isBase64(src))
				{
					byte[] data = Base64.decodeBase64(src);
					e.attr("src", filesaver.saveFile(data, "png"));
				}
			}
		}
		
		return doc.toString();
	}	
        
}
