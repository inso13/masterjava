package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {

        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int threadCount = 10;
        class MultiplyArrays extends Thread {

            private int[][] m1, m2;
            private int begin, end;


            public MultiplyArrays(int[][] m1, int[][] m2, int begin, int end) {
                this.m1 = m1;
                this.m2 = m2;
                this.begin = begin;
                this.end = end;
            }

            public void run() {
                /*for (int i = begin; i < end; i++) {
                    for (int j = 0; j < m2[0].length; j++) {
                        int sum = 0;
                        for (int r = 0; r < m2.length; r++) {
                            sum = sum + m1[i][r] * m2[r][j];
                        }
                        matrixC[i][j] = sum;
                    }
                }*/
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

                        for (int i = 0; i < aRows; i++) {
                            int thisRow[] = matrixA[i];
                            int summand = 0;
                            for (int k = 0; k < aColumns; k++) {
                                summand += thisRow[k] * thatColumn[k];
                            }
                            matrixC[i][j] = summand;
                        }
                    }
                } catch (IndexOutOfBoundsException ignored) { }
            }
        }
        int area = matrixA.length / threadCount;
        MultiplyArrays[] multiplyArrayses = new MultiplyArrays[threadCount];
        for (int i = 0; i < threadCount; i++) {
            multiplyArrayses[i] = new MultiplyArrays(matrixA,matrixB, i * area, (i + 1) * area);
            multiplyArrayses[i].start();
        }

        for (MultiplyArrays multiplyArrays : multiplyArrayses) {
            multiplyArrays.join();
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
/*
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;*/
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

                for (int i = 0; i < aRows; i++) {
                    int thisRow[] = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < aColumns; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = summand;
                }
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
