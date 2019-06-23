package com.griddynamics.external_sorting;

import com.griddynamics.external_sorting.exception.SortingException;
import com.griddynamics.external_sorting.util.MemoryEstimator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.util.Objects.nonNull;

public class AppCustomSorting {

    public static void main(String[] args) throws IOException {
        String inputFile = null;
        String outputFile = null;
        boolean parallel = false;
        boolean detailed = false;
        for (String arg : args) {
            if ("-d".equals(arg)) {
                detailed = true;
            } else if ("-h".equals(arg)) {
                displayInfo();
                return;
            } else if ("-p".equals(arg)) {
                parallel = true;
            } else {
                if (!nonNull(inputFile)) {
                    inputFile = arg;
                } else if (!nonNull(outputFile)) {
                    outputFile = arg;
                } else {
                    System.out.println("Unknown parameter: " + arg);
                }
            }
        }

        if (!nonNull(outputFile)) {
            System.err.println("\n========================");
            System.err.println("        WARNING!");
            System.err.println("========================");
            System.err.println(" PROVIDE AN OUTPUT FILE!");
            System.out.println();
            System.out.println();
            displayInfo();
            return;
        }
        diskSpaceInfo(Path.of(inputFile).toFile());
        System.out.println("\nSTART SORTING DATA FROM THE FILE: " + inputFile);
        System.out.println();
        ExternalSort externalSort = new ExternalSort(parallel);
        List<File> fileFragments = externalSort.sortAndDivide(Path.of(inputFile).toFile());
        if (detailed) {
            System.out.println("\n" + fileFragments.size() + " temp file(s) created");
        }
        externalSort.mergeSortedFiles(fileFragments, new File(outputFile));
        System.out.println("\nSORTING IS DONE! \nPlease check a result by path: " + outputFile);
    }

    private static void displayInfo() {
        System.out.println("\ncom.griddynamics.external_sorting.AppCustomSorting <inputfilePath> <outputfilePath>");
        System.out.println();
        System.out.println("Parameters are:");
        System.out.println();
        System.out.println("-h: display this message");
        System.out.println("-d: detailed info");
        System.out.println("-p: to use a parallel sorting");
    }

    private static void diskSpaceInfo(final File file) {
        try {
            if (MemoryEstimator.measureAvailableDiskSpace() < MemoryEstimator.getFileSize(file)) {
                throw new SortingException(
                        "\n========================" +
                                "\nNOT ENOUGH FREE DISK SPACE!" +
                                "\nPlease free the disc space and re-run the application." +
                                "\n========================", null);
            }
        } catch (SortingException e) {
            e.getMessage();
        }
    }
}
