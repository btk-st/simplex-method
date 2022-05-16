package ru.yarsu.molab;

public class Matrix {
    private Fraction[][] matrix;
    private int rows;
    private int cols;

    public Matrix(Fraction[][] matrix, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new Fraction[rows][cols];

        //копируем значения
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
    private Fraction cofactor(int i, int j) {
        int n = matrix.length;
        Fraction[][] newMatr = new Fraction[n-1][n-1];
        int realI = 0, realJ = 0;
        for (int i1 = 0; i1 < n; i1++) {
            if (i == i1) continue;
            realJ = 0;
            for (int j1 = 0; j1 < n; j1++) {
                if (j == j1) continue;
                newMatr[realI][realJ++] = matrix[i1][j1];
            }
            realI++;
        }
        Matrix minorMatrix = new Matrix(newMatr, n-1, n-1);
        Fraction det = minorMatrix.calcDet();
        if ((i+j)%2 == 1) det = det.multiply(new Fraction(-1,1));
        return det;
    }
    public Fraction calcDet() {
        //n - число строк
        int n = matrix.length;
        Fraction det = new Fraction(0,1);
        if (n == 1) return matrix[0][0];
        //вычисляем по первой строке
        for (int j = 0; j < n; j++) {
            det = det.add(matrix[0][j].multiply(cofactor(0, j)));
        }
        return det;
    }
    public void makeDiagonal() {
        for (int j = 0; j < rows; j++) {
            for (int i =0; i < rows; i++) {
                //элемент на главной диагонали ИЛИ равен 0
                if (i == j || matrix[i][j].getNumerator() == 0) continue;
                Fraction multiplier = matrix[i][j].divide(matrix[j][j]);
                Fraction[] rowToSubtract =  this.rowCopy(j);
                this.multiplyRow(rowToSubtract, multiplier);
                this.multiplyRow(rowToSubtract, new Fraction(-1,1));
                this.addRow(this.getRow(i), rowToSubtract);
            }
            //делим строку, чтобы получить 1
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
    public void deleteCol(int col) {
        Fraction[][] newMatr = new Fraction[rows][cols-1];
        cols--;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                newMatr[i][j] = matrix[i][j];
            }
            for (int j = col; j < cols; j++) {
                newMatr[i][j] = matrix[i][j+1];
            }
        }
        matrix = newMatr;
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

    public boolean zeroOnMainDiagonal() {
        int n = matrix.length;
        for (int i =0; i < n; i++) {
            if (matrix[i][i].getNumerator() == 0) return true;
        }
        return false;
    }
}
