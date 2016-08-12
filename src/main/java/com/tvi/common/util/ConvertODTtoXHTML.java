/*
package com.tvi.common.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLConverter;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

public class ConvertODTtoXHTML {

    private final IFileSaveHelper fileSave;

    public ConvertODTtoXHTML(IFileSaveHelper fileSave) {
        this.fileSave = fileSave;
    }

    public String convert(InputStream inputstream) {
        String xml = "Loi";
        try {
            List<String> urls = new ArrayList<String>();
            // 1) Load odt with ODFDOM
            OdfTextDocument document = OdfTextDocument.loadDocument(inputstream);

            // Get File of All Package 
            Object[] file = document.getPackage().getFilePaths().toArray();
             
            Writer out = new StringWriter();
            XHTMLConverter.getInstance().convert(document, out, null);
            xml = out.toString();

            
            for (Object file1 : file) {
                if (file1.toString().startsWith("Pictures")||file1.toString().startsWith("media")) {
                     xml = xml.replaceAll(file1.toString(),fileSave.saveFile(document.getPackage().getBytes(file1.toString()),
                            file1.toString().substring(file1.toString().lastIndexOf(".") + 1)));
                }

            }
           
           
           xml = xml.replaceAll("style=\"width:100%;height:100%;display:block\"></img>",  "  />");
           xml = xml.replaceAll("></img>",  "  ///>");
           
        } catch (Exception e) {
            throw new RuntimeException("cannot convert to html");
        }
        return xml;
    }

}
*/