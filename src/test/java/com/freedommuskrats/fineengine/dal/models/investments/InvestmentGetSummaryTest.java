package com.freedommuskrats.fineengine.dal.models.investments;


import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.service.comparison.Summary;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.freedommuskrats.fineengine.dal.models.investments.Investment.createConstantContributionSchedule;
import static com.freedommuskrats.fineengine.util.GeneralUtil.print;

public class InvestmentGetSummaryTest {


    @Test
    void test_CompositePlan() {
        int years = 15;
        double monthlyDisposableIncome = 3500;
        double fundRate = 3;

        Loan mortgage = new Loan(380000, 15, 6, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                5,
                400000,
                "House",
                null,
                mortgage,
                1.2,
                homeInsurance,
                pmi,
                0,
                4000
        );
        double monthlyPayment = home.monthlyMortgagePayment();
        List<Double> duringMortgage = createConstantContributionSchedule(
                monthlyDisposableIncome - monthlyPayment,
                15 * 12);
        List<Double> withoutMortgage = createConstantContributionSchedule(
                monthlyDisposableIncome,
                5 * 12);
        List<Double> contributionSchedule = new ArrayList<>();
        contributionSchedule.addAll(duringMortgage);
        contributionSchedule.addAll(withoutMortgage);

        Fund fundWithHome = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(years, false);
        Summary eftSummary = fundWithHome.getSummary(years, false);

        print("Home");
        print(homeSummary);
        print(monthlyPayment);
        print(eftSummary);
        print();

        contributionSchedule = new ArrayList<>();
        contributionSchedule.addAll(withoutMortgage);
        contributionSchedule.addAll(duringMortgage);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        print("Just Investment");
        print(home.getSummary(years - 5, false));
        print(fundFirst.getSummary(years, false));
        print();
    }


}
