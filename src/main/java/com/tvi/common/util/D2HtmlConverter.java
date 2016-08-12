package com.tvi.common.util;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class D2HtmlConverter implements ID2HtmlConverter {

    private final IFileSaveHelper filesaver;
    private final SocketOpenOfficeConnection connection;
    private DocumentConverter converter;

    public D2HtmlConverter(IFileSaveHelper filesaver) {
        connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
        this.filesaver = filesaver;
    }

    @Override
    public void clean(String path) {
        String html = null;
        try {
            html = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document doc = Jsoup.parse(html);
        Elements newsHeadlines = doc.select("img");
        for (Element e : newsHeadlines) {
            String src = e.attr("src");
            if (!src.startsWith("data:image/")) {
                filesaver.deleteRel(src);
            }
        }
        filesaver.deleteAbs(path);
    }

    @Override
    public String convert(InputStream is) {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("tempdir_");
        } catch (IOException ex) {
            Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        //create a tempority doc file
        long time = new Date().getTime();
        long threadid = Thread.currentThread().getId();
        String filename = dir + "/" + time + "_" + threadid + ".doc";
        File docfile = new File(filename);
        int fi = 1;
        while (docfile.exists()) {
            filename = time + "_" + threadid + "_" + fi + ".html";
            docfile = new File(filename);
            fi++;
        }

        filename = dir + "/" + time + "_" + threadid + ".html";
        File htmlfile = new File(filename);
        fi = 1;
        while (htmlfile.exists()) {
            filename = time + "_" + threadid + "_" + fi + ".html";
            htmlfile = new File(filename);
            fi++;
        }
        try {
            Files.write(Paths.get(docfile.getAbsolutePath()), IOUtils.toByteArray(is));
        } catch (IOException ex) {
            Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        //OpenOfficeConnection connection; = new SocketOpenOfficeConnection("127.0.0.1", 8100);
        converter = keepAlive();

        converter.convert(docfile, htmlfile);
        String html = null;
        try {
            html = new String(Files.readAllBytes(Paths.get(htmlfile.getAbsolutePath())), "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        Document doc = Jsoup.parse(html);
        Elements newsHeadlines = doc.select("img");
        for (Element e : newsHeadlines) {
            String src = e.attr("src");
            if (!src.startsWith("data:image/")) {
                try {
                    String url = filesaver.saveFile(Files.readAllBytes(Paths.get(dir + "/" + src)), "png");
                    e.attr("src", url);
                } catch (IOException ex) {
                    Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            FileUtils.deleteDirectory(new File(dir.toString()));
        } catch (IOException ex) {
            Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc.toString();
    }

    private DocumentConverter keepAlive() {
        if (connection.isConnected() == false) {
            try {
                connection.connect();
                converter = new OpenOfficeDocumentConverter(connection);
            } catch (ConnectException ex) {
                Logger.getLogger(D2HtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return converter;
    }
}
