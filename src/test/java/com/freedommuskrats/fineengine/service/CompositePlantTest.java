package com.freedommuskrats.fineengine.service;

import com.freedommuskrats.fineengine.dal.models.comparison.CompositePlan;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.service.fileparsers.CompositePlanParser;
import com.freedommuskrats.fineengine.service.fileparsers.FundParser;
import com.freedommuskrats.fineengine.service.fileparsers.HomeParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

public class CompositePlantTest {



    @Test
    void buildPlan() throws IOException {
        String[] years = {"1a", "1b", "2023", "2024", "2025", "2026", "2027"};

        Arrays.stream(years).sequential().forEach(year -> {
            try {
                Fund fund = FundParser.parseFundBasic("src/test/resources/"+ year + "/F.txt");
                Home home = HomeParser.parseHomeBasic("src/test/resources/"+ year + "/H.txt");
                CompositePlan compositePlan = CompositePlanParser.parseCompositePlan("src/test/resources/"+ year + "/Cp.txt", fund, home, true);

                compositePlan.displayBasic("src/test/resources/"+ year + "/Result.txt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }

}
