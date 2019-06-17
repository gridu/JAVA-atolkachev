package com.griddynamics.mergesort.util;

import com.griddynamics.mergesort.exception.IncorrectFileNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllLines;

/**
 * Provides files data reading
 */
public class FileToArrayProvider {


    /**
     * Reads a file, separates the file's data by lines and returns an array
     * @param fileName a file that is need to read
     * @return a String array
     * @throws IOException
     * @throws IncorrectFileNameException
     */
    public static String[] readLines(final File fileName) throws IOException, IncorrectFileNameException {
        try {
            var lines = readAllLines(Paths.get(fileName.getAbsolutePath()), defaultCharset());
            return lines.toArray(new String[]{});
        } catch (NoSuchFileException e) {
                throw new IncorrectFileNameException("Incorrect file path: " + fileName, e);
        }
    }
}
