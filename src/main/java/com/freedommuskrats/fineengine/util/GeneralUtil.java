package com.freedommuskrats.fineengine.util;

public class GeneralUtil {

    public static void print(Object message) {
        System.out.println(message);
    }

    public static void print() {
        System.out.println();
    }


    public static double round(double value, int precision) {
        double mult = Math.pow(10, precision);
        return Math.round(value * mult) / mult;
    }
}
