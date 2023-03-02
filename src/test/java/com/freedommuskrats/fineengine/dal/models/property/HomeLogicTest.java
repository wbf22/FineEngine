package com.freedommuskrats.fineengine.dal.models.property;

import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.util.AnnuityMath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HomeLogicTest {


    @Test
    void getTotalCost_normal() {
        Loan mortgage = new Loan(400000, 30, 6, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);

        Home home = new Home(3, 400000, "House", null, mortgage, 1.2, homeInsurance, pmi, 0, 4000);

        double totalCost = home.getTotalCost(30, true);
        assertEquals(210087.77, AnnuityMath.round(totalCost,2));
    }

    @Test
    void testStuff() {
        Loan mortgage = new Loan(400000, 15, 6, null);
        Insurance homeInsurance = new Insurance(125, 0);
        Insurance pmi = new Insurance(120, 0);

        Home home = new Home(5, 400000, "House", null, mortgage, 1.2, homeInsurance, pmi, 0, 4000);

        double totalCost = home.getTotalCost(15, false);
        System.out.println(totalCost);
    }


}
