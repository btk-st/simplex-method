package ru.yarsu.molab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Solver {
    private final int MAX_SIZE = 40;
    private int varN;
    private int constraintsN;
    private Fraction[] objF = new Fraction[MAX_SIZE];
    private Fraction[][] constraints = new Fraction[MAX_SIZE][MAX_SIZE];

    public Solver(int varN, int constraintsN) {
        this.varN = varN;
        this.constraintsN = constraintsN;
    }

    public Solver() {

    }

    public void init() {
        for (int i = 0; i < MAX_SIZE; i++) {
            objF[i] = new Fraction(0, 1);
            for (int j = 0; j < MAX_SIZE; j++) {
                constraints[i][j] = new Fraction(0, 1);
            }
        }
    }
    public Solver toSupportingTask() {
        //решаем вспомогательную задачу
        int n = varN;
        int m = constraintsN;
        Solver artificialSolver = new Solver(n+m,m);
        //занулили
        artificialSolver.init();
        //целевая ф-я
        //ставим коеф 1 у целевой ф-ии
        for (int i = n; i < n+m; i++) {
            artificialSolver.getObjF()[i] = new Fraction(1,1);
        }
        //ограничения
        //скопируем значения из оригинальной задачи
        for (int i = 0; i <m; i++) {
            //если bi < 0, то умножим строку на -1
            Fraction bi = constraints[i][MAX_SIZE-1];
            //умножаем на 1 или -1
            Fraction multiplier = new Fraction(((Fraction.compareToZero(bi) == -1) ? -1 : 1), 1);
            artificialSolver.getConstraints()[i][MAX_SIZE-1] = new Fraction(bi.multiply(multiplier));
            for (int j = 0; j < n; j ++) {
                artificialSolver.getConstraints()[i][j] = new Fraction(constraints[i][j].multiply(multiplier));
            }
        }
        //1 0 0.. 0 1 0.. 0 0 1 для вспомогательных
        for (int i = 0; i < m; i++) {
            for (int j = n; j < n+m; j++) {
                Fraction fraction;
                fraction = (j-n == i) ? new Fraction(1,1) : new Fraction(0,1);
                artificialSolver.getConstraints()[i][j] = fraction;
            }
        }
        return artificialSolver;
    }
    public void readFromFile(File file) {
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException er) {
            System.out.println("file not found");
            return;
        }

        varN = Integer.parseInt(sc.nextLine());
        constraintsN = Integer.parseInt(sc.nextLine());

        //objective function
        String[] s = sc.nextLine().split(" ");
        for (int i = 0; i < varN; i++) {
            try {
                objF[i] = new Fraction(s[i]);
            } catch (Exception e) {
                System.out.println("error with reading objective function from file");
                objF[i] = new Fraction(0, 1);
            }
        }
        try {
            objF[MAX_SIZE-1] = new Fraction(s[varN]);
        } catch (Exception ex) {
            System.out.println("error with reading objective function from file");
            objF[MAX_SIZE-1] = new Fraction(0, 1);
        }


        //constraint equations
        for (int constraintN = 0; constraintN < constraintsN; constraintN++) {
            s = sc.nextLine().split(" ");
            for (int i = 0; i < varN; i++ ) {
                try {
                    constraints[constraintN][i] = new Fraction(s[i]);
                } catch (Exception e) {
                    System.out.println("error with reading constraints functions from file");
                    constraints[constraintN][i] = new Fraction(0, 1);
                }
            }
            try {
                constraints[constraintN][MAX_SIZE-1] = new Fraction(s[varN]);
            } catch (Exception ex) {
                System.out.println("error with reading constraints functions from file");
                constraints[constraintN][MAX_SIZE-1] = new Fraction(0, 1);
            }
        }
    }

    public void saveToFile(File curFile) {
        FileWriter writer;
        try {
            writer = new FileWriter(curFile);
            writer.write(Integer.toString(varN));
            writer.write("\n");
            writer.write(Integer.toString(constraintsN));
            writer.write("\n");
            //objF
            StringBuilder s = new StringBuilder();
            for (int i=0; i < varN; i++) {
                s.append(objF[i].toString()).append(" ");
            }
            s.append(objF[MAX_SIZE - 1].toString());
            writer.write(s.toString());
            writer.write("\n");
            // constraints

            for (int i = 0; i < constraintsN; i++) {
                s = new StringBuilder();
                for (int j = 0; j < varN; j++) {
                    s.append(constraints[i][j].toString()).append(" ");
                }
                s.append(constraints[i][MAX_SIZE - 1].toString());
                writer.write(s.toString());
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("error saving file");
            return;
        }

    }

    public void print() {
        System.out.println("objf:");
        for (int i = 0; i < varN; i ++) {
            System.out.print(objF[i] + " ");
        }
        System.out.println("|" + objF[MAX_SIZE-1]);
        System.out.println("constr:");
        for (int i = 0; i < constraintsN; i ++) {
            for (int j = 0; j < varN; j ++) {
                System.out.print(constraints[i][j] + " ");

            }
            System.out.println("|" + constraints[i][MAX_SIZE-1]);

        }
    }

    public Matrix toMatrix(int rows, int cols) {
        Fraction[][] res = new Fraction[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                res[i][j] = new Fraction(constraints[i][j]);
            }
            res[i][cols-1] = new Fraction(constraints[i][MAX_SIZE-1]);
        }
        return new Matrix(res, rows, cols);
    }

    public Fraction[] getObjF() {
        return objF;
    }

    public void setObjF(Fraction[] objF) {
        this.objF = objF;
    }

    public Fraction[][] getConstraints() {
        return constraints;
    }

    public void setConstraints(Fraction[][] constraints) {
        this.constraints = constraints;
    }

    public int getVarN() {
        return varN;
    }

    public void setVarN(int varN) {
        this.varN = varN;
    }

    public int getConstraintsN() {
        return constraintsN;
    }

    public void setConstraintsN(int constraintsN) {
        this.constraintsN = constraintsN;
    }

    public int getMAX_SIZE() {
        return MAX_SIZE;
    }
}
