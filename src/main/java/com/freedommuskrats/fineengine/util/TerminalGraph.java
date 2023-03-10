package com.freedommuskrats.fineengine.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.freedommuskrats.fineengine.util.GeneralUtil.round;

public class TerminalGraph {

    char[][] graphWithDecorations;
    char[][] graph;
    char[] graphIcons = new char[]{'*', '+', 'o', 'c', '%', '&',  '@', '#'};
    int columns = 8;
    int rows = 8;

    public TerminalGraph(
            String yLabel,
            String xLabel,
            Map<String, List<Double>> yValues)
    {
        graph = new char[36][9];
        graph = initialize(graph);

        graph = makeBorder(graph);

        graph = graphPoints(graph, yValues);

        display(graph);
    }

    private char[][] initialize(char[][] g) {
        for (int x = 0; x < g.length; x++) {
            for (int y = 0; y < g[x].length; y++) {
                g[x][y] = ' ';
            }
        }
        return g;
    }

    private char[][] makeBorder(char[][] g) {
        for (int i = 0; i < g.length; i++) {
            g[i][0] = '.';
        }
        Arrays.fill(g[0], '.');

        return g;
    }

    private char[][] graphPoints(
            char[][] g,
            Map<String, List<Double>> yValues)
    {
        final double[] yminMax = {0, 0};
        final double[] xminMax = {0, 0};
        yValues.forEach((label, values) -> {
            yminMax[0] = Math.min(yminMax[0], Collections.min(values));
            yminMax[1] = Math.max(yminMax[1], Collections.max(values));
            xminMax[1] = Math.max(xminMax[1], values.size());
        });

        double yRange = yminMax[1] - yminMax[0];
        double yStep = yRange / rows;

        double xStep = xminMax[1] / columns;

        final int[] c = {0};
        yValues.forEach((label, values) -> {
            for(int x = 1; x <= columns; x++) {
                int y = getYValueForX(values, x, yStep, xStep);
                g[x * 4][y] = graphIcons[c[0]];
            }
            c[0] += 1;
        });

        return g;
    }

    private int getYValueForX(
            List<Double> yValues,
            int x,
            double yStep,
            double xStep)
    {
        //10, 4
        // 1.25, .5
        //1, 4, 8

        //100, 200, 300, 400
        // 1, 1, 2, 2, 3, 4, 5, 6, 7, 8

        int index = (int) Math.round(x * xStep);
        double average = yValues.get(index - 1) +
                ((yValues.size() > index)? yValues.get(index) : yValues.get(index - 1));
        average /= 2;

        return (int) Math.round(average * yStep);
    }


    private void display(char[][] g) {
        for (int y = g[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < g.length; x++) {
                System.out.print(g[x][y]);
            }
            System.out.println();
        }
    }
}
