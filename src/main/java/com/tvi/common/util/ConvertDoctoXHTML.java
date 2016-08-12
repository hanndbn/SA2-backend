/*
 
package com.tvi.common.util;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;


public class ConvertDoctoXHTML {
    
    private final IFileSaveHelper fileSave;
    public ConvertDoctoXHTML(IFileSaveHelper fileSave) {
        this.fileSave = fileSave;
    }
    
    public String convert(InputStream inputstream) {
        String html = null;
        try {
            long time = new Date().getTime();
            long threadid = Thread.currentThread().getId();
            String filename =   time + "_" + threadid  ;
            
            
            File base = new File("./"+filename);
            base.mkdir();
            File fin =  File.createTempFile(filename,".sex" ,base);
            FileOutputStream out = new FileOutputStream(fin);
            IOUtils.copy(inputstream, out);
            File fout =  File.createTempFile(filename,".html" ,base);            
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
            connection.connect();
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);            
            converter.convert(fin, fout);
            fin.delete();
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(fout));
            while ((line = br.readLine()) != null) {
				html += line;
			}
            List <String> filenames = new ArrayList<String>();
            int i = 0;
            int k1 ;
            int k2;
            while ( (i = html.indexOf("<IMG SRC=\"",i))!= -1){
                k1= html.indexOf("\"",i);
                k2= html.indexOf("\"",k1+1);
                i = i+1;
                String name = html.substring(k1, k2);
                Path path = Paths.get("./"+filename+"//"+name);
                byte[] bytes = Files.readAllBytes(path);
                String newname = fileSave.saveFile(bytes, name.substring(name.toString().lastIndexOf(".") + 1));
                html = html.replaceFirst(name, newname);
                
                
            }
            while ( (i = html.indexOf("<IMG SRC=\"",i))!= -1){
                k1= html.indexOf("\"",i);
                k2= html.indexOf("\"",k1+1);
                i = i+1;
                String name = html.substring(k1, k2);
                 File tmp = new File("./"+filename+"//"+name);
                tmp.delete();
            }
            
          fout.delete();
        } catch (IOException ex) {
            Logger.getLogger(ConvertDoctoXHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
        return html ;
    
    }
}
*/