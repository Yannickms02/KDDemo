package com.example.kdtreedemo;

public class QuickSort {

    public static void quickSort(double[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(double[] array, int start, int end) {
        if (start >= end) {
            return;
        }

        int pivotIndex = partition(array, start, end);
        quickSort(array, start, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, end);
    }

    private static int partition(double[] array, int start, int end) {
        double pivot = array[end];
        int i = start;

        for (int j = start; j < end; j++) {
            if (array[j] <= pivot) {
                swap(array, i, j);
                i++;
            }
        }

        swap(array, i, end);
        return i;
    }

    private static void swap(double[] array, int i, int j) {
        double temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
