package ru.yarsu.molab;

public class Fraction {
    double numerator;
    double denominator;

    /**
     * Constructor
     *
     * @param numr
     * @param denr
     */
    public Fraction(double numr, double denr) {
        numerator = numr;
        denominator = denr;
        reduce();
    }

    //cpu
    public Fraction(Fraction that) {
        this(that.getNumerator(), that.getDenominator());
    }

    public Fraction(String fraction) throws IllegalArgumentException {
        String[] nums = fraction.split("/");
        try {
            switch (nums.length) {
                case 1:
                    numerator = Double.parseDouble(nums[0]);
                    denominator = 1;
                    break;
                case 2:
                    numerator = Double.parseDouble(nums[0]);
                    denominator = Double.parseDouble(nums[1]);
                    break;
                default:
                    throw new IllegalArgumentException("bad format");
            }
            System.out.println(numerator);
            System.out.println(denominator);

        } catch (IllegalArgumentException e) {
            numerator = 0;
            denominator = 1;
            throw e;
        }


        reduce();

    }

    public double getNumerator() {
        return numerator;
    }

    public void setNumerator(double numerator) {
        this.numerator = numerator;
    }

    public double getDenominator() {
        return denominator;
    }

    public void setDenominator(double denominator) {
        this.denominator = denominator;
    }

    /**
     * Calculates gcd of two numbers
     *
     * @param numerator
     * @param denominator
     * @return
     */
    private int calculateGCD(double numerator, double denominator) {
        if (numerator % denominator == 0) {
            return (int) Math.abs(denominator);
        }
        return calculateGCD(denominator, numerator % denominator);
    }

    /**
     * @param fraction
     * @return -1, 0, 1
     */
    public int compare(Fraction fraction) {
        Fraction result = this.subtract(fraction);
        if (result.getNumerator() == 0) return 0;
        if (result.getNumerator() < 0 && result.getDenominator() > 0 || result.getNumerator() > 0 && result.getDenominator() < 0)
            return -1;
        return 1;
    }
    public static int compareToZero(Fraction fraction) {
        if (fraction.getNumerator() == 0) return 0;
        if (fraction.getNumerator() < 0 && fraction.getDenominator() > 0 || fraction.getNumerator() > 0 && fraction.getDenominator() < 0)
            return -1;
        return 1;
    }
    /**
     * Reduce the fraction to lowest form
     */
    void reduce() {
        //если дробное - оставляем так
        if (numerator % 1 != 0 || denominator % 1 != 0) return;
        int gcd = calculateGCD(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;
        if (numerator < 0 && denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    /**
     * Adds two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction add(Fraction fractionTwo) {
        double numer = (numerator * fractionTwo.getDenominator()) +
                (fractionTwo.getNumerator() * denominator);
        double denr = denominator * fractionTwo.getDenominator();
        return new Fraction(numer, denr);
    }

    /**
     * Subtracts two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction subtract(Fraction fractionTwo) {
        double newNumerator = (numerator * fractionTwo.denominator) -
                (fractionTwo.numerator * denominator);
        double newDenominator = denominator * fractionTwo.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Multiples two functions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction multiply(Fraction fractionTwo) {
        double newNumerator = numerator * fractionTwo.numerator;
        double newDenominator = denominator * fractionTwo.denominator;
        return new Fraction(newNumerator, newDenominator);
    }
    public boolean isDouble() {
        return  (numerator % 1 != 0);

    }
    /**
     * Divides two fractions
     *
     * @param fractionTwo
     * @return
     */
    public Fraction divide(Fraction fractionTwo) {
        double newNumerator = numerator * fractionTwo.getDenominator();
        double newDenominator = denominator * fractionTwo.numerator;
        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Returns string representation of the fraction
     */
    @Override
    public String toString() {
        if (numerator == 1 && denominator == -1) return "-1";
        if (Math.abs(denominator) != 1)
            return (int)numerator + "/" + (int)denominator;
        else if (numerator % 1 == 0) {
            //если в числителе - целое число
            return Integer.toString((int)numerator);
        } else {
            //если не целое
            return Double.toString(numerator);
        }
    }
    public void toDouble() {
        numerator = Math.ceil((numerator / denominator) * 1000) / 1000;
        denominator = 1;
    }

    public boolean isFraction() {
        return denominator != 1;
    }
}
