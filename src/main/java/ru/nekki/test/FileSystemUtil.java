package ru.nekki.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
class FileSystemUtil {
    private final static Logger logger =
            LogManager.getLogger(FileSystemUtil.class);


    static void waitForNewFilesAndProcess(Path inputDir, Path outputDir) {
        WatchService watcher = registerWatcher(inputDir);
        for (; ; ) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == OVERFLOW) {
                    continue;
                }
                if (kind == ENTRY_CREATE) {
                    // The filename is the
                    // context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    Path child = inputDir.resolve(filename);
                    try {
                        Files.walkFileTree(child, new FileVisitor(outputDir));
                    } catch (IOException e) {
                        logger.error("Error while reading a newly added file " + child, e);
                    }
                }
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    //TODO check if exists and folder and for cyclics etc
    private static WatchService registerWatcher(Path dir) {
        WatchService watcher;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, ENTRY_CREATE);
            return watcher;
        } catch (IOException x) {
            logger.error(
                    String.format("Could not register watcher for folder %s. Is the folder available?", dir));
            throw new RuntimeException(x);
        }
    }
}
