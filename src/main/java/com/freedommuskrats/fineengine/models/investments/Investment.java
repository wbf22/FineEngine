package com.freedommuskrats.fineengine.models.investments;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
public abstract class Investment {
    @Id
    private Long id;
    private float returnRate;
    private float value;
    private String name;
    @ElementCollection
    private Map<Float, Float> contributionSchedule;

}
