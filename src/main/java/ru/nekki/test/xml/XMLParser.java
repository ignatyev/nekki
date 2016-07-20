package ru.nekki.test.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.nekki.test.dao.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <Entry>
 * <!--строка длиной до 1024 символов-->
 * <content>Содержимое записи</content>
 * <!--дата создания записи-->
 * <creationDate>2014-01-01 00:00:00</creationDate>
 * </Entry>
 * <p/>
 * Created by AnVIgnatev on 20.07.2016.
 */
public class XMLParser {

    public static final String CONTENT = "content";
    public static final String CREATION_DATE = "creationDate";
    public static final String ENTRY = "Entry";

    public static Entry parse(Path file) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.toFile());

        //wrong file xml structure
        if (!ENTRY.equals(doc.getDocumentElement().getTagName())) return null;

        String content = getString(doc.getElementsByTagName(CONTENT));
        String creationDate = getString(doc.getElementsByTagName(CREATION_DATE));

        Entry entry = new Entry();
        entry.setContent(content);
        entry.setCreationDate(parseDate(creationDate));
        return entry;
    }

    private static Date parseDate(String date) {
        if (date == null) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getString(NodeList nodes) {
        if (nodes == null || nodes.getLength() != 1) return null;
        Node item = nodes.item(0);
        if (item == null) return null;
        return item.getTextContent();
    }

    public static void main(String[] args) throws JAXBException, IOException, ParserConfigurationException, SAXException {
        parse(Paths.get("c:/temp/1.xml"));
    }
}
