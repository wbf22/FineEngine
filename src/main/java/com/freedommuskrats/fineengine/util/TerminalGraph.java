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
    int graphIndent = 16;
    int graphCenter = graphIndent + 4 * columns/2;

    public TerminalGraph(
            String yLabel,
            String xLabel,
            Map<String, List<Double>> yValues)
    {
        final double[] yminMax = {0, 0};
        final double[] xminMax = {0, 0};
        yValues.forEach((label, values) -> {
            yminMax[0] = Math.min(yminMax[0], Collections.min(values));
            yminMax[1] = Math.max(yminMax[1], Collections.max(values));
            xminMax[1] = Math.max(xminMax[1], values.size());
        });

        graph = new char[37][9];
        graph = initialize(graph);

        graph = makeBorder(graph);

        graph = graphPoints(graph, yValues, yminMax, xminMax);

        graph = insetGraphWithBorder(graph, 16, yValues.size() + 3);

        String[][] axisNumbers = determineAxisNumbers(graph, yminMax, xminMax );

        graph = addGraphDecorations(graph, axisNumbers, 16, yValues.size() + 3,  new String[]{xLabel, yLabel});

        display(graph);
    }

    private char[][] initialize(char[][] g) {
        for (int x = 0; x < g.length; x++) {
            Arrays.fill(g[x], ' ');
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
            Map<String, List<Double>> yValues,
            double[] yminMax,
            double[] xminMax)
    {
        double yRange = yminMax[1] - yminMax[0];
        double yStep = yRange / rows;

        double xStep = xminMax[1] / columns;

        final int[] c = {0};
        yValues.forEach((label, values) -> {
            for(int x = 1; x <= columns; x++) {
                int index = (int) Math.round(x * xStep);
                if (index <= values.size()) {
                    int y = getYValueForX(values, yStep, index);
                    g[x * 4][y] = graphIcons[c[0]];
                }
            }
            c[0] += 1;
        });

        return g;
    }

    private int getYValueForX(
            List<Double> yValues,
            double yStep,
            int index)
    {
        //10, 4
        // 1.25, .5
        //1, 4, 8

        //100, 200, 300, 400
        // 1, 1, 2, 2, 3, 4, 5, 6, 7, 8

//        double average = yValues.get(index - 1) +
//                ((yValues.size() > index)? yValues.get(index) : yValues.get(index - 1));
//        average /= 2.0;
        double average = yValues.get(index - 1);

        return (int) Math.round(average / yStep);
    }

    private char[][] insetGraphWithBorder(char[][] graph, int leftBorder, int bottomBorder) {
        char[][] newGraph = new char[leftBorder + graph.length][graph[0].length + bottomBorder];
        newGraph = initialize(newGraph);

        for(int x = 0; x < graph.length; x++) {
            for (int y = graph[0].length - 1; y >= 0; y--) {
                newGraph[x + leftBorder][y + bottomBorder] = graph[x][y];
            }
        }

        return newGraph;
    }

    private String[][] determineAxisNumbers(char[][] graph, double[] yminMax, double[] xminMax) {
        String[] yLabels = new String[5];
        String[] xLabels = new String[5];

        if (yminMax[1] > 1000000) {
            yminMax[0] = round(yminMax[0] / 1000, 1);
            yminMax[1] = round(yminMax[1] / 1000, 1);

            double yStep = (yminMax[1] - yminMax[0]) / 4;

            yLabels[0] = round(yminMax[0], 1) + "m";
            yLabels[1] = round(yminMax[0] + yStep, 1) + "m";
            yLabels[2] = round(yminMax[0] + yStep + yStep, 1) + "m";
            yLabels[3] = round(yminMax[1] - yStep, 1) + "m";
            yLabels[4] = round(yminMax[1], 1) + "m";
        }
        else if (yminMax[1] > 1000) {
            yminMax[0] = round(yminMax[0] / 1000, 1);
            yminMax[1] = round(yminMax[1] / 1000, 1);

            double yStep = (yminMax[1] - yminMax[0]) / 4;

            yLabels[0] = round(yminMax[0], 1) + "k";
            yLabels[1] = round(yminMax[0] + yStep, 1) + "k";
            yLabels[2] = round(yminMax[0] + yStep + yStep, 1) + "k";
            yLabels[3] = round(yminMax[1] - yStep, 1) + "k";
            yLabels[4] = round(yminMax[1], 1) + "k";
        }
        else {
            double yStep = (yminMax[1] - yminMax[0]) / 4;

            yLabels[0] = String.valueOf(round(yminMax[0], 1));
            yLabels[1] = String.valueOf(round(yminMax[0] + yStep, 1));
            yLabels[2] = String.valueOf(round(yminMax[0] + yStep + yStep, 1));
            yLabels[3] = String.valueOf(round(yminMax[1] - yStep, 1));
            yLabels[4] = String.valueOf(round(yminMax[1], 1));
        }



        double xStep = (xminMax[1] - xminMax[0]) / 5;
        xLabels[0] = String.valueOf(round(xminMax[0], 1));
        xLabels[1] = String.valueOf(round(xminMax[0] + xStep, 1));
        xLabels[2] = String.valueOf(round(xminMax[0] + xStep + xStep, 1));
        xLabels[3] = String.valueOf(round(xminMax[1] - xStep, 1));
        xLabels[4] = String.valueOf(round(xminMax[1], 1));

        return new String[][]{xLabels, yLabels};
    }

    private char[][] addGraphDecorations(char[][] graph, String[][] axisNumbers, int leftBorder, int bottomBorder, String[] xyLabels) {
        for (int label = 0; label < axisNumbers[1].length; label++) {
            String ylabel = axisNumbers[1][label];
            int l = ylabel.length();
            int index = leftBorder - l;
            for (int i = index; i < leftBorder; i++) {
                graph[i][label * 2 + bottomBorder] = ylabel.charAt(i - index);
            }
        }

        for (int label = 0; label < axisNumbers[0].length; label++) {
            String xLabel = axisNumbers[0][label];
            int index = leftBorder + label * 8;
            for (int i = 0; i < xLabel.length(); i++) {
                graph[index + i][bottomBorder - 1] = xLabel.charAt(i);
            }
        }

        return graph;
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
