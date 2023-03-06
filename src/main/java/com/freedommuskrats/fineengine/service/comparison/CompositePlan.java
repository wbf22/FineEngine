package com.freedommuskrats.fineengine.service.comparison;

import com.freedommuskrats.fineengine.dal.models.investments.Investment;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;


public class CompositePlan {


    @Id
    private Long id;


    private List<Investment> investment;


}
