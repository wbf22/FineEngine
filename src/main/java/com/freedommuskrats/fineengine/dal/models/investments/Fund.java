package com.freedommuskrats.fineengine.dal.models.investments;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.comparison.Summary;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Fund extends Investment {


    private String conditions;

    public Fund() {
        super();
    }


    @Builder
    public Fund(double yearlyReturnRate, double value, String name, List<Double> contributionSchedule, String conditions, TimeUnit contributionFrequency) {
        super(yearlyReturnRate, value, name, contributionSchedule, contributionFrequency);
        this.conditions = conditions;
    }

    @Override
    public Summary getSummary(int years, boolean liquidateAtEnd) {
        Projection projection = makeProjection(
                currentValue,
                contributionSchedule,
                contributionPeriod,
                false,
                TimeUnit.YEAR);

        int periods = (int) (years * contributionPeriod.periodPerYear);

        periods = (periods >= projection.getLines().size())? projection.getLines().size() - 1 : periods;

        double interest = projection.getLines()
                .subList(0, periods).stream()
                .map(ProjectionLine::getInterest)
                .reduce(0.0, Double::sum);
        return new Summary(interest, 0);
    }


}
