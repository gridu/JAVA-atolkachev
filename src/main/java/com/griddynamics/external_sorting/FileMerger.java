package com.griddynamics.external_sorting;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import static java.util.stream.Collectors.toCollection;

class FileMerger<T extends Comparable<T>> {

    private final PriorityQueue<FileWrapper<T>> files;

    FileMerger(List<File> files, Deserializer<T> deserializer) {
        this.files = files.stream()
                .map(file -> new FileWrapper<>(file, deserializer))
                .filter(comparableFile -> !(comparableFile).isEmpty())
                .collect(toCollection(PriorityQueue::new));
    }

    Iterator<T> getElements() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !files.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                FileWrapper<T> head = files.poll();
                T next = null;
                if (head != null) {
                    next = head.pop();
                }
                if (head != null && !head.isEmpty()) {
                    files.add(head);
                }
                return next;
            }
        };
    }
}
