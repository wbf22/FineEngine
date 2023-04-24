package com.freedommuskrats.fineengine.ai;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.comparison.CompositePlan;
import com.freedommuskrats.fineengine.dal.models.comparison.Summary;
import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.util.GeneralUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.freedommuskrats.fineengine.ai.GeneticAlgoAi.*;
import static com.freedommuskrats.fineengine.ai.GeneticAlgoAi.MONTHLY_INCOME;
import static com.freedommuskrats.fineengine.ai.GeneticAlgoAi.PLAN_LENGTH;
import static com.freedommuskrats.fineengine.service.fileparsers.CompositePlanParser.*;
import static com.freedommuskrats.fineengine.util.GeneralUtil.*;

@Data
public class GeneticInstance {

    private CompositePlan compositePlan;

    private double[] percentSchedule = new double[PLAN_LENGTH];
    private double preHouseSavings;
    private double downPayment;
    private double initialInvestment;
    private int loanLength;
    private int housePrice;
    private int apartmentYears;

    public void init() {
        for (int i = 0; i < percentSchedule.length; i++) {
            percentSchedule[i] = randD(0, 100);
        }
        housePrice = randI(MIN_HOUSE_PRICE, MAX_HOUSE_PRICE);

        apartmentYears = randI(1, (MAX_DOWN_PAYMENT/(MONTHLY_INCOME-APARTMENT_RENT))/12);
        preHouseSavings = apartmentYears * (double) (MONTHLY_INCOME-APARTMENT_RENT) * 12.0;
        downPayment = preHouseSavings * randD(0, 1);

        initialInvestment = preHouseSavings - downPayment;
        loanLength = randI(20, 30);

    }

    public void build() {
        Home home = Home.builder()
                .yearlyReturnRate(HOUSE_APPRECIATION)
                .mortgage(Loan.builder()
                        .loanAmount(housePrice - downPayment)
                        .interestRate(MORTGAGE_RATE)
                        .termYearsLeft(loanLength)
                        .build()
                )
                .value(housePrice)
                .homeInsurance(Insurance.builder()
                        .monthlyPayment(MONTHLY_INSURANCE)
                        .build()
                )
                .pmi(Insurance.builder()
                        .monthlyPayment(MONTHLY_PMI)
                        .build()
                )
                .propertyTaxRate(TAX_RATE)
                .montlyHOAFee(MONTHLY_HOA)
                .yearlyUpkeepCost(YEARLY_UP_KEEP)
                .build();

        home.setDownPayment(downPayment);

        percentSchedule = replaceValuesToLowWithMinMortgagePayment(home, MONTHLY_INCOME, percentSchedule, 0);

        List<Double> fundPostApartment = createContributionSchedule(
                PLAN_LENGTH - apartmentYears, MONTHLY_INCOME, 0, 0, 0,
                percentSchedule, home.getMortgage().getLoanLength(), true);

        List<Double> homeSchedule = createContributionSchedule(
                PLAN_LENGTH - apartmentYears, MONTHLY_INCOME, 0, 0, 0,
                percentSchedule, home.getMortgage().getLoanLength(), false);

        Double[] fundInApartment = new Double[apartmentYears * 12];
        Arrays.fill(fundInApartment, (preHouseSavings-downPayment)/(apartmentYears * 12));
        List<Double> fundSchedule = new ArrayList<>(Arrays.asList(fundInApartment));
        fundSchedule.addAll(fundPostApartment);

        Fund fund = Fund.builder()
                .value(initialInvestment)
                .contributionFrequency(TimeUnit.MONTH)
                .yearlyReturnRate(FUND_RETURN_RATE)
                .build();

        fund.setContributionSchedule(fundSchedule);
        home.setContributionSchedule(homeSchedule);

        compositePlan = CompositePlan.builder()
                .planLengthYears(PLAN_LENGTH)
                .fund(fund)
                .home(home)
                .stringBuilder(buildStringBuilderStuff(home, fund))
                .build();
    }

