package com.freedommuskrats.fineengine.dal.models.loan;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.AnnuityMath;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loadId;
    private double loanAmount;
    private double termYearsLeft;
    private double yearlyInterestRate;
    @ElementCollection
    private List<Double> contributionSchedule;

    public Loan() {

    }


    public Loan(double loanAmount, double termYearsLeft, double interestRate, List<Double> contributionSchedule) {
        this.loanAmount = loanAmount;
        this.termYearsLeft = termYearsLeft;
        this.yearlyInterestRate = interestRate;
        this.contributionSchedule = contributionSchedule;
    }



    public Projection calculateMonthlyPaymentSchedule(
            double startingAmount,
            TimeUnit contributionPeriod,
            boolean contributeAtBeginning,
            TimeUnit compoundPeriod,
            int years
    ) {
        double compoundPer = (compoundPeriod == TimeUnit.YEAR)? 1 : 12;
        double contributionPer = (contributionPeriod == TimeUnit.YEAR)? 1 : 12;
        int paymentAtEnd = (contributeAtBeginning)? 1 : 0;

        double contribution = AnnuityMath.getMonthlyPayment(
                startingAmount,
                years,
                yearlyInterestRate / 12.0,
                compoundPer,
                contributionPer,
                paymentAtEnd);

        List<ProjectionLine> lines = new ArrayList<>();
        for (int i = 0; i < years; i++) {

            double endBalance = AnnuityMath.getFvValue(
                    startingAmount,
                    1,
                    yearlyInterestRate,
                    compoundPer,
                    -contribution,
                    contributionPer,
                    paymentAtEnd);

            double interest = endBalance - startingAmount + contribution * contributionPer;
            double principal = 12 * contribution - interest;
            lines.add(new ProjectionLine(startingAmount, contribution * contributionPer, interest, principal, endBalance));
            startingAmount = endBalance;
        }

        return new Projection(lines);
    }

    public Projection calculateMonthlyPaymentSchedule(
            double startingAmount,
            TimeUnit contributionPeriod,
            boolean contributeAtBeginning,
            TimeUnit compoundPeriod,
            int years,
            List<Double> extraPayment
    ) {
        int extraPaymentYears = (contributionPeriod == TimeUnit.YEAR)? extraPayment.size() : extraPayment.size() * 12;
        if (extraPaymentYears < years)
            throw new RuntimeException("Extra payment schedule must be as long as 'years'");

        double compoundPer = (compoundPeriod == TimeUnit.YEAR)? 1 : 12;
        double contributionPer = (contributionPeriod == TimeUnit.YEAR)? 1 : 12;
        int paymentAtEnd = (contributeAtBeginning)? 1 : 0;

        double monthlyPayment = AnnuityMath.getMonthlyPayment(
                startingAmount,
                years,
                yearlyInterestRate / 12.0,
                compoundPer,
                contributionPer,
                paymentAtEnd);

        List<ProjectionLine> lines = new ArrayList<>();
        for (int i = 0; i < years; i++) {

            double monthlyPaymentWithExtra = monthlyPayment + getExtraMonthlyPayment(i, contributionPeriod, extraPayment);

            double endBalance = AnnuityMath.getFvValue(
                    startingAmount,
                    1,
                    yearlyInterestRate,
                    compoundPer,
                    -monthlyPaymentWithExtra,
                    contributionPer,
                    paymentAtEnd);

            double interest = endBalance - startingAmount + monthlyPaymentWithExtra * contributionPer;
            double principal = contributionPer * monthlyPaymentWithExtra - interest;
            lines.add(new ProjectionLine(startingAmount, monthlyPaymentWithExtra * contributionPer, interest, principal, endBalance));
            startingAmount = endBalance;

            if (endBalance < 0) {
                return new Projection(lines);
            }
        }

        return new Projection(lines);
    }

    double getExtraMonthlyPayment(
            int startYear,
            TimeUnit contributionPeriod,
            List<Double> extraPayment)
    {
        double extra;
        if (contributionPeriod == TimeUnit.MONTH) {
            extra = extraPayment.subList(startYear * 12, startYear * 12 + 12).stream().reduce(0.0, Double::sum);
            extra /= 12;
        }
        else {
            extra = extraPayment.get(startYear) / 12;
        }
        return extra;
    }

}
