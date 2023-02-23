package com.freedommuskrats.fineengine.models.insurance;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Insurance {
    @Id
    private Long id;
    private float monthlyPayment;
    private float coverage;
}
