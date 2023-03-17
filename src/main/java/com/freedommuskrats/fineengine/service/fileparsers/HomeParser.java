package com.freedommuskrats.fineengine.service.fileparsers;

import com.freedommuskrats.fineengine.dal.models.insurance.Insurance;
import com.freedommuskrats.fineengine.dal.models.loan.Loan;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import org.springframework.data.relational.core.sql.In;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeParser {
    public static final String HOUSE_APPRECIATION = "House Appreciation Rate =";
    public static final String MORTGAGE_RATE = "Mortgage Rate =";
    public static final String HOUSE_VALUE = "House Value =";
    public static final String LOAN_LENGTH = "Loan Length in Years =";
    public static final String MONTHLY_INSURANCE = "Monthly Insurance =";
    public static final String MONTHLY_PMI = "Monthly PMI =";
    public static final String TAX_RATE = "Property Tax Rate =";
    public static final String MONTHLY_HOA = "Monthy HOA Fee =";
    public static final String YEARLY_UP_KEEP = "Yearly Up Keep =";

    public static final List<String> fields = new ArrayList<>(List.of(
            HOUSE_APPRECIATION,
            MORTGAGE_RATE,
            HOUSE_VALUE,
            LOAN_LENGTH,
            MONTHLY_INSURANCE,
            MONTHLY_PMI,
            TAX_RATE,
            MONTHLY_HOA,
            YEARLY_UP_KEEP)
    );

    public static Home parseHomeBasic(String fileLocation) throws FileNotFoundException {
        Map<String, Object> extractedFields = parseHomeFields(fileLocation);
        return Home.builder()
                .yearlyReturnRate((Double) extractedFields.get(HOUSE_APPRECIATION))
                .mortgage(Loan.builder()
                        .loanAmount((Double) extractedFields.get(HOUSE_VALUE))
                        .interestRate((Double) extractedFields.get(MORTGAGE_RATE))
                        .termYearsLeft((Double) extractedFields.get(LOAN_LENGTH))
                        .build()
                )
                .value((Double) extractedFields.get(HOUSE_VALUE))
                .homeInsurance(Insurance.builder()
                        .monthlyPayment((Double) extractedFields.get(MONTHLY_INSURANCE))
                        .build()
                )
                .pmi(Insurance.builder()
                        .monthlyPayment((Double) extractedFields.get(MONTHLY_PMI))
                        .build()
                )
                .propertyTaxRate((Double) extractedFields.get(TAX_RATE))
                .montlyHOAFee((Double) extractedFields.get(MONTHLY_HOA))
                .yearlyUpkeepCost((Double) extractedFields.get(YEARLY_UP_KEEP))
                .build();
    }

    public static Map<String, Object> parseHomeFields(String fileLocation) throws FileNotFoundException {
        Map<String, Object> extractedFields = new HashMap<>();

        File file = new File(fileLocation);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        reader.lines().forEach(line -> {
            String field = fields.stream().filter(line::contains).findFirst().orElse(null);

            if (field != null) {
                switch (field){
                    case HOUSE_APPRECIATION ->
                            extractedFields.put(HOUSE_APPRECIATION, houseAppreciationRate(line));
                    case MORTGAGE_RATE ->
                            extractedFields.put(MORTGAGE_RATE, parseMortgageRate(line));
                    case HOUSE_VALUE ->
                            extractedFields.put(HOUSE_VALUE, parseHouseValue(line));
                    case LOAN_LENGTH ->
                            extractedFields.put(LOAN_LENGTH, parseLoanLength(line));
                    case MONTHLY_INSURANCE ->
                            extractedFields.put(MONTHLY_INSURANCE, parseInsurance(line));
                    case MONTHLY_PMI ->
                            extractedFields.put(MONTHLY_PMI, parsePmi(line));
                    case TAX_RATE ->
                            extractedFields.put(TAX_RATE, parseTaxRate(line));
                    case MONTHLY_HOA ->
                            extractedFields.put(MONTHLY_HOA, parseMonthlyHoa(line));
                    case YEARLY_UP_KEEP ->
                            extractedFields.put(YEARLY_UP_KEEP, parseYearlyUpKeep(line));
                    default ->
                            extractedFields.put("NOTHING", "NOTHING");
                }
            }
        });

        return extractedFields;
    }

    private static Object houseAppreciationRate(String line) {
        String value = line.split("=")[1];
        return Double.parseDouble(value.split("%")[0]);
    }

    private static Object parseMortgageRate(String line) {
        String value = line.split("=")[1];
        return Double.parseDouble(value.split("%")[0]);
    }

    private static Object parseHouseValue(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Object parseLoanLength(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Object parseInsurance(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Object parsePmi(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Object parseTaxRate(String line) {
        String value = line.split("=")[1];
        return Double.parseDouble(value.split("%")[0]);
    }

    private static Object parseMonthlyHoa(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

    private static Object parseYearlyUpKeep(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }

}
