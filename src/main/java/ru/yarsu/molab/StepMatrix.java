package ru.yarsu.molab;

import java.util.ArrayList;

public class StepMatrix {
    private Matrix matrix;
    private ArrayList<PivotElement> pivotElements;
    private PivotElement selectedPivot = null;
    private int rows;
    private int cols;
    private int[] oY;
    private int[] oX;
    private String answer;

    public StepMatrix(Matrix matrix, int[] oX,int[] oY) {
        this.answer = "";
        this.rows = oY.length;
        this.cols = oX.length;
        this.oY = new int[rows];
        this.oX = new int[cols];
        this.matrix = new Matrix(matrix);
        pivotElements = new ArrayList<>();
        //columns order
        System.arraycopy(oY, 0, this.oY, 0, rows);
        System.arraycopy(oX, 0, this.oX, 0, cols);
    }
    public StepMatrix(StepMatrix that) {
        this(that.getMatrix(), that.getoX(), that.getoY());
    }
    public void deleteCol(int j) {
        //уменьшаем число столбцов
        cols--;
        //удаляем столбец в матрице
        matrix.deleteCol(j);
        //удаляем из oX
        int[] newoX = new int[cols];
        for (int i=0; i<j; i++) {
            newoX[i] = oX[i];
        }
        for (int i=j; i<cols; i++) {
            newoX[i] = oX[i+1];
        }
        oX = newoX;
    }
    public void swapColumns(int col1, int col2) {
        matrix.changeCols(col1, col2);
        int tmp = oX[col1];
        oX[col1] = oX[col2];
        oX[col2] = tmp;
    }

    public ArrayList<PivotElement> getPivotElements() {
        return pivotElements;
    }
    public PivotElement getSelectedPivot() {
        return selectedPivot;
    }

    public void setSelectedPivot(PivotElement selectedPivot) {
        this.selectedPivot = selectedPivot;
    }
    public void print() {
        System.out.println("stepMatrix:");
        matrix.print();
        System.out.println("oX");
        for (int x : oX) System.out.print(x + " ");
        System.out.println();
        System.out.println("oY");
        for (int j : oY) System.out.println(j);
    }
    public void findPivotElements() {
        //1) for all i>0: p(i) >= 0 then answer
        int pRow = rows;
        Fraction zero = new Fraction(0,1);
        boolean isAnswer = true;
        for (int j = 0; j <cols; j++) {
            if (matrix.getElement(pRow, j).compare(zero) == -1)
                isAnswer = false;
        }
        if (isAnswer) {
            Fraction[] xs = new Fraction[cols+rows];
            for (int x : oX) {
                xs[x] = new Fraction(0, 1);
            }
            for (int i = 0; i < oY.length; i++) {
                xs[oY[i]] = matrix.getElement(i, cols);
            }

            answer = "x = ("+xs[0].toString();
            for (int i = 1; i < xs.length; i++) {
                answer += ", " + xs[i].toString();
            }
            answer += ")";
            answer += "\nf = " + matrix.getElement(pRow, cols).multiply(new Fraction(-1, 1));
            return;
        }

        //2) if exists pi < 0 AND for all i ai <= 0 then no answer
        boolean isNoAnswer;
        for (int j = 0; j < cols; j ++) {
            if (matrix.getElement(pRow, j).compare(zero) == -1) {
                isNoAnswer = true;
                for (int i = 0; i < rows; i++) {
                    if (matrix.getElement(i, j).compare(zero) == 1) {
                        isNoAnswer = false;
                        break;
                    }
                }
                if (isNoAnswer) {
                    answer = "Функция неограничена снизу";
                    System.out.println("no answer");
                    return;
                }
            }
        }

        //3) if not 1) and 2) then continue process -
        PivotElement colCandidate;
        for (int j = 0; j < cols; j++) {
            colCandidate = null;
            if (matrix.getElement(pRow, j).compare(zero) == -1) {
                for (int i= 0; i < rows; i++){
                    Fraction curEl = matrix.getElement(i,j);
                    if (curEl.compare(zero) == 0) continue;
                    Fraction curValue = matrix.getElement(i,cols).divide(curEl);
                    if (curEl.compare(zero) < 1) continue;
                    //if element is first
                    if (colCandidate == null) {
                        colCandidate = new PivotElement(i,j, curValue);
                    } else {
                        //new best column element
                        if (curValue.compare(colCandidate.getValue()) == -1) {
                            colCandidate.setI(i);
                            colCandidate.setJ(j);
                            colCandidate.setValue(curValue);
                        }
                    }

                }
                if (colCandidate != null)
                    pivotElements.add(colCandidate);
            }

        }
        //find best (min value)
        PivotElement min = null;
        for (PivotElement cur : pivotElements) {
            if (min == null) {
                min = cur;
            } else {
                if (cur.getValue().compare(min.getValue()) == -1) {
                    min = cur;
                }
            }
        }
        if (min == null) {
            System.out.println("pivot element not found??");
        }
        min.setBest(true);
        return;
    }
    public String getAnswer() {
        return answer;
    }
    public StepMatrix nextStepMatrix() {
        matrix.print();
        StepMatrix newMatrix = new StepMatrix(this);
        //1:indexes
        int r = selectedPivot.getI();
        int s = selectedPivot.getJ();
        //switch r and s
        int tmp = newMatrix.getoX()[s];
        newMatrix.getoX()[s] = newMatrix.getoY()[r];
        newMatrix.getoY()[r] = tmp;
        //doing transition
        //2
        Fraction newPivotEl = new Fraction(1,1).divide(matrix.getElement(r,s));
        newMatrix.getMatrix().setElement(r,s, newPivotEl);
        //3: row
        for (int j = 0; j < cols+1; j++) {
            if (j==s) continue;
            newMatrix.getMatrix().setElement(r, j, matrix.getElement(r,j).multiply(newPivotEl));
        }
        //4: col
        for (int i = 0; i < rows+1; i++) {
            if (i==r) continue;
            newMatrix.getMatrix().setElement(i, s, matrix.getElement(i,s).multiply(newPivotEl).multiply(new Fraction(-1,1)));
        }
        //5: rest
        matrix.print();
        for (int i = 0; i < rows+1; i++) {
            if (i == r) continue;
            for (int j = 0; j < cols+1; j++) {
                if (j == s) continue;
                Fraction newEl;
                newEl = matrix.getElement(i,j).subtract(matrix.getElement(i,s).multiply(newMatrix.getMatrix().getElement(r,j)));
                newMatrix.getMatrix().setElement(i,j,newEl);
            }
        }

        return newMatrix;
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
