package com.freedommuskrats.fineengine.util;

import javax.swing.plaf.IconUIResource;

public class AnnuityMath {

    // https://www.quora.com/How-is-a-compound-interest-with-monthly-contributions-formula-applied
    // A = P * (1 + r/n)^(nt) + C * [((1 + r/n)^(nt) - 1) / (r/n)]
    // A = add (1+r/n)^n - 1 / r/n

    // https://www.google.com/search?client=firefox-b-1-d&q=annuity+formula

    //https://www.investopedia.com/retirement/calculating-present-and-future-value-of-annuities/
    //https://www.calculatorsoup.com/calculators/financial/future-value-annuity-calculator.php
    //https://www.calculatorsoup.com/calculators/financial/loan-calculator.php

    public static double equivalentInterestRate(double rate, double compounding, double desiredCompounding) {
        double first = Math.pow(
                1 + rate/compounding,
                compounding/desiredCompounding) - 1;
        return desiredCompounding * first;
    }

    public static double getFvValue(
            double initialValue,
            double numPeriods,
            double ratePerPeriod,
            double compoundingPerPeriod,
            double paymentAmount,
            double numberOfPaymentsPerPeriod,
            int paymentAtEnd) {


        double i = equivalentInterestRate(ratePerPeriod/100, compoundingPerPeriod, numberOfPaymentsPerPeriod);
        i /= numberOfPaymentsPerPeriod;
        double n = numberOfPaymentsPerPeriod * numPeriods;

        double fir = paymentAmount;
        double sec = Math.pow(1+i, n) - 1;
        sec /= i;
        double thir = 1 + i*paymentAtEnd;
        double fv = fir * sec * thir;

        double principalAppr = initialValue * Math.pow(1+i, n);

        return fv + principalAppr;
    }


    public static double getMonthlyPayment(
            double initialValue,
            double numPeriods,
            double ratePerPeriod,
            double compoundingPerPeriod,
            double numberOfPaymentsPerPeriod,
            int paymentAtEnd
    ) {
        double i = ratePerPeriod/100;
        double n = numberOfPaymentsPerPeriod * numPeriods;

        double principalAppr = initialValue * i * Math.pow(1+i, n);

        double sec = Math.pow(1+i, n) - 1;

        double pv = principalAppr / sec;

        return pv / (1 + i*paymentAtEnd);
    }

}
