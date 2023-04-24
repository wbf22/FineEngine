package com.freedommuskrats.fineengine.service.fileparsers;

import com.freedommuskrats.fineengine.dal.models.comparison.CompositePlan;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.property.Apartment;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.util.GeneralUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static com.freedommuskrats.fineengine.util.AnnuityMath.buildMonthlyContributionSchedule;
import static com.freedommuskrats.fineengine.util.GeneralUtil.print;

public class CompositePlanParser {

    public static final String PLAN_LENGTH = "Plan Length Years =";
    public static final String MONTHLY_INCOME = "Monthly Available Income =";
    public static final String PURCHASE_YEAR = "House Purchase Year =";
    public static final String APARTEMENT_RENT = "Apartment Rent Before Home Purchase =";
    public static final String PERCENT_TO_HOME = "Percent Income To House Instead of Investment =";
    public static final String YEAR_FIELD = "year";

    public static final List<String> fields = new ArrayList<>(List.of(
            PLAN_LENGTH,
            MONTHLY_INCOME,
            PURCHASE_YEAR,
            APARTEMENT_RENT,
            PERCENT_TO_HOME,
            YEAR_FIELD)
    );

    public static CompositePlan parseCompositePlan(String fileLocation, Fund fund, Home home, boolean display) throws FileNotFoundException {
        Map<String, Object> extractedFields = parseCompositePlanFields(fileLocation);
        int planLength = (int) extractedFields.get(PLAN_LENGTH);
        double monthlyIncome = (double) extractedFields.get(MONTHLY_INCOME);
        int purchaseYear = (int) extractedFields.get(PURCHASE_YEAR);
        double apartmentRent = (double) extractedFields.get(APARTEMENT_RENT);
        double percentToHome = (double) extractedFields.get(PERCENT_TO_HOME);
        double[] percentSchedule = (double[]) extractedFields.get(YEAR_FIELD);

        int yearsInApartment = Math.min(purchaseYear, planLength);

        double downPayment = getDownPayment(monthlyIncome, percentSchedule, yearsInApartment);
        double loanAmount = home.getStartingValue() - downPayment;
        home.getMortgage().setLoanAmount(loanAmount);
        home.setDownPayment(downPayment);

        percentSchedule = replaceValuesToLowWithMinMortgagePayment(home, monthlyIncome, percentSchedule, yearsInApartment);

        List<Double> fundSchedule = createContributionSchedule(
                planLength, monthlyIncome, purchaseYear, apartmentRent, percentToHome,
                percentSchedule, home.getMortgage().getLoanLength(), true);

        double percentToApartment = 100 * apartmentRent / monthlyIncome;
        for (int i = 0; i < yearsInApartment; i++) {
            percentSchedule[i] = percentSchedule[i] - percentToApartment;
        }

        List<Double> homeSchedule = createContributionSchedule(
                planLength, monthlyIncome, purchaseYear, apartmentRent, percentToHome,
                percentSchedule, home.getMortgage().getLoanLength(), false);

        fund.setContributionSchedule(fundSchedule);

        home.setContributionSchedule(homeSchedule.subList(yearsInApartment * 12, homeSchedule.size()));

        Apartment apartment = Apartment.builder()
                .monthlyPayment(apartmentRent)
                .yearsInApartment(yearsInApartment)
                .build();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("*********Start Values**************");
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Plan Length Years = %s", planLength));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly Available Income = %s", monthlyIncome));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-House Purchase Year = %s", purchaseYear));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Apartment Rent Before Home Purchase = %s", apartmentRent));
        stringBuilder.append("\n");
        stringBuilder.append("Contribution Percentages House vs Investments (Adjusted to meet min mortgage payment and rent):");
        stringBuilder.append("\n");
        Arrays.stream(percentSchedule).sequential().forEach(d -> stringBuilder.append(GeneralUtil.round(d, 1) + ", "));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-House Appreciation Rate = %s", home.getYearlyReturnRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Mortgage Rate = %s", home.getMortgage().getYearlyInterestRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-House Value = %s", home.getStartingValue()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Mortgage Loan Amount = %s", home.getMortgage().getLoanAmount()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly Insurance =  %s", home.getHomeInsurance().getMonthlyPayment()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthly PMI = %s", home.getPmi().getMonthlyPayment()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Property Tax Rate = %s", home.getPropertyTaxRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Monthy HOA Fee = %s", home.getMontlyHOAFee()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Yearly Up Keep = %s", home.getYearlyUpkeepCost()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Yearly Return Rate = %s", fund.getYearlyReturnRate()));
        stringBuilder.append("\n");
        stringBuilder.append(String.format("-Current Value = %s", fund.getStartingValue()));
        stringBuilder.append("\n");




        return CompositePlan.builder()
                .planLengthYears(planLength)
                .fund(fund)
                .home(home)
                .apartment(apartment)
                .stringBuilder(stringBuilder)
                .build();
    }

    public static double getDownPayment(double monthlyIncome, double[] percentSchedule, int yearsInApartment) {
        double downPayment = 0;
        for (int i = 0; i < yearsInApartment; i++) {
            downPayment += monthlyIncome * 12 * percentSchedule[i] / 100;
        }
        return downPayment;
    }

    public static double[] replaceValuesToLowWithMinMortgagePayment(Home home, double monthlyIncome, double[] percentSchedule, int yearsInApartment) {
        double minPayment = home.getMinMonthlyMortgagePayment();
        double minPercentage = 100 * minPayment / monthlyIncome;
        for (int i = yearsInApartment; i < home.getMortgage().getLoanLength() + yearsInApartment; i++) {
            percentSchedule[i] = Math.max(minPercentage, percentSchedule[i]);
        }
        return percentSchedule;
    }

    public static List<Double> createContributionSchedule(
            int planLength,
            double monthlyIncome,
            int purchaseYear,
            double apartmentRent,
            double percentToHome,
            double[] percentSchedule,
            double mortgageLength,
            boolean flipPercentages)
    {
        int yearsInApartment = Math.min(purchaseYear, planLength);

        percentSchedule = (flipPercentages)?
                Arrays.stream(percentSchedule).sequential().map(d -> 1-(d/100)).toArray() :
                Arrays.stream(percentSchedule).sequential().map(d -> d/100).toArray();
        double percent = (flipPercentages)? 1-(percentToHome/100) : percentToHome / 100;

        double[] newPSchedule = new double[planLength];
        for (int i = 0; i < planLength; i++) {
            if (i > mortgageLength + yearsInApartment) {
                newPSchedule[i] = (flipPercentages) ? 1 : 0;
            }
            else {
                newPSchedule[i] = (i < percentSchedule.length)? percentSchedule[i] : percent;
            }
        }

        List<Double> yearsAndAmounts = new ArrayList<>();

        for (int i = 0; i < Math.min(yearsInApartment, planLength); i++) {
            yearsAndAmounts.add(monthlyIncome * newPSchedule[i]);
        }

        for (int i = yearsInApartment; i < Math.min(mortgageLength + yearsInApartment, planLength); i++) {
            yearsAndAmounts.add(monthlyIncome * newPSchedule[i]);
        }

        for (int i = (int) Math.round(mortgageLength + yearsInApartment); i < planLength; i++) {
            yearsAndAmounts.add(monthlyIncome * newPSchedule[i]);
        }

        return buildMonthlyContributionSchedule(yearsAndAmounts, planLength);
    }

    private static Map<String, Object> parseCompositePlanFields(String fileLocation) throws FileNotFoundException {
        Map<String, Object> extractedFields = new HashMap<>();

        File file = new File(fileLocation);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        reader.lines().forEach(line -> {
            String field = fields.stream().filter(line::contains).findFirst().orElse(null);

            if (field != null) {
                switch (field){
                    case PLAN_LENGTH ->
                            extractedFields.put(PLAN_LENGTH, parsePlanLength(line));
                    case MONTHLY_INCOME ->
                            extractedFields.put(MONTHLY_INCOME, parseMonthlyIncome(line));
                    case PURCHASE_YEAR ->
                            extractedFields.put(PURCHASE_YEAR, parsePurchaseYear(line));
                    case APARTEMENT_RENT ->
                            extractedFields.put(APARTEMENT_RENT, parseApartmentRent(line));
                    case PERCENT_TO_HOME ->
                            extractedFields.put(PERCENT_TO_HOME, parsePercentToHome(line));
                    case YEAR_FIELD ->
                            extractedFields.put(YEAR_FIELD,
                                    parseYearField(line, extractedFields.get(YEAR_FIELD))
                            );
                    default ->
                            extractedFields.put("NOTHING", "NOTHING");
                }
            }
        });

        return extractedFields;
    }
    private static Integer parsePlanLength(String line) {
        return Math.toIntExact(Math.round(Double.parseDouble(line.split("=")[1])));
    }

    private static Double parseMonthlyIncome(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Integer parsePurchaseYear(String line) {
        return Math.toIntExact(Math.round(Double.parseDouble(line.split("=")[1])));
    }

    private static Double parseApartmentRent(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Double parsePercentToHome(String line) {
        String value = line.split("=")[1];
        return Double.parseDouble(value.split("%")[0]);
    }

    private static double[] parseYearField(String line, Object currentList) {
        double[] list = new double[50];
        if (currentList != null) {
            list = (double[]) currentList;
        }
        else {
            Arrays.fill(list, 0.0);
            list[0] = -1.0;
        }

        int yearNumber = Math.toIntExact(Math.round(
                Double.parseDouble(
                        line.split("=")[0].split(" ")[1]
                )
        ));

        if (line.split("=").length > 1) {
            String value = line.split("=")[1];
            list[yearNumber - 1] = Double.parseDouble(value.split("%")[0]);
        }

        return list;
    }




}
