package com.freedommuskrats.fineengine.models.investments;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Bond extends Investment{
    private float lengthYears;

}
