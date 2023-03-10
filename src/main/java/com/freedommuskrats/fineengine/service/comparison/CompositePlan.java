package com.freedommuskrats.fineengine.service.comparison;

import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class CompositePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compositePlanId;

    @OneToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Fund> funds;

    @OneToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Home> homes;

    public CompositePlan(){}

    public CompositePlan(Fund fund, Home home) {
        funds = new ArrayList<>(List.of(fund));
        homes = new ArrayList<>(List.of(home));
    }

}
