package ru.nekki.test.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.nekki.test.dao.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
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
    private static final String CONTENT = "content";
    private static final String CREATION_DATE = "creationDate";
    private static final String ENTRY = "Entry";
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final int MAX_CONTENT_LENGTH = 1024;
    private final static Logger logger =
            LogManager.getLogger(XMLParser.class);

    public static Entry parse(Path file) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.toFile());

        if (!ENTRY.equals(doc.getDocumentElement().getTagName())) {
            logger.debug(file + " has wrong xml structure");
            return null;
        }

        String content = getString(doc.getElementsByTagName(CONTENT));
        String creationDate = getString(doc.getElementsByTagName(CREATION_DATE));

        Entry entry = new Entry();
        entry.setContent(content);
        entry.setCreationDate(parseDate(creationDate));
        return entry;
    }

    private static Date parseDate(String date) {
        if (date == null) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            logger.error(
                    String.format("Wrong date format (%s is expected): %s",
                            DATE_FORMAT, date), e);
        }
        return null;
    }

    private static String getString(NodeList nodes) {
        if (nodes == null || nodes.getLength() != 1) return null;
        Node item = nodes.item(0);
        if (item == null) return null;
        String textContent = item.getTextContent();
        if (textContent != null && textContent.length() > MAX_CONTENT_LENGTH) {
            return textContent.substring(0, MAX_CONTENT_LENGTH - 1);
        }
        return textContent;
    }
}
