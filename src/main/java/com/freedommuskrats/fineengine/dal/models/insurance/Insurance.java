package com.freedommuskrats.fineengine.dal.models.insurance;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceId;
    private double monthlyPayment;
    private double coverage;

    public Insurance(double monthlyPayment, double coverage) {
        this.monthlyPayment = monthlyPayment;
        this.coverage = coverage;
    }

    public Insurance() {

    }
}
