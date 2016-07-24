package ru.nekki.test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class FileVisitor extends SimpleFileVisitor<Path> {
    private final Path outputDir;

    FileVisitor(Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            FileProcessor.process(file, outputDir);
        }
        return super.visitFile(file, attrs);
    }
}