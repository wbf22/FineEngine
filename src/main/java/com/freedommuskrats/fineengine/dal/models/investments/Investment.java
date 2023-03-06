package com.freedommuskrats.fineengine.dal.models.investments;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.comparison.Summary;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.AnnuityMath;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MappedSuperclass
@Data
public abstract class Investment {
    @Id
    protected Long id;
    protected double yearlyReturnRate;
    protected double currentValue;
    protected String name;
    @ElementCollection
    protected List<Double> contributionSchedule;

    protected TimeUnit contributionPeriod;

    protected Investment() {

    }

    protected Investment(
            double yearlyReturnRate,
            double value, String name,
            List<Double> contributionSchedule,
            TimeUnit contributionPeriod
    ) {
        this.yearlyReturnRate = yearlyReturnRate;
        this.currentValue = value;
        this.name = name;
        this.contributionSchedule = contributionSchedule;
        this.contributionPeriod = contributionPeriod;
    }


    public abstract Summary getSummary (int years, boolean liquidateAtEnd);


    public Projection makeProjection(
            double startingAmount,
            double contribution,
            TimeUnit contributionPeriod,
            boolean contributeAtBeginning,
            TimeUnit compoundPeriod,
            int years
    ) {
        double compoundPer = (compoundPeriod == TimeUnit.YEAR)? 1 : 12;
        double contributionPer = (contributionPeriod == TimeUnit.YEAR)? 1 : 12;
        int paymentAtEnd = (contributeAtBeginning)? 1 : 0;

        List<ProjectionLine> lines = new ArrayList<>();
        for (int i = 0; i < years; i++) {

            double endBalance = AnnuityMath.getFvValue(
                    startingAmount,
                    1,
                    yearlyReturnRate,
                    compoundPer,
                    contribution,
                    contributionPer,
                    paymentAtEnd);

            double interest = endBalance - startingAmount - contribution * contributionPer;
            lines.add(new ProjectionLine(startingAmount, contribution * contributionPer, interest, contribution * contributionPer * i, endBalance));
            startingAmount = endBalance;
        }

        return new Projection(lines);
    }

    public Projection makeProjection(
            double startingAmount,
            List<Double> contributionSchedule,
            TimeUnit contributionPeriod,
            boolean contributeAtBeginning,
            TimeUnit compoundPeriod
    ) {
        double compoundPer = TimeUnit.convert(contributionPeriod, compoundPeriod);
        int paymentAtEnd = (contributeAtBeginning)? 1 : 0;
        double interestRate = yearlyReturnRate / contributionPeriod.periodPerYear;

        List<ProjectionLine> lines = new ArrayList<>();
        for (int i = 0; i < contributionSchedule.size(); i++) {

            double endBalance = AnnuityMath.getFvValue(
                    startingAmount,
                    1,
                    interestRate,
                    compoundPer,
                    contributionSchedule.get(i),
                    1,
                    paymentAtEnd);

            double interest = endBalance - startingAmount - contributionSchedule.get(i);
            lines.add(new ProjectionLine(startingAmount, contributionSchedule.get(i), interest, contributionSchedule.get(i) * i, endBalance));
            startingAmount = endBalance;
        }

        return new Projection(lines);
    }


    public static List<Double> createConstantContributionSchedule(double amount, int numberContributions) {
        List<Double> schedule = new ArrayList<>();
        for (int i = 0; i < numberContributions; i++) {
            schedule.add(amount);
        }
        return schedule;
    }


}
