package ru.nekki.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("=================================START=================================\n");
        logger.debug(Arrays.toString(args));
        if (args.length != 2) {
            logger.error(
                    "Please specify input/output folders as parameters of the start script e.g.:\n" +
                            "-------------\n" +
                            ">nekki-test.bat c:/input c:/output \n" +
                            "-------------\n" +
                            " The program will exit now.");
            throw new RuntimeException();
        }

        Path inputDir = Paths.get(args[0]);
        logger.debug("input folder: " + inputDir);
        Path outputDir = Paths.get(args[1]);
        logger.debug("output folder: " + outputDir);
        checkFolders(inputDir, outputDir);
        processNewFiles(inputDir, outputDir);
        processInitialFiles(inputDir, outputDir);
    }

    private static void checkFolders(Path... dirs) {
        for (Path dir : dirs) {
            if (Files.notExists(dir) || !Files.isDirectory(dir)) {
                logger.error(
                        String.format("The folder %s does not exist, is not a directory or is forbidden to be read", dir));
                throw new RuntimeException();
            }
        }
        logger.debug(Arrays.toString(dirs) + " are checked OK");
    }

    private static void processNewFiles(final Path inputDir, final Path outputDir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileSystemUtil.waitForNewFilesAndProcess(inputDir, outputDir);
            }
        }).start();
        logger.printf(Level.DEBUG, "Monitoring %s for new files", inputDir);
    }

    private static void processInitialFiles(Path inputDir, Path outputDir) {
        try {
            Files.walkFileTree(inputDir, new FileVisitor(outputDir));
        } catch (IOException e) {
            logger.error("An error while walking through " + inputDir, e);
        }
    }

}
