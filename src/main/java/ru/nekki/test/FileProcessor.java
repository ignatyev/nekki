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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.nekki.test.dao.STATUS.STARTED;
import static ru.nekki.test.dao.STATUS.SUCCESSFUL;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
class FileProcessor {
    private final static Logger logger =
            LogManager.getLogger(FileProcessor.class);
    private final static ExecutorService threadPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //only used as a lock so that one file is processed exactly once
    private final static ConcurrentHashMap<String, Boolean> filesInProcess = new ConcurrentHashMap<>();

    static void process(Path file, Path outputDir) throws IOException {
        String canonicalPath = file.toFile().getCanonicalPath();
        if (filesInProcess.putIfAbsent(canonicalPath, Boolean.TRUE) == null) {
            threadPool.submit(new ProcessFileCallable(file, outputDir));
        }
    }

    private static Entry readFile(Path file) {
        Entry entry = null;
        try {
            entry = XMLParser.parse(file);
            if (entry != null) {
                entry.setPath(file.toFile().getCanonicalPath());
            }
            logger.info(file + " has been successfully parsed.");
        } catch (IOException | ParserConfigurationException e) {
            logger.error("Error while reading/parsing " + file, e);
        } catch (SAXException e) {
            logger.info(String.format("File %s could not be parsed as a valid xml", file));
        }
        return entry;
    }

    private static void moveFile(Path file, Path outputDir) throws IOException {
        Path resultPath = Files.move(file, outputDir.resolve(generateFileName(file)),
                StandardCopyOption.ATOMIC_MOVE);
        logger.info(String.format("%s has been successfully processed and moved to %s", file, resultPath));
    }

    private static String generateFileName(Path file) {
        return UUID.randomUUID().toString() +
                " - processed - " +
                file.getFileName().toString();
    }

    private static class ProcessFileCallable implements Callable {
        private final Path file;
        private final Path outputDir;

        ProcessFileCallable(Path file, Path outputDir) {
            this.file = file;
            this.outputDir = outputDir;
        }

        @Override
        public Object call() throws IOException {
            try {
                Entry entry = readFile(file);
                if (entry == null) return null;
                entry.setStatus(STARTED);
                moveFile(file, outputDir);
                DAOService.save(entry);
                entry.setStatus(SUCCESSFUL);
                DAOService.save(entry);
            } finally {
                filesInProcess.remove(file.toFile().getCanonicalPath());
            }
            //returning null here just in order to return something.
            // Callable is used instead of Runnable in order to declare an exception
            return null;
        }
    }
}
