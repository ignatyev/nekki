package ru.nekki.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Main {
    private final static Logger logger = LogManager.getLogger(Main.class);
    public static final String CONFIG_PROPERTIES = "config.properties";
    public static final String INPUT_PROPERTY = "input";
    public static final String OUTPUT_PROPERTY = "output";

    public static void main(String[] args) {
        Properties properties = loadProperties();
        Path inputDir = Paths.get(properties.getProperty(INPUT_PROPERTY));
        Path processedDir = Paths.get(properties.getProperty(OUTPUT_PROPERTY));
        checkFolders(inputDir, processedDir);
        processNewFiles(inputDir, processedDir);
        processInitialFiles(inputDir, processedDir);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            logger.error("Could not read property file " + CONFIG_PROPERTIES);
            throw new RuntimeException();
        }
        return properties;
    }

    private static void checkFolders(Path... dirs) {
        for (Path dir : dirs) {
            if (Files.notExists(dir) || !Files.isDirectory(dir) || !Files.isReadable(dir)) {
                //TODO cycle here?
                logger.error(
                        String.format("The folder %s does not exist, is not a directory or is forbidden to be read", dir));
                throw new RuntimeException();
            }
        }
    }

    private static void processInitialFiles(Path inputDir, final Path processedDir) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (final File file : inputDir.toFile().listFiles(new RegularFileFilter())) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    FileProcessor.process(file.toPath(), processedDir);
                }
            });
        }
    }

    public static void processNewFiles(final Path dir, final Path processedFolder) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        FileSystemUtil.waitForNewFilesAndProcess(dir, processedFolder);
                    }
                }
        ).start();

    }

    private static class RegularFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile();
        }
    }
}
