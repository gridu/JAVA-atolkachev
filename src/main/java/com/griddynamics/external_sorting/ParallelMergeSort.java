package com.griddynamics.external_sorting;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort {

    private final ForkJoinPool pool;

    public static class MergeSortTask extends RecursiveAction {

        private static final int MAX_ARRAY_SIZE = 8192;
        private final String[] array;
        private final int minIndex;
        private final int maxIndex;

        MergeSortTask(final String[] array, int minIndex, int maxIndex) {
            this.array = array;
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        void merge(final int middleIndex) {
            if (array[middleIndex - 1].compareTo(array[middleIndex]) <= 0) {
                return;
            }

            String[] copy = new String[maxIndex - minIndex];
            System.arraycopy(array, minIndex, copy, 0, copy.length);

            int copyMin = 0;
            int copyMax = maxIndex - minIndex;
            int copyMiddle = middleIndex - minIndex;

            for (int min = minIndex, cmin = copyMin, cmid = copyMiddle; min < maxIndex; min++) {
                if (cmid >= copyMax || (cmin < copyMiddle && copy[cmin].compareTo(copy[cmid]) <= 0)) {
                    array[min] = copy[cmin++];
                } else {
                    array[min] = copy[cmid++];
                }
            }
        }

        @Override
        protected void compute() {
            if (maxIndex - minIndex <= MAX_ARRAY_SIZE) {
                Arrays.sort(array, minIndex, maxIndex);
            } else {
                int middle = minIndex + ((maxIndex - minIndex) / 2);
                final MergeSortTask left = new MergeSortTask(array, minIndex, middle);
                final MergeSortTask right = new MergeSortTask(array, middle, maxIndex);
                invokeAll(left, right);
                merge(middle);
            }
        }
    }

    ParallelMergeSort(int parallelism) {
        pool = new ForkJoinPool(parallelism);
    }

    List<String> sort(final String[] arrayForSorting) {
        ForkJoinTask<Void> job = pool.submit(new MergeSortTask(arrayForSorting, 0, arrayForSorting.length));
        job.join();
        return List.of(arrayForSorting);
    }
}
