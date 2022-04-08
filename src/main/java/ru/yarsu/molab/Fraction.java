package ru.yarsu.molab;

import java.text.ParseException;

public class Fraction {
    int numerator;
    int denominator;

    /**
     * Constructor
     *
     * @param numr
     * @param denr
     */
    public Fraction(int numr, int denr) {
        numerator = numr;
        denominator = denr;
        reduce();
    }

    public Fraction(String fraction) throws Exception {
        String[] nums = fraction.split("/");

        switch (nums.length) {
            case 1:
                numerator = Integer.parseInt(nums[0]);
                denominator = 1;
                break;
            case 2:
                numerator = Integer.parseInt(nums[0]);
                denominator = Integer.parseInt(nums[1]);
                break;
            default:
                throw new Exception("bad format");
        }

        reduce();

    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    /**
     * Calculates gcd of two numbers
     *
     * @param numerator
     * @param denominator
     * @return
     */
    public int calculateGCD(int numerator, int denominator) {
        if (numerator % denominator == 0) {
            return denominator;
        }
        return calculateGCD(denominator, numerator % denominator);
    }

    /**
     * Reduce the fraction to lowest form
     */
    void reduce() {
        int gcd = calculateGCD(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;
    }

    /**
     * Adds two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction add(Fraction fractionTwo) {
        int numer = (numerator * fractionTwo.getDenominator()) +
                (fractionTwo.getNumerator() * denominator);
        int denr = denominator * fractionTwo.getDenominator();
        return new Fraction(numer, denr);
    }

    /**
     * Subtracts two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction subtract(Fraction fractionTwo) {
        int newNumerator = (numerator * fractionTwo.denominator) -
                (fractionTwo.numerator * denominator);
        int newDenominator = denominator * fractionTwo.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Multiples two functions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction multiply(Fraction fractionTwo) {
        int newNumerator = numerator * fractionTwo.numerator;
        int newDenominator = denominator * fractionTwo.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Divides two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction divide(Fraction fractionTwo) {
        int newNumerator = numerator * fractionTwo.getDenominator();
        int newDenominator = denominator * fractionTwo.numerator;
        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Returns string representation of the fraction
     */
    @Override
    public String toString() {
        if (denominator != 1)
            return this.numerator + "/" + this.denominator;
        else return Integer.toString(this.numerator);
    }
}
