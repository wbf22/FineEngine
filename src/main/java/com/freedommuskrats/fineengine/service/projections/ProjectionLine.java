package com.freedommuskrats.fineengine.service.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
public class ProjectionLine {
    private double startBalance;
    private double contribution;
    private double interest;
    private double principal;
    private double endBalance;



}
