package com.freedommuskrats.fineengine.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.freedommuskrats.fineengine.util.AnnuityMath.buildContributionSchedule;

public class TerminalGraphTest {

    @Test
    void test_TerminalGraph() {
        Map<String, List<Double>> yValues = new LinkedHashMap<>();
        yValues.put("EFT", new ArrayList<Double>(
                    List.of(0.0, 0.5, 2.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
                )
        );

        TerminalGraph graph = new TerminalGraph("Value", "Years", yValues);
    }


}
