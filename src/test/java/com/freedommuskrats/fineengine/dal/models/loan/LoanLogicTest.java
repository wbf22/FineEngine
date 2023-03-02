package com.freedommuskrats.fineengine.dal.models.loan;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.AnnuityMath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
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
                AnnuityMath.round(lines.get(lines.size() - 1).getEndBalance(),2)
        );
        assertEquals(
                23866.38,
                AnnuityMath.round(lines.get(0).getInterest(),2)
        );
        assertEquals(
                26245.77,
                AnnuityMath.round(lines.get(28).getPrincipal(),2)
        );
    }
}
