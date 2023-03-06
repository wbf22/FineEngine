package com.freedommuskrats.fineengine.dal.models.property;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Investment;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
@Data
public abstract class Property extends Investment {
    protected double valueAppreciationRate;

    public Property(){}

    public Property(double valueAppreciationRate, double currentValue, String name, List<Double> contributionSchedule, TimeUnit paymentFrequency) {
        super(valueAppreciationRate, currentValue, name, contributionSchedule, paymentFrequency);
    }
}
