package com.freedommuskrats.fineengine.dal.models.property;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.investments.Investment;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.service.comparison.Summary;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Map;

@Entity
@EqualsAndHashCode(callSuper=false)
public class Home extends Property {

    @OneToOne
    private Loan mortgage;
    private double propertyTaxRate;
    @OneToOne
    private Insurance homeInsurance;
    @OneToOne
    private Insurance pmi;
    private double montlyHOAFee;
    private double yearlyUpkeepCost;

    public Home(
            double returnRate,
            double value,
            String name,
            List<Double> contributionSchedule,
            Loan mortgage,
            double propertyTaxRate,
            Insurance homeInsurance,
            Insurance pmi,
            double montlyHOAFee,
            double yearlyUpkeepCost) {
        super(returnRate, value, name, contributionSchedule, TimeUnit.MONTH);
        this.mortgage = mortgage;
        this.propertyTaxRate = propertyTaxRate;
        this.homeInsurance = homeInsurance;
        this.pmi = pmi;
        this.montlyHOAFee = montlyHOAFee;
        this.yearlyUpkeepCost = yearlyUpkeepCost;
    }

    public Home() {

    }

    @Override
    public Summary getSummary(int years, boolean liquidateAtEnd) {
        Projection mortgageProjection = getMonthlyMortgagePaymentSchedule();

        years = (years >= mortgageProjection.getLines().size())? mortgageProjection.getLines().size() - 1 : years;
        ProjectionLine lastLine = mortgageProjection.getLines().get(years);
        double cost = getTotalCost(years, liquidateAtEnd) * -1;
        if (liquidateAtEnd) {
            return new Summary(cost, 0);
        }
        else {
            return new Summary(cost, lastLine.getEndBalance());
        }
    }

    public double monthlyMortgagePayment() {
        return mortgage.calculateMonthlyPaymentSchedule(
                mortgage.getLoanAmount(),
                TimeUnit.MONTH,
                false,
                TimeUnit.MONTH,
                (int) Math.round(mortgage.getTermYearsLeft())
        ).getLines().get(0).getContribution() / 12;
    }

    public Projection getMonthlyMortgagePaymentSchedule() {
        return mortgage.calculateMonthlyPaymentSchedule(
                mortgage.getLoanAmount(),
                TimeUnit.MONTH,
                false,
                TimeUnit.MONTH,
                (int) Math.round(mortgage.getTermYearsLeft())
        );
    }

    public double getTotalCost(int yearsToProject, boolean sellAtEnd) {
        Projection mortgageProjection = getMonthlyMortgagePaymentSchedule();


        Projection houseAppr = makeProjection(
                currentValue,
                0,
                TimeUnit.YEAR,
                false,
                TimeUnit.YEAR,
                yearsToProject
        );

        double totalOutOfPocket = getTotalOutOfPocket(mortgageProjection, yearsToProject, houseAppr);

        double newHouseValue = (sellAtEnd)? houseAppr.getLines().get(houseAppr.getLines().size() - 1).getEndBalance()
                : 0;
        double mortgageEndBalance = mortgageProjection.getLines().get(
                (yearsToProject <= mortgageProjection.getLines().size())? yearsToProject - 1 : mortgageProjection.getLines().size() - 1
        ).getEndBalance();
        double profitFromSale = (sellAtEnd)? newHouseValue - mortgageEndBalance : 0;

        double totalCost = profitFromSale
                - totalOutOfPocket;

        return totalCost * -1;
    }

    private double getTotalOutOfPocket(Projection mortgageProjection, int yearsToProject, Projection houseAppr){
        double mortgageCost = mortgageProjection.getLines().get(0).getContribution() * yearsToProject;
        double hoaCost = montlyHOAFee * 12 * yearsToProject;
        double insCost = homeInsurance.getMonthlyPayment() * 12 * yearsToProject;

        double pmiCost = 0;
        double loanToValueRatio = mortgageProjection.getLines().get(0).getStartBalance() / currentValue;
        int i = 0;
        while (loanToValueRatio > .8 && i < yearsToProject) {
            pmiCost += pmi.getMonthlyPayment() * 12;
            loanToValueRatio = mortgageProjection.getLines().get(i).getEndBalance()
                    / houseAppr.getLines().get(i).getEndBalance();
            i++;
        }

        double taxCost = currentValue * propertyTaxRate / 100 * yearsToProject;
        double yearlyUpKeepCost = yearlyUpkeepCost * yearsToProject;
        return  mortgageCost + hoaCost + insCost + pmiCost + taxCost + yearlyUpKeepCost;
    }

}
