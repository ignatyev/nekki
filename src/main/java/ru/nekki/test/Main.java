package ru.nekki.test;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by AnVIgnatev on 20.07.2016.
 */
public class Main {
//    Logger logger = Logger.getLogger(getClass().getName());

    //TODO check if exists and folder and for cyclics etc
    private static WatchService registerWatcher(Path dir) {
        WatchService watcher;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY); //TODO use result??
            return watcher;
        } catch (IOException x) {
            //TODO log
            throw new RuntimeException(x); //TODO wait until folder exists
        }
    }

    public static void main(String[] args) {
//TODO check input
        Path dir = Paths.get("c:/test"); //TODO move to params
        WatchService watcher = registerWatcher(dir);

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

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                // Verify that the new
                //  file is a text file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    Path child = dir.resolve(filename);
                    if (!Files.probeContentType(child).equals("text/plain")) {
                        System.err.format("New file '%s'" +
                                " is not a plain text file.%n", filename);
                        continue;
                    }
                } catch (IOException x) {
                    System.err.println(x);
                    continue;
                }

                // Email the file to the
                //  specified email alias.
                System.out.format("Emailing file %s%n", filename);
                //Details left to reader....
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
}
