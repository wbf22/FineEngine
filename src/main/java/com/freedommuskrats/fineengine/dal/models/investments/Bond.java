package com.freedommuskrats.fineengine.dal.models.investments;

import lombok.Data;

import javax.persistence.Entity;

@Entity
public class Bond extends Investment{
    private float lengthYears;

}
