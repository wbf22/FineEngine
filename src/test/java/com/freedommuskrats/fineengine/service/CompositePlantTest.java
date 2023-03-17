package com.freedommuskrats.fineengine.service;

import com.freedommuskrats.fineengine.dal.models.comparison.CompositePlan;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.investments.Investment;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.service.fileparsers.CompositePlanParser;
import com.freedommuskrats.fineengine.service.fileparsers.FundParser;
import com.freedommuskrats.fineengine.service.fileparsers.HomeParser;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class CompositePlantTest {



    @Test
    void buildPlan() throws FileNotFoundException {
        Fund fund = FundParser.parseFundBasic("src/test/resources/TestFundForm.txt");
        Home home = HomeParser.parseHomeBasic("src/test/resources/TestHouseForm.txt");
        CompositePlan compositePlan = CompositePlanParser.parseCompositePlan("src/test/resources/TestCompositePlanForm.txt", fund, home);

        compositePlan.displayBasic();
    }

}
