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
    public Matrix(Matrix that) {
        this(that.getMatrix(), that.getRows(), that.getCols());
    }
    public void changeCols(int src, int dest) {
        for (int i =0; i < rows; i++) {
            Fraction tmp = matrix[i][src];
            matrix[i][src] = matrix[i][dest];
            matrix[i][dest] = tmp;
        }
    }

    public Fraction[][] getMatrix() {
        return matrix;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void makeDiagonal() {
        //todo need to check minor before doing!!!
        for (int j = 0; j < rows; j++) {
            for (int i =0; i < rows; i++) {
                //if element on main diagonal or is zero
                if (i == j || matrix[i][j].getNumerator() == 0) continue;
                Fraction multiplier = matrix[i][j].divide(matrix[j][j]);
                Fraction[] rowToSubtract =  this.rowCopy(j);
                this.multiplyRow(rowToSubtract, multiplier);
                this.multiplyRow(rowToSubtract, new Fraction(-1,1));
                this.addRow(this.getRow(i), rowToSubtract);
            }
            //divide origin row to get coef 1
            this.multiplyRow(this.getRow(j), new Fraction(1,1).divide(matrix[j][j]));
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
    public void multiplyColumn(int col, Fraction multiplier) {
        for (int i = 0; i < rows; i++) {
            matrix[i][col] = matrix[i][col].multiply(multiplier);
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
    public void setElement(int i, int j, Fraction f) {
        matrix[i][j] = f;
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
