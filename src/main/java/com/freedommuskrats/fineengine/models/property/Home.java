package com.freedommuskrats.fineengine.models.property;

import com.freedommuskrats.fineengine.models.insurance.Insurance;
import com.freedommuskrats.fineengine.models.loan.Loan;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Data
public class Home extends Property{

    @OneToOne
    private Loan loan;
    @OneToOne
    private Insurance homeInsurance;
    @OneToOne
    private Insurance pmi;
    private float montlyHOAFee;
    private float yearlyUpkeepCost;


}
