package com.freedommuskrats.fineengine.util;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerminalGraphTest {

    @Test
    void test_TerminalGraph() {
        Map<String, List<Double>> yValues = new LinkedHashMap<>();
        yValues.put("EFT", new ArrayList<>(
                    List.of(1000.0, 4000.0, 8000.0, 16000.0, 32000.0, 64000.0)
                )
        );

        TerminalGraph graph = new TerminalGraph("Value", "Years", yValues);
    }

    @Test
    void test_TerminalGraph_withRealAnnuity() {
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                1000,
                TimeUnit.MONTH,
                false,
                TimeUnit.YEAR,
                15);

        List<ProjectionLine> lines = projection.getLines();


        Map<String, List<Double>> yValues = new LinkedHashMap<>();
        List<Double> values = new ArrayList<>();
        for (ProjectionLine line : lines) {
            values.add(line.getEndBalance());
        }
        yValues.put("EFT", values);


        TerminalGraph graph = new TerminalGraph("Value", "Years", yValues);
    }


}
