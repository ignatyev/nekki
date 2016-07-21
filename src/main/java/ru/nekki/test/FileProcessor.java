package ru.nekki.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import ru.nekki.test.dao.DAOService;
import ru.nekki.test.dao.Entry;
import ru.nekki.test.xml.XMLParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class FileProcessor {
    private final static Logger logger =
            LogManager.getLogger(FileProcessor.class);

    public static void process(Path file, Path processedFolder) {
        Entry entry = readFile(file);
        if (entry == null) return;
        DAOService.save(entry);
        moveFile(file, processedFolder);
    }

    private static Entry readFile(Path file) {
        Entry entry = null;
        try {
            entry = XMLParser.parse(file);
            logger.info(file + " is successfully parsed.");
        } catch (IOException | ParserConfigurationException e) {
            logger.error("Error while parsing " + file, e);
        } catch (SAXException e) {
            logger.info(String.format("File %s could not be parsed as a valid xml", file));
        }
        return entry;
    }

    private static void moveFile(Path file, Path processedFolder) {
        try {
            Path resultPath = Files.move(file, processedFolder.resolve(generateFileName(file)),
                    StandardCopyOption.ATOMIC_MOVE);
            logger.info(String.format("%s has been successfully processed and moved to %s", file, resultPath));
        } catch (IOException e) {
            logger.error(String.format("Error while moving %s to %s", file, processedFolder), e);
            //TODO clear DB entry?
        }
    }

    private static String generateFileName(Path file) {
        return UUID.randomUUID().toString() +
                " - processed - " +
                file.getFileName().toString();
    }

}
