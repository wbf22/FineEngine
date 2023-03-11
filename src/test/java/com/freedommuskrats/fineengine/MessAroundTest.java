package com.freedommuskrats.fineengine;


import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.service.comparison.Summary;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.freedommuskrats.fineengine.dal.models.investments.Investment.createConstantContributionSchedule;
import static com.freedommuskrats.fineengine.util.GeneralUtil.*;

public class MessAroundTest {

    int years = 30;
    int yearsToSellHouse = 100;
    double monthlyDisposableIncome = 3500;
    double fundRate = 7; // 7
    double houseAppRate = 4; // 4
    double mortgageRate = 5; // 5
    double houseValue = 320000;
    int loanLength = 15;

    @Test
    void test_CompositePlan() {

        print();
        formatPrint("Years %s", years);
        formatPrint("monthlyDisposableIncome %s", monthlyDisposableIncome);
        formatPrint("fundRate %s", fundRate);
        formatPrint("houseAppRate %s", houseAppRate);
        formatPrint("mortgageRate %s", mortgageRate);
        formatPrint("houseValue %s", houseValue);
        formatPrint("loanLength %s", loanLength);
        print();


        homeThenInvest();

        investThenHome();

        minDownPaymentAndInvestThenHomeThenInvest();

        maxDownPaymentThenHomeThenInvest();

        buyHouseDuringDipMaxDownThenHomeTheInvest();

        buyHouseAfterDipMaxDownThenHomeTheInvest();
    }

    void homeThenInvest(){

        Loan mortgage = new Loan(houseValue, loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundWithHome = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundWithHome.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost();

        print("Buy Home And Then Invest More");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-fund profit %s", eftSummary);
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    void investThenHome(){

        Loan mortgage = new Loan(houseValue, loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        double apartmentProfit = -830 * 12 * 5;

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(5, monthlyDisposableIncome - 830);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundFirst.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() + apartmentProfit;

        print("Apartment and Invest, then Home");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-apartment profit %s", apartmentProfit);
        formatPrint("-fund profit %s", fundFirst.getSummary(years, false));
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    void minDownPaymentAndInvestThenHomeThenInvest(){
        double downPaymentPercentage = .2;

        Loan mortgage = new Loan(houseValue * (1-downPaymentPercentage), loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        double apartmentProfit = -830 * 12 * 5;
        double houseSavings = houseValue * downPaymentPercentage / (12*5);

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(5, monthlyDisposableIncome - 830 - houseSavings);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundFirst.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() + apartmentProfit;

        print("Apartment Min Save for Downpayment and Invest, Then Home");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-downpayment %s", houseValue * downPaymentPercentage);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-apartment profit %s", apartmentProfit);
        formatPrint("-fund profit %s", fundFirst.getSummary(years, false));
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    void maxDownPaymentThenHomeThenInvest() {
        double downPayment = monthlyDisposableIncome * 5 * 12;

        Loan mortgage = new Loan(houseValue - downPayment, loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        double apartmentProfit = -830 * 12 * 5;

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(5, 0.0);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundFirst.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() + apartmentProfit;

        print("Apartment Save for Downpayment no Invest, Then Home, Then Invest");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-downpayment %s", downPayment);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-apartment profit %s", apartmentProfit);
        formatPrint("-fund profit %s", fundFirst.getSummary(years, false));
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    void buyHouseDuringDipMaxDownThenHomeTheInvest() {
        double downPayment = monthlyDisposableIncome * 1 * 12;

        Loan mortgage = new Loan(houseValue * .94 - downPayment, loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        double apartmentProfit = -830 * 12;

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(1, 0.0);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundFirst.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() + apartmentProfit;

        print("Apartment Save for Downpayment no Invest Buy during 2023 dip, Then Home, Then Invest");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-downpayment %s", downPayment);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-apartment profit %s", apartmentProfit);
        formatPrint("-fund profit %s", fundFirst.getSummary(years, false));
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    void buyHouseAfterDipMaxDownThenHomeTheInvest() {
        double downPayment = monthlyDisposableIncome * 2 * 12;

        Loan mortgage = new Loan(houseValue * .98 - downPayment, loanLength, mortgageRate, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);
        Home home = new Home(
                houseAppRate,
                houseValue,
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

        double apartmentProfit = -830 * 12 * 2;

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(2, 0.0);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildContributionSchedule(yearsAndAmounts, years);

        Fund fundFirst = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        Summary homeSummary = home.getSummary(Math.min(Math.min(yearsToSellHouse, years), loanLength), (yearsToSellHouse < loanLength));
        Summary eftSummary = fundFirst.getSummary(years, false);
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() + apartmentProfit;

        print("Apartment Save for Downpayment no Invest Buy a little after 2023 dip, Then Home, Then Invest");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-monthly payment %s", monthlyPayment);
        formatPrint("-downpayment %s", downPayment);
        formatPrint("-mortgage cost %s", monthlyPayment * 12 * loanLength);
        formatPrint("-apartment profit %s", apartmentProfit);
        formatPrint("-fund profit %s", fundFirst.getSummary(years, false));
        print(contributionSchedule);
        formatPrint("-profit minus debt %s", total);
        print();
    }

    List<Double> buildContributionSchedule(Map<Integer, Double> yearsAndAmounts, int desiredLengthYears) {
        int desiredLength = desiredLengthYears * 12;

        List<Double> schedule = new ArrayList<>();
        yearsAndAmounts.forEach((key, value) -> {
            for (int i = 0; i < key * 12; i++) {
                schedule.add(round(value, 2));
            }
        });

        List<Double> resized = schedule;
        if (schedule.size() > desiredLength) {
            resized = schedule.subList(0, desiredLength);
        }
        if (schedule.size() < desiredLength) {
            resized.addAll(
                    createConstantContributionSchedule(0, desiredLength-schedule.size())
            );
        }
        return  resized;
    }


    /**
     * It looks like the best strategy is to divert the max amount of funds to the mortgage. Advantages:
     * - less overall cost from mortgage (lots of interest being paid at start of loan)
     * - less debt risk during initial years
     * - frees up more money and time for investing
     *
     * Some Reasons
     * - you can make money if the house appreciates a lot, but you'll sell at the same time as buying so
     * the gain isn't really achieved.
     * - Investing first isn't better because the mortgage has a lot more interest
     *
     *
     */
}
