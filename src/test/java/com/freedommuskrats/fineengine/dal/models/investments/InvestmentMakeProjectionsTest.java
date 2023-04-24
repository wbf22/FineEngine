package com.freedommuskrats.fineengine.dal.models.investments;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.GeneralUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InvestmentMakeProjectionsTest {


    @Test
    void fund_makeProjection_normal() {
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                1000,
                TimeUnit.MONTH,
                false,
                TimeUnit.YEAR,
                15);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                291705.02,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjectionContributeAtBeginning() {
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                1000,
                TimeUnit.MONTH,
                true,
                TimeUnit.YEAR,
                15);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                293101.58,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjection_withContributionScheduleNoVariationYearly() {

        List<Double> contributions = new ArrayList<>(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0));
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                contributions,
                TimeUnit.YEAR,
                false,
                TimeUnit.YEAR);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                8313.54,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjection_withContributionScheduleNoVariationMonthly() {

        List<Double> contributions = new ArrayList<>(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0));
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                contributions,
                TimeUnit.MONTH,
                false,
                TimeUnit.YEAR);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                7098.06,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjection_withContributionScheduleVariedMonthly() {

        List<Double> contributions = new ArrayList<Double>(List.of(
                1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
                500.0, 500.0, 500.0, 500.0, 500.0));
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                contributions,
                TimeUnit.MONTH,
                false,
                TimeUnit.MONTH);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                9805.18,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjection_withContributionScheduleVaried_compoundMonthlyContributeYearly() {

        List<Double> contributions = new ArrayList<Double>(List.of(
                1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
                500.0, 500.0, 500.0, 500.0, 500.0));
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                contributions,
                TimeUnit.YEAR,
                false,
                TimeUnit.MONTH);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                14095.90,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }

    @Test
    void fund_makeProjection_withContributionScheduleVaried_compoundMonthlyContributeYearly_contributeAtBeginning() {

        List<Double> contributions = new ArrayList<Double>(List.of(
                1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
                500.0, 500.0, 500.0, 500.0, 500.0));
        Fund fund = new Fund(6, 2000, "EFT", null, null, null);
        Projection projection = fund.makeProjection(
                fund.getStartingValue(),
                contributions,
                TimeUnit.YEAR,
                true,
                TimeUnit.MONTH);

        List<ProjectionLine> lines = projection.getLines();
        assertEquals(
                14740.88,
                GeneralUtil.round(
                        lines.get(lines.size() - 1).getEndBalance(),
                        2)
        );

    }



}
