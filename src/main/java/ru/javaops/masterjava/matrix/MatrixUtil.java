package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {

        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int threadCount = ((ThreadPoolExecutor) executor).getCorePoolSize();
        final int area = matrixA.length / threadCount;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int begin = i * area;
            int end = ((i + 1) * area);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                final int aColumns = matrixA.length;
                final int aRows = matrixA[0].length;
                final int bColumns = matrixB.length;
                final int bRows = matrixB[0].length;
                int thatColumn[] = new int[bRows];

                try {
                    for (int j = begin;j<end ; j++) {
                        for (int k = 0; k < aColumns; k++) {
                            thatColumn[k] = matrixB[k][j];
                        }
                        calc(aColumns, aRows, thatColumn, j, matrixA, matrixC);
                    }
                } catch (IndexOutOfBoundsException ignored) { }
                finally {
                    latch.countDown();}
            }
        });}
        try {
            latch.await();
        } catch (InterruptedException e) {//handle
            }
        return matrixC;
    }

    private static void calc(int aColumns, int aRows, int[] thatColumn, int j, int[][] matrixA, int[][] matrixC) {
        for (int i = 0; i < aRows; i++) {
            int thisRow[] = matrixA[i];
            int summand = 0;
            for (int k = 0; k < aColumns; k++) {
                summand += thisRow[k] * thatColumn[k];
            }
            matrixC[i][j] = summand;
        }
    }

    public static int[][] singleThreadMultiplyUnoptimized(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;}

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int aColumns = matrixA.length;
        final int aRows = matrixA[0].length;
        final int bColumns = matrixB.length;
        final int bRows = matrixB[0].length;

        int thatColumn[] = new int[bRows];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < aColumns; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                calc(aColumns, aRows, thatColumn, j, matrixA, matrixC);
            }
        } catch (IndexOutOfBoundsException ignored) { }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
