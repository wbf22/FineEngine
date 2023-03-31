package com.freedommuskrats.fineengine.util;

import java.util.Random;

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

    public static double randD (double min, double max) {
        Random random = new Random(System.currentTimeMillis() + System.nanoTime());
        return random.nextDouble() * (max - min) + min;
    }

    public static int randI (int min, int max) {
        Random random = new Random(System.currentTimeMillis() + System.nanoTime());
        return (int) Math.round(random.nextDouble() * (max - min) + min);
    }

    public static double avg (double one, double two, double three) {
        return (one + two + three) / 3;
    }
    public char[][] concat(char[][] g, char[] toConcat, int dim) {
        if (dim == 0) {
            char [][] result = new char[g.length + 1][g[0].length];
            for (int i = 0; i < g.length; i++) {
                result[i] = g[i].clone();
            }
            result[g.length] = toConcat.clone();
            return result;
        }
        else
        {
            char [][] result = new char[g.length][g[0].length + 1];
            for (int x = 0; x < g.length; x++) {
                for (int y = 0; y < g[0].length; y++) {
                    result[x][y] = g[x][y];
                }
                result[x][g[0].length] = toConcat[x];
            }
            return result;
        }
    }
}
