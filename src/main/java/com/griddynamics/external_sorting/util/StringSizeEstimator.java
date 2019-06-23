package com.griddynamics.external_sorting.util;

public final class StringSizeEstimator {

    private static int OBJ_OVERHEAD;

    // By default we use 64 bit JVM
    private static boolean IS_64_BIT_JVM = true;

    private StringSizeEstimator() {
    }

    static {

        // It is necessary to be sure that we use a correct JVM
        String arch = System.getProperty("sun.arch.data.model");
        if (arch != null) {
            if (arch.contains("32")) {
                // If the property contains '32', we assume that we use 32 bit JVM
                IS_64_BIT_JVM = false;
            }
        }

        // The sizes help to avoid an out of memory error
        // The object header consists of a mark word and a class pointer
        // 64 bit (8 bytes mark word + class pointer 8 bytes)
        // 32 bit (4 bytes mark word + class pointer 4 bytes)
        int OBJ_HEADER = IS_64_BIT_JVM ? 16 : 8;
        // https://www.javamex.com/tutorials/memory/array_memory_usage.shtml
        int ARR_HEADER = IS_64_BIT_JVM ? 24 : 12;
        // https://stackoverflow.com/questions/37416073/where-jvm-keeps-information-about-reference-and-object-types
        int OBJ_REF = IS_64_BIT_JVM ? 8 : 4;
        // https://stackoverflow.com/questions/11012302/learn-about-object-overhead-in-jvm
        int INT_FIELDS = 12;
        OBJ_OVERHEAD = OBJ_HEADER + INT_FIELDS + OBJ_REF + ARR_HEADER;
    }

    /**
     * Estimates the size of a {@link String} object in bytes.
     * @param s The string to estimate memory footprint.
     * @return The estimated size in bytes
     */
    public static long estimatedSizeOf(String s) {
        // char takes 2 bytes in memory
        // we should estimate how many bytes a given string takes
        // for that we multiple a string length by 2
        return (s.length() * 2) + OBJ_OVERHEAD;
    }
}
