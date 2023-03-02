package com.freedommuskrats.fineengine.dal.models;

public enum TimeUnit {
    MONTH(12),
    YEAR(1);

    public double periodPerYear;
    TimeUnit(double periodsPerYear) {
        this.periodPerYear = periodsPerYear;
    }

    public static double convert(TimeUnit desired, TimeUnit current) {
        if (desired == YEAR && current == MONTH) {
            return 12;
        }
        else if (desired == MONTH && current == YEAR) {
            return 1.0 / 12.0;
        }
        return 1;
    }
}
