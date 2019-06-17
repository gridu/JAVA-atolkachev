package com.griddynamics.mergesort.util;

import static java.lang.Runtime.*;

public final class MemoryEstimator {

    public static long estimateAvailableMemory() {
        System.gc();
        Runtime r = getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        return r.maxMemory() - allocatedMemory;
    }

}
