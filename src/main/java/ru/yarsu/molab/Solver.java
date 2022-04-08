package ru.yarsu.molab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Solver {
    private int varN;
    private int constraintsN;
    private Fraction[] objF;
    private Fraction[][] constraints;

    public Solver() {

    }

    public void readFromFile(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        varN = Integer.parseInt(sc.nextLine());
        constraintsN = Integer.parseInt(sc.nextLine());
        objF = new Fraction[varN+1];
        constraints = new Fraction[constraintsN][varN+1];

        //objective function
        String[] s = sc.nextLine().split(" ");
        for (int i = 0; i < s.length; i++) {
            try {
                objF[i] = new Fraction(s[i]);
            } catch (Exception e) {
                System.out.println("error with reading objective function from file");
                objF[i] = new Fraction(0, 1);
            }
        }

        //constraint equations
        for (int constraintN = 0; constraintN < constraintsN; constraintN++) {
            s = sc.nextLine().split(" ");
            for (int i = 0; i < s.length; i++ ) {
                try {
                    constraints[constraintN][i] = new Fraction(s[i]);
                } catch (Exception e) {
                    System.out.println("error with reading constraints functions from file");
                    constraints[constraintN][i] = new Fraction(0, 1);
                }
            }
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
}
