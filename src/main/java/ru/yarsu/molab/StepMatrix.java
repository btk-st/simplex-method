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

    public void makeDiagonal() {
        //todo need to check minor before doing!!!
        for (int j = 0; j < rows; j++) {
            for (int i =0; i < rows; i++) {
                //if element on main diagonal or is zero
                if (i == j || matrix.getElement(i,j).getNumerator() == 0) continue;
                Fraction multiplier = matrix.getElement(i,j).divide(matrix.getElement(j,j));
                Fraction[] rowToSubtract =  matrix.rowCopy(j);
                matrix.multiplyRow(rowToSubtract, multiplier);
                matrix.multiplyRow(rowToSubtract, new Fraction(-1,1));
                matrix.addRow(matrix.getRow(i), rowToSubtract);
            }
            //divide origin row to get coef 1
            matrix.multiplyRow(matrix.getRow(j), new Fraction(1,1).divide(matrix.getElement(j,j)));
        }
    }
}
