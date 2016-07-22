package ru.nekki.test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;

public class FileVisitor extends SimpleFileVisitor<Path> {
    private final Path outputDir;
    private ExecutorService threadPool;

    public FileVisitor(Path outputDir, ExecutorService threadPool) {
        this.outputDir = outputDir;
        this.threadPool = threadPool;
    }

    @Override
    public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isRegularFile()) {
            return super.visitFile(file, attrs);
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                FileProcessor.process(file, outputDir);
            }
        });
        return super.visitFile(file, attrs);
    }
}