package com.freedommuskrats.fineengine.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.freedommuskrats.fineengine.ai.GeneticInstance.breed;
import static com.freedommuskrats.fineengine.util.GeneralUtil.randI;

public class GeneticAlgoAi {

    public static final int PLAN_LENGTH = 40;
    public static final int MONTHLY_INCOME = 3500;
    public static final double FUND_RETURN_RATE = 7.0;
    public static final int HOUSE_PRICE = 240000;
    public static final int MAX_DOWN_PAYMENT = 30000;
    public static final double HOUSE_APPRECIATION = 5.0;
    public static final double MORTGAGE_RATE = 5.0;
    public static final double MONTHLY_INSURANCE = 125;
    public static final double MONTHLY_PMI = 120;
    public static final double TAX_RATE = 1.2;
    public static final double MONTHLY_HOA = 0;
    public static final double YEARLY_UP_KEEP = 4000;


    public static final int NUMBER_BEST_TO_KEEP = 5;
    public static final int POPULATION = 50;
    public static final int ITERATIONS = 1000;

    public static void main(String[] args) throws IOException {
        List<GeneticInstance> saves = new ArrayList<>();
        List<GeneticInstance> specimens = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            GeneticInstance start = new GeneticInstance();
            start.init();
            start.build();
            specimens.add(start);
        }

        for (int i = 0; i < ITERATIONS; i++) {
            //breed
            for (int j = 0; j+1 < specimens.size(); j+=2) {
                specimens.add(
                        breed(specimens.get(j), specimens.get(j+1))
                );
            }

            //add a random
            GeneticInstance start = new GeneticInstance();
            start.init();
            start.build();
            specimens.add(start);

            //kill off random portion
            double fitness = 0.8 * specimens.get(randI(0, specimens.size() - 1)).getUtility();
            List<GeneticInstance> toRemove = new ArrayList<>();
            for (int j = 0; j < specimens.size(); j++) {
                if (specimens.get(j).getUtility() < fitness) {
                    toRemove.add(specimens.get(j));
                }
            }
            specimens.removeAll(toRemove);

            // save the best
            GeneticInstance best = specimens.stream().max(Comparator.comparing(GeneticInstance::getUtility)).orElse(null);
            saves.add(best);
            if( i % 100 == 0) System.out.println(best.getUtility());

            // kill off until reaching size
            if (specimens.size() > POPULATION) {
                specimens = specimens.subList(0, POPULATION);
            }

        }

        GeneticInstance best = saves.stream().max(Comparator.comparing(GeneticInstance::getUtility)).orElse(null);
        best.getCompositePlan().displayBasic("src/main/resources/Result.txt");
        System.out.println(best);

        System.out.println("******Saves******");
        for (int i = 0; i < 4; i++) {
            System.out.println(saves.get(randI(0, saves.size())).toString());
        }

    }



    private void testStuff() {
        GeneticInstance geneticInstance = new GeneticInstance();
        geneticInstance.init();

        double[] schedule = new double[50];
        Arrays.fill(schedule, 0.0);
        geneticInstance.setPercentSchedule(schedule);

        geneticInstance.setDownPayment(20000);
        geneticInstance.setInitialInvestment(0);
        geneticInstance.setLoanLength(30);

        geneticInstance.build();

        System.out.print(geneticInstance.getUtility());
    }

}
