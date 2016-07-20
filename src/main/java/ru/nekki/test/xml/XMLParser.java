package ru.nekki.test.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <Entry>
 * <!--строка длиной до 1024 символов-->
 * <content>Содержимое записи</content>
 * <!--дата создания записи-->
 * <creationDate>2014-01-01 00:00:00</date>
 * </Entry>
 * <p/>
 * Created by AnVIgnatev on 20.07.2016.
 */
public class XMLParser {
    public static void parse(Path file) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.toFile());

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("staff");

        System.out.println();

       /* DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document parse = newDocumentBuilder.parse(file.toFile());
        System.out.println(parse.getFirstChild().getTextContent());

*/
        /*
        JAXBContext jc = JAXBContext.newInstance(Entry.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            Object je = unmarshaller.unmarshal(
                    reader);
            System.out.println(je);
        }*/
    }

    public static void main(String[] args) throws JAXBException, IOException, ParserConfigurationException, SAXException {
        parse(Paths.get("c:/test/1.xml"));
    }
}
