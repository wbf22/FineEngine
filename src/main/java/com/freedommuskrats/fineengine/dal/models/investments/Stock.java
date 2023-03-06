package com.freedommuskrats.fineengine.dal.models.investments;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.service.comparison.Summary;
import com.freedommuskrats.fineengine.service.projections.Projection;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import lombok.Data;

import javax.persistence.Entity;

@Entity
public class Stock extends Investment{


    @Override
    public Summary getSummary(int years, boolean liquidateAtEnd) {
        Projection projection = makeProjection(
                currentValue,
                contributionSchedule,
                contributionPeriod,
                false,
                TimeUnit.YEAR);

        years = (years > projection.getLines().size())? projection.getLines().size() - 1 : years;
        ProjectionLine lastLine = projection.getLines().get(years);
        return new Summary(lastLine.getInterest(), 0);
    }
}
