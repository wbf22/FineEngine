package com.freedommuskrats.fineengine.dal.models.insurance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Insurance {
    @Id
    private Long id;
    private double monthlyPayment;
    private double coverage;

    public Insurance(double monthlyPayment, double coverage) {
        this.monthlyPayment = monthlyPayment;
        this.coverage = coverage;
    }

    public Insurance() {

    }
}
