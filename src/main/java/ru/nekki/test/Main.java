package ru.nekki.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Main {
//    Logger logger = Logger.getLogger(getClass().getName());


    public static void main(String[] args) {
//TODO check input
        final Path dir = Paths.get("c:/test"); //TODO move to params
        if (Files.notExists(dir) || !Files.isDirectory(dir) || Files.isReadable(dir)) {
            //TODO cycle here?
            throw new RuntimeException(
                    String.format("The folder %s does not exist, is not a directory or is forbidden to be read", dir));
        }
        FileSystemUtil.processNewFiles(dir);
        processInitialFiles(dir);
    }

    private static void processInitialFiles(Path dir) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (final File file : dir.toFile().listFiles()) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Processor.process(file.toPath());
                }
            });
        }
    }
}
