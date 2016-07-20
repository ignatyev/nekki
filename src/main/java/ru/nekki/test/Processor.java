package ru.nekki.test;

import org.xml.sax.SAXException;
import ru.nekki.test.dao.DAOService;
import ru.nekki.test.dao.Entity;
import ru.nekki.test.xml.XMLParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Processor {

    public static void process(Path file) {
        //read file
        Entity entity = null;
        try {
            entity = XMLParser.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        //save file
        DAOService.persist(entity);
        //delete file

    }

}