    private StringBuilder buildStringBuilderStuff(Home home, Fund fund) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("*********Start Values**************");
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Plan Length Years = %s", PLAN_LENGTH));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly Available Income = %s", MONTHLY_INCOME));
        stringBuilder.append("\n");
        stringBuilder.append("Contribution Percentages House vs Investments (Adjusted to meet min mortgage payment and rent):");
        stringBuilder.append("\n");
        Arrays.stream(percentSchedule).sequential().forEach(d -> stringBuilder.append(GeneralUtil.round(d, 1) + ", "));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-House Appreciation Rate = %s", home.getYearlyReturnRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Mortgage Rate = %s", home.getMortgage().getYearlyInterestRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-House Value = %s", home.getStartingValue()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Mortgage Loan Amount = %s", home.getMortgage().getLoanAmount()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly Insurance =  %s", home.getHomeInsurance().getMonthlyPayment()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly PMI = %s", home.getPmi().getMonthlyPayment()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Property Tax Rate = %s", home.getPropertyTaxRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthy HOA Fee = %s", home.getMontlyHOAFee()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Yearly Up Keep = %s", home.getYearlyUpkeepCost()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Yearly Return Rate = %s", fund.getYearlyReturnRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Current Value = %s", fund.getStartingValue()));
        stringBuilder.append("\n");
        return stringBuilder;
    }

    public static GeneticInstance breed(GeneticInstance father, GeneticInstance mother) {
        GeneticInstance newInstance = new GeneticInstance();
        newInstance.init();

        for (int i = 0; i < newInstance.getPercentSchedule().length; i++) {
            if (randI(0, 10) > 6) {
                newInstance.getPercentSchedule()[i] = father.getPercentSchedule()[i];
            }
            else if (randI(0, 10) > 0) {
                newInstance.getPercentSchedule()[i] = mother.getPercentSchedule()[i];
            }
        }

        newInstance.setHousePrice(
                (int) avg(newInstance.getHousePrice(), father.getHousePrice(), mother.getHousePrice())
        );
        newInstance.setApartmentYears(
                (int) avg(newInstance.getApartmentYears(), father.getApartmentYears(), mother.getApartmentYears())
        );
        newInstance.setPreHouseSavings(
                newInstance.getApartmentYears() * (MONTHLY_INCOME-APARTMENT_RENT) * 12
        );
        newInstance.setDownPayment(
                avg(newInstance.getDownPayment(), father.getDownPayment(), mother.getDownPayment())
        );
        newInstance.setInitialInvestment(newInstance.getPreHouseSavings() - newInstance.getDownPayment());
        newInstance.setLoanLength(
                (int) avg(newInstance.getLoanLength(), father.getLoanLength(), mother.getLoanLength())
        );

        newInstance.build();
        return newInstance;
    }

    public double getUtility() {
        Summary homeSummary = compositePlan.getHomes().get(0).getSummary(PLAN_LENGTH - apartmentYears, false);
        Summary fundSummary = compositePlan.getFunds().get(0).getSummary(PLAN_LENGTH, false);
        return homeSummary.profitOrCost() - homeSummary.debt() + fundSummary.profitOrCost();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("*********Instance**************");
        builder.append("\n");
        builder.append("-percent schedule");
        builder.append("\n");
        Arrays.stream(percentSchedule).sequential().forEach(d -> builder.append(GeneralUtil.round(d, 1) + ", "));
        builder.append("\n");
        builder.append(String.format("-years in apartment %s", apartmentYears));
        builder.append("\n");
        builder.append(String.format("-end total %s", getUtility()));
        builder.append("\n");

        Home home = compositePlan.getHomes().get(0);
        builder.append("*********Home**************");
        builder.append("\n");
        builder.append(String.format("-home value %s", home.getStartingValue()));
        builder.append("\n");
        builder.append(String.format("-down payment %s", downPayment));
        builder.append("\n");
        builder.append(String.format("-monthly cost %s", home.getTotalMinMonthlyCost()));
        builder.append("\n");
        builder.append(String.format("-mortgage payment %s", home.getMinMonthlyMortgagePayment()));
        builder.append("\n");
        builder.append(String.format("-loan amount %s", home.getMortgage().getLoanAmount()));
        builder.append("\n");
        builder.append(String.format("-loan length %s", home.getMortgage().getLoanLength()));
        builder.append("\n");
        builder.append(String.format("-down payment %s", home.getSummary(PLAN_LENGTH - apartmentYears, false)));
        builder.append("\n");

        Fund fund = compositePlan.getFunds().get(0);
        builder.append("*********Fund**************");
        builder.append("\n");
        builder.append(String.format("-initial investment %s", initialInvestment));
        builder.append("\n");
        builder.append(String.format("-initial investment %s", fund.getSummary(PLAN_LENGTH, false)));
        builder.append("\n");
        return builder.toString();
    }
}
