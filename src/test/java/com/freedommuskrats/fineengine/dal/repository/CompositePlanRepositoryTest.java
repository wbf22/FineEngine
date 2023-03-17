package com.freedommuskrats.fineengine.dal.repository;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.dal.models.comparison.CompositePlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.freedommuskrats.fineengine.util.AnnuityMath.buildMonthlyContributionSchedule;

@SpringBootTest
public class CompositePlanRepositoryTest {

    @Autowired
    CompositePlanRepository compositePlanRepository;

    CompositePlan createCompositePlan() {
        int years = 30;
        int yearsToSellHouse = 100;
        double monthlyDisposableIncome = 3500;
        double fundRate = 7; // 7
        double houseAppRate = 4; // 4
        double mortgageRate = 5; // 5
        double houseValue = 320000;
        int loanLength = 15;

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
        double monthlyPayment = home.getMinMonthlyMortgagePayment();

        double apartmentProfit = -830 * 12 * 5;

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(5, 0.0);
        yearsAndAmounts.put(loanLength, monthlyDisposableIncome - monthlyPayment);
        yearsAndAmounts.put(100, monthlyDisposableIncome);
        List<Double> contributionSchedule = buildMonthlyContributionSchedule(yearsAndAmounts, years);

        Fund fund = new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

        return new CompositePlan(years, fund, home, null);
    }

    @Test
    void saveAndLoad_normal() {
        CompositePlan plan = createCompositePlan();

        compositePlanRepository.save(plan);

        CompositePlan retrieved = compositePlanRepository.findById(plan.getCompositePlanId()).orElse(null);
        double mortAmount = retrieved.getHomes().get(0).getMortgage().getLoanAmount();

        assertNotNull(retrieved);
        assertEquals(plan.getHomes().get(0).getName(), retrieved.getHomes().get(0).getName());

    }

}
