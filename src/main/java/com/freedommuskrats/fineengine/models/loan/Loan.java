package com.freedommuskrats.fineengine.models.loan;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
@Data
public class Loan {
    @Id
    private Long id;
    private float loanAmount;
    private float termYearsLeft;
    private float interestRate;
    @ElementCollection
    private Map<Float, Float> contributionSchedule;
}
