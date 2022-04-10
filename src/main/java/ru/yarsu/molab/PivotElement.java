package ru.yarsu.molab;

public class PivotElement {
    private int i;
    private int j;
    boolean isBest;
    private Fraction value;

    public PivotElement(int i, int j, Fraction value) {
        this.i = i;
        this.j = j;
        this.value = value;
        this.setBest(false);
    }

    public Fraction getValue() {
        return value;
    }

    public void setValue(Fraction value) {
        this.value = value;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public boolean isBest() {
        return isBest;
    }

    public void setBest(boolean best) {
        isBest = best;
    }
}
