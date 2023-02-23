package com.freedommuskrats.fineengine.models.property;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public abstract class Property {
    @Id
    private Long id;
    private float value;
    private float valueAppreciationRate;

}
