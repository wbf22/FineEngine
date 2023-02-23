package com.freedommuskrats.fineengine.models.investments;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
@Data
public class Fund extends Investment{
    private String conditions;
}
