package com.freedommuskrats.fineengine.dal.repository;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.freedommuskrats.fineengine.util.AnnuityMath.buildMonthlyContributionSchedule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class FundRepositoryTest {
    @Autowired
    FundRepository fundRepository;

    Fund createFund() {
        int years = 30;
        double fundRate = 7; // 7


        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(years, 1000.0);
        List<Double> contributionSchedule = buildMonthlyContributionSchedule(yearsAndAmounts, years);

        return new Fund(
                fundRate,
                2000,
                "EFT",
                contributionSchedule,
                null,
                TimeUnit.MONTH
        );

    }

    @Test
    void saveAndLoad_normal() {
        Fund fund = createFund();

        fundRepository.save(fund);

        Fund retrieved = fundRepository.findById(fund.getInvestmentId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(fund.getName(), retrieved.getName());

    }
}
