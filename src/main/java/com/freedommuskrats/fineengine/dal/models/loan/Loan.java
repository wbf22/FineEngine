package com.freedommuskrats.fineengine.dal.models.loan;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.AnnuityMath;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class Loan {
    @Id
    private Long id;
    private double loanAmount;
    private double termYearsLeft;
    private double yearlyInterestRate;
    @ElementCollection
    private Map<Double, Double> contributionSchedule;

    public Loan() {

    }


    public Loan(double loanAmount, double termYearsLeft, double interestRate, Map<Double, Double> contributionSchedule) {
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

}
