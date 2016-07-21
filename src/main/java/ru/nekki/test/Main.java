package ru.nekki.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Main {
    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
//TODO check input
        //TODO move to params
        Path inputDir = Paths.get("C:\\Users\\AnVIgnatev\\Documents\\TEMP");
        Path processedDir = Paths.get("C:\\Users\\AnVIgnatev\\Documents\\TEMP\\processed");
        checkFolders(inputDir, processedDir);
        processNewFiles(inputDir, processedDir);
        processInitialFiles(inputDir, processedDir);
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
