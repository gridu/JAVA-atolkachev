package com.griddynamics.external_sorting;

import com.griddynamics.external_sorting.exception.SortingException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.nio.file.Files.lines;

public class FileWrapper<T extends Comparable<T>> implements Comparable<FileWrapper<T>> {

    private final Deserializer<T> deserializer;
    private final Iterator<String> lines;
    private T saved;

    FileWrapper(File file, Deserializer<T> deserializer) {
        this.deserializer = deserializer;
        try {
            this.lines = lines(file.toPath()).iterator();
        } catch (IOException e) {
            throw new SortingException(
                    "File not found! Please check a file by " + file.getAbsolutePath() + " path", e.getCause());
        }
    }

    @Override
    public int compareTo(FileWrapper<T> that) {
        T current = peek();
        T next = that.peek();

        if (current == null) return next == null ? 0 : -1;
        if (next == null) return 1;
        return current.compareTo(next);
    }

    T pop() {
        T tmp = peek();

        if (tmp != null) {
            saved = null;
            return tmp;
        }
        throw new NoSuchElementException();
    }

    boolean isEmpty() {
        return peek() == null;
    }

    private T peek() {
        if (saved != null) {
            return saved;
        }
        if (!lines.hasNext()) {
            return null;
        }
        return saved = deserializer.deserialize(lines.next(), null);
    }
}
