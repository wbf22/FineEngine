package com.freedommuskrats.fineengine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnuityMathTest {


    @Test
    void annuityMath_getFvValue_sameCompoundAndPaymentPeriod() {
        double numPeriods = 3;
        double rate = 10;
        double compounding = 12;
        double paymentAmount = 10;
        double numPaymentsPerPeriod = 12;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double fv = AnnuityMath.getFvValue(
                0,
                numPeriods,
                rate,
                compounding,
                paymentAmount,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(417.82, AnnuityMath.round(fv, 2));
    }

    @Test
    void annuityMath_getFvValue_differentCompoundAndPaymentPeriod() {
        double numPeriods = 3;
        double rate = 10;
        double compounding = 1;
        double paymentAmount = 10;
        double numPaymentsPerPeriod = 12;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double fv = AnnuityMath.getFvValue(
                0,
                numPeriods,
                rate,
                compounding,
                paymentAmount,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(415.09, AnnuityMath.round(fv, 2));
    }

    @Test
    void annuityMath_getFvValue_differentCompoundAndPaymentPeriodWithStartingValue() {
        double startingAmount = 2000;
        double numPeriods = 3;
        double rate = 10;
        double compounding = 1;
        double paymentAmount = 10;
        double numPaymentsPerPeriod = 12;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double fv = AnnuityMath.getFvValue(
                2000,
                numPeriods,
                rate,
                compounding,
                paymentAmount,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(3077.09, AnnuityMath.round(fv, 2));
    }

    @Test
    void annuityMath_getFvValue_YearlyContributionAndInterest() {
        double startingAmount = 2000;
        double numPeriods = 1;
        double rate = 10;
        double compounding = 1;
        double paymentAmount = 10;
        double numPaymentsPerPeriod = 1;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double fv = AnnuityMath.getFvValue(
                startingAmount,
                numPeriods,
                rate,
                compounding,
                paymentAmount,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(2210.00, AnnuityMath.round(fv, 2));
    }

    @Test
    void annuityMath_getFvValue_MonthlyContributionAndInterest() {
        double startingAmount = 2000;
        double numPeriods = 1;
        double rate = 10.0/12.0;
        double compounding = 1;
        double paymentAmount = 10;
        double numPaymentsPerPeriod = 1;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double fv = AnnuityMath.getFvValue(
                startingAmount,
                numPeriods,
                rate,
                compounding,
                paymentAmount,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(2026.67, AnnuityMath.round(fv, 2));
    }


    @Test
    void annuityMath_getMonthlyPayment() {
        double startingAmount = 400000;
        double numPeriods = 360;
        double rate = 6.0/12.0;
        double compounding = 12;
        double numPaymentsPerPeriod = 1;
        int paymentAtEnd = 0; // 0 for end, 1 for beginning

        double pmt = AnnuityMath.getMonthlyPayment(
                startingAmount,
                numPeriods,
                rate,
                compounding,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(2398.20, AnnuityMath.round(pmt, 2));
    }


    @Test
    void annuityMath_getMonthlyPayment_contributeAtBeginning() {
        double startingAmount = 400000;
        double numPeriods = 360;
        double rate = 6.0/12.0;
        double compounding = 12;
        double numPaymentsPerPeriod = 1;
        int paymentAtEnd = 1; // 0 for end, 1 for beginning

        double pmt = AnnuityMath.getMonthlyPayment(
                startingAmount,
                numPeriods,
                rate,
                compounding,
                numPaymentsPerPeriod,
                paymentAtEnd
        );

        assertEquals(2386.27, AnnuityMath.round(pmt, 2));
    }




}
