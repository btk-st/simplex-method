package ru.yarsu.molab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Solver {
    private final int MAX_SIZE = 17;
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


}
