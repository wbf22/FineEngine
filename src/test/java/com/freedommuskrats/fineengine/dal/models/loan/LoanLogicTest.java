package com.freedommuskrats.fineengine.dal.models.loan;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.GeneralUtil;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.freedommuskrats.fineengine.util.AnnuityMath.buildMonthlyContributionSchedule;
import static org.junit.jupiter.api.Assertions.assertEquals;


class LoanLogicTest {

    @Test
    void calculateFVOfLoan_normal() {
        Loan loan = new Loan(400000, 30, 6, null);
        Projection projection = loan.calculateMonthlyPaymentSchedule(
                loan.getLoanAmount(),
                TimeUnit.MONTH,
                false,
                TimeUnit.MONTH,
                30);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                0,
                GeneralUtil.round(lines.get(lines.size() - 1).getEndBalance(),2)
        );
        assertEquals(
                23866.38,
                GeneralUtil.round(lines.get(0).getInterest(),2)
        );
        assertEquals(
                26245.77,
                GeneralUtil.round(lines.get(28).getPrincipal(),2)
        );
    }


    @Test
    void calculateFVOfLoan_withContributionSchedule() {
        Loan loan = new Loan(400000, 30, 6, null);

        Map<Integer, Double> yearsAndAmounts = new LinkedHashMap<>();
        yearsAndAmounts.put(30, 100.0);
        List<Double> extraPayments = buildMonthlyContributionSchedule(yearsAndAmounts, 30);

        Projection projection = loan.calculateMonthlyPaymentSchedule(
                loan.getLoanAmount(),
                TimeUnit.MONTH,
                false,
                TimeUnit.MONTH,
                30,
                extraPayments);

        List<ProjectionLine> lines = projection.getLines();
        double totalInterest = lines.stream()
                .map(ProjectionLine::getInterest)
                .reduce(0.0, Double::sum);

        assertEquals(
                407594.13,
                GeneralUtil.round(totalInterest,2)
        );
    }
}
