package com.griddynamics.external_sorting;

import com.griddynamics.external_sorting.exception.SortingException;
import com.griddynamics.external_sorting.util.TimeMetric;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static com.griddynamics.external_sorting.util.MemoryEstimator.*;
import static com.griddynamics.external_sorting.util.StringSizeEstimator.estimatedSizeOf;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Collections.sort;

public class ExternalSort {

    private ParallelMergeSort parallelMergeSort;
    private boolean isParallel;

    ExternalSort() {
        parallelMergeSort = new ParallelMergeSort(measureAvailableCoresNumber());
    }

    public ExternalSort(boolean isParallel) {
        this();
        this.isParallel = isParallel;
    }

    public List<File> sortAndDivide(final File file) throws IOException {
        TimeMetric timer = new TimeMetric("Sort and divide a file");
        List<File> filesToReturn = new ArrayList<>();
        long bufferSize = measureBlockSize(file.length(), measureAvailableMemory());

        try (Stream<String> streamLines = lines(file.toPath(), defaultCharset())) {
            List<String> tmpList = new ArrayList<>();
            String line;
            try {
                var iterator = streamLines.iterator();
                while (iterator.hasNext()) {
                    long recordedDataSize = 0;
                    while ((recordedDataSize < bufferSize && (iterator.hasNext()))) {
                        line = iterator.next();
                        tmpList.add(line);
                        recordedDataSize += estimatedSizeOf(line);
                    }
                    filesToReturn.add(sortAndWrite(tmpList));
                    tmpList.clear();
                }
            } finally {
                streamLines.close();
            }
        } catch (NoSuchFileException e) {
            throw new SortingException("\nFile not found by path: " + file.getAbsolutePath(), e.getCause());
        }
        timer.print();
        return filesToReturn;
    }

    public File sortAndWrite(List<String> fragments) throws IOException {
        File tempFile = createTempFile("fragmentFile", ".tmp").toFile();
        tempFile.deleteOnExit();
        if (!isParallel()) {
            sort(fragments);
        } else {
            fragments = parallelMergeSort.sort(fragments.toArray(String[]::new));
        }
        writeDataFromList(tempFile, fragments);
        return tempFile;
    }

    private void writeDataFromList(final File file, List<String> fragments) throws IOException {
        try (var writer = newBufferedWriter(file.toPath(), defaultCharset())) {
            for (String line : fragments) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void mergeSortedFiles(final List<File> files, File outputFile) {
        TimeMetric timer = new TimeMetric("Merging sorted files to the output file");
        try (var writer = newBufferedWriter(outputFile.toPath(), TRUNCATE_EXISTING)) {
            Iterator<String> iterator = new FileMerger<>(files, (line, arr) -> line).getElements();
            while (iterator.hasNext()) {
                writer.write(iterator.next());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.print();
        removeTempFiles(files);
    }

    private void removeTempFiles(List<File> tempFiles) {
        tempFiles.forEach(File::delete);
    }

    public ParallelMergeSort getParallelMergeSort() {
        return parallelMergeSort;
    }

    public void setParallelMergeSort(ParallelMergeSort parallelMergeSort) {
        this.parallelMergeSort = parallelMergeSort;
    }

    public boolean isParallel() {
        return this.isParallel;
    }

    public void setParallel(boolean parallel) {
        this.isParallel = parallel;
    }
}
