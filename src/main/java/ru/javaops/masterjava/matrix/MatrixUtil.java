package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {
    static class MultiplierThread extends Thread
    {
        /** Первая (левая) матрица. */
        private final int[][] firstMatrix;
        /** Вторая (правая) матрица. */
        private final int[][] secondMatrix;
        /** Результирующая матрица. */
        private final int[][] resultMatrix;
        /** Начальный индекс. */
        private final int firstIndex;
        /** Конечный индекс. */
        private final int lastIndex;
        /** Число членов суммы при вычислении значения ячейки. */
        private final int sumLength;

        /**
         * @param firstMatrix  Первая (левая) матрица.
         * @param secondMatrix Вторая (правая) матрица.
         * @param resultMatrix Результирующая матрица.
         * @param firstIndex   Начальный индекс (ячейка с этим индексом вычисляется).
         * @param lastIndex    Конечный индекс (ячейка с этим индексом не вычисляется).
         */
        public MultiplierThread(final int[][] firstMatrix,
                                final int[][] secondMatrix,
                                final int[][] resultMatrix,
                                final int firstIndex,
                                final int lastIndex)
        {
            this.firstMatrix  = firstMatrix;
            this.secondMatrix = secondMatrix;
            this.resultMatrix = resultMatrix;
            this.firstIndex   = firstIndex;
            this.lastIndex    = lastIndex;

            sumLength = secondMatrix.length;
        }

        /**Вычисление значения в одной ячейке.
         *
         * @param row Номер строки ячейки.
         * @param col Номер столбца ячейки.
         */
        private void calcValue(final int row, final int col)
        {
            int sum = 0;
            for (int i = 0; i < sumLength; ++i)
                sum += firstMatrix[row][i] * secondMatrix[i][col];
            resultMatrix[row][col] = sum;
        }

        /** Рабочая функция потока. */
        @Override
        public void run()
        {
           // System.out.println("Thread " + getName() + " started. Calculating cells from " + firstIndex + " to " + lastIndex + "...");

            final int colCount = secondMatrix[0].length;  // Число столбцов результирующей матрицы.
            for (int index = firstIndex; index < lastIndex; ++index)
                calcValue(index / colCount, index % colCount);

           // System.out.println("Thread " + getName() + " finished.");
        }
    }

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] firstMatrix, int[][] secondMatrix, ExecutorService executor) throws InterruptedException, ExecutionException {
        int threadCount = 10;

        final int rowCount = firstMatrix.length;             // Число строк результирующей матрицы.
        final int colCount = secondMatrix[0].length;         // Число столбцов результирующей матрицы.
        final int[][] result = new int[rowCount][colCount];  // Результирующая матрица.

        final int cellsForThread = (rowCount * colCount) / threadCount;  // Число вычисляемых ячеек на поток.
        int firstIndex = 0;  // Индекс первой вычисляемой ячейки.
        final MultiplierThread[] multiplierThreads = new MultiplierThread[threadCount];  // Массив потоков.

        // Создание и запуск потоков.
        for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {
            int lastIndex = firstIndex + cellsForThread;  // Индекс последней вычисляемой ячейки.
            if (threadIndex == 0) {
                /* Один из потоков должен будет вычислить не только свой блок ячеек,
                   но и остаток, если число ячеек не делится нацело на число потоков. */
                lastIndex = rowCount * colCount;
            }
            multiplierThreads[threadIndex] = new MultiplierThread(firstMatrix, secondMatrix, result, firstIndex, lastIndex);
            multiplierThreads[threadIndex].start();
            firstIndex = lastIndex;
        }

        // Ожидание завершения потоков.
        try {
            for (final MultiplierThread multiplierThread : multiplierThreads)
                multiplierThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
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
