package com.freedommuskrats.fineengine.dal.models.property;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Investment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Map;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class Property extends Investment {
    protected double valueAppreciationRate;

    protected Property(){}

    protected Property(double valueAppreciationRate, double currentValue, String name, List<Double> contributionSchedule, TimeUnit paymentFrequency) {
        super(valueAppreciationRate, currentValue, name, contributionSchedule, paymentFrequency);
    }
}
