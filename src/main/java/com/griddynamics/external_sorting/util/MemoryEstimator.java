package com.griddynamics.external_sorting.util;

import com.griddynamics.external_sorting.exception.SortingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.lang.Runtime.getRuntime;

public final class MemoryEstimator {

    /**
     * Estimates and returns size of available RAM memory
     *
     * @return the estimated value
     */
    public static long measureAvailableMemory() {
        System.gc();
        Runtime r = getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        return r.maxMemory() - allocatedMemory;
    }

    /**
     * Estimates and returns numbers of available CPU cores
     *
     * @return the estimated value
     */
    public static int measureAvailableCoresNumber() {
        Runtime r = getRuntime();
        return r.availableProcessors();
    }

    /**
     * Estimates and returns size of available disk space
     *
     * @return the estimated value
     */
    public static long measureAvailableDiskSpace() {
        return new File("").getTotalSpace();
    }

    /**
     * Gets a file size
     *
     * @param file - original file
     * @return size of the original file
     * @throws SortingException generate an exception
     */
    public static long getFileSize(final File file) {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            throw new SortingException("File " + file.getName() + "not found!", e.getCause());
        }
    }

    /**
     * Divides data from a file into small blocks. It helps to avoid an over of memory error.
     *
     * @param fileSize  which size has a file
     * @param maxMemory the size of available memory
     * @return the estimated value
     */
    public static long measureBlockSize(final long fileSize, final long maxMemory) {
        long blockSize = fileSize / 1024;

        if (blockSize < (maxMemory / 2)) {
            blockSize = maxMemory / 2;
        }
        return blockSize;
    }
}
