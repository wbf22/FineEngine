package com.freedommuskrats.fineengine.dal.models.property;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apartmentId;
    private double monthlyPayment;
    private int yearsInApartment;

    @Builder
    public Apartment(double monthlyPayment, int yearsInApartment) {
        this.monthlyPayment = monthlyPayment;
        this.yearsInApartment = yearsInApartment;
    }


}
