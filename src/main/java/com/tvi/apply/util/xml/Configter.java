package com.tvi.apply.util.xml;

import com.tvi.apply.util.file.IFileHelper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configter implements IConfiger {

    final String filepath;
    final IFileHelper filemgr;

    public Configter(String filepath, IFileHelper filemgr) {
        this.filepath = filepath;

        this.filemgr = filemgr;
    }

    @Override
    public String load(String nodepath) {
        try {
            String values = "";
            int i = 0;

            String[] part = nodepath.split(" ");
            int n = part.length;

            File xmlFile = new File("run.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList root = doc.getElementsByTagName("root");
            //System.out.println(getnode(0,root.item(0)));
            Node node = root.item(0);
            return getnode2(i + 1, ((Element) node).getElementsByTagName(part[i]).item(0), part);

        } catch (SAXException ex) {
            Logger.getLogger(Configter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Configter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static String getnode2(int i, Node node, String[] part) {
        int m = part.length;
        if (i == m) {
            return node.getTextContent();
        }
        return getnode2(i + 1, ((Element) node).getElementsByTagName(part[i]).item(0), part);
    }

    @Override
    public void save(String nodepath, String value) {

    }
    public static void main(String[] args) {
        Configter cf=new Configter(null, null);
        System.out.println(cf.load("account2 password"));
    }
}
