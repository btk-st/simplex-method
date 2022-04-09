package ru.yarsu.molab;

import javafx.scene.layout.GridPane;

public class StepMatrix {
    private Matrix matrix;
    private int rows;
    private int cols;
    private int[] oY;
    private int[] oX;

    public StepMatrix(Matrix matrix, int[] oX,int[] oY) {
        this.rows = oY.length;
        this.cols = oX.length;
        this.oY = new int[rows];
        this.oX = new int[cols];
        this.matrix = matrix;
        //columns order
        System.arraycopy(oY, 0, this.oY, 0, rows);
        System.arraycopy(oX, 0, this.oX, 0, cols);
    }

    public void swapColumns(int col1, int col2) {
        matrix.changeCols(col1, col2);
        int tmp = oX[col1];
        oX[col1] = oX[col2];
        oX[col2] = tmp;
    }
    public void print() {
        matrix.print();
        System.out.println("oX");
        for (int i = 0; i < oX.length; i++)
            System.out.print(oX[i] + " ");
        System.out.println();
        System.out.println("oY");
        for (int i = 0; i < oY.length; i++)
            System.out.println(oY[i]);
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int[] getoY() {
        return oY;
    }

    public void setoY(int[] oY) {
        this.oY = oY;
    }

    public int[] getoX() {
        return oX;
    }

    public void setoX(int[] oX) {
        this.oX = oX;
    }
}
