package com.freedommuskrats.fineengine.dal.models.investments;

import lombok.*;

import javax.persistence.Entity;
import java.util.Map;

@Entity
public class Fund extends Investment{
    private String conditions;

    public Fund() {
        super();
    }

    public Fund(float returnRate, float value, String name, Map<Double, Double> contributionSchedule, String conditions) {
        super(returnRate, value, name, contributionSchedule);
        this.conditions = conditions;
    }


}
