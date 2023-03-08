package com.freedommuskrats.fineengine.util;

public class GeneralUtil {

    public static void formatPrint(String format, Object singleValue) {
        print(String.format(format, singleValue.toString()));
    }

    public static void print(Object object) {
        System.out.println(object);
    }

    public static void print() {
        System.out.println();
    }


    public static double round(double value, int precision) {
        double mult = Math.pow(10, precision);
        return Math.round(value * mult) / mult;
    }
}
