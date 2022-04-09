package ru.yarsu.molab;

public class Matrix {
    private Fraction[][] matrix;
    private int rows;
    private int cols;

    public Matrix(Fraction[][] matrix, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new Fraction[rows][cols];

        //copy fractions
        for (int i =0; i < rows; i++)
            for (int j =0; j < cols; j++)
                this.matrix[i][j] = new Fraction(matrix[i][j]);
    }
    public void changeCols(int src, int dest) {
        for (int i =0; i < rows; i++) {
            Fraction tmp = matrix[i][src];
            matrix[i][src] = matrix[i][dest];
            matrix[i][dest] = tmp;
        }
    }
    public void changeRows(int src, int dest) {
        for (int j =0; j < cols; j++) {
            Fraction tmp = matrix[dest][j];
            matrix[dest][j] = matrix[src][j];
            matrix[src][j] = tmp;
        }
    }
    public void multiplyRow(Fraction[] row, Fraction multiplier) {
        for (int j=0; j<cols; j++) {
            row[j] = row[j].multiply(multiplier);
        }
    }
    public void multiplyCol(int col, Fraction multiplier) {

    }
    public Fraction[] rowCopy(int row) {
        Fraction[] res = new Fraction[cols];
        for (int j = 0; j < cols; j++) {
            res[j] = new Fraction(matrix[row][j]);
        }
        return res;
    }
    public void addRow(Fraction[] src, Fraction[] row) {
        for (int j=0; j<cols; j++) {
            src[j] = src[j].add(row[j]);
        }
    }
    public Fraction[] getRow(int row) {
        return matrix[row];
    }
    public Fraction getElement(int i, int j) {
        return matrix[i][j];
    }
    public void print() {
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                System.out.print(matrix[i][j] + " ");

            }
            System.out.println();
        }
    }
}
