package com.freedommuskrats.fineengine.service.fileparsers;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FundParser {

    public static final String YEARLY_RETURN_RATE = "Yearly Return Rate =";
    public static final String CURRENT_VALUE = "Current Value =";

    public static final List<String> fields = new ArrayList<>(List.of(
            YEARLY_RETURN_RATE,
            CURRENT_VALUE)
    );

    public static Fund parseFundBasic(String fileLocation) throws FileNotFoundException {
        Map<String, Object> extractedFields = parseFundFields(fileLocation);

        return Fund.builder()
                .value((Double) extractedFields.get(CURRENT_VALUE))
                .contributionFrequency(TimeUnit.MONTH)
                .yearlyReturnRate((Double) extractedFields.get(YEARLY_RETURN_RATE))
                .build();
    }

    public static Map<String, Object> parseFundFields(String fileLocation) throws FileNotFoundException {
        Map<String, Object> extractedFields = new HashMap<>();

        File file = new File(fileLocation);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        reader.lines().forEach(line -> {
            String field = fields.stream().filter(line::contains).findFirst().orElse(null);

            if (field != null) {
                switch (field){
                    case YEARLY_RETURN_RATE ->
                            extractedFields.put(YEARLY_RETURN_RATE, parseYearlyRate(line));
                    case CURRENT_VALUE ->
                            extractedFields.put(CURRENT_VALUE, parseCurrentValue(line));
                    default ->
                            extractedFields.put("NOTHING", "NOTHING");
                }
            }
        });

        return extractedFields;
    }

    private static Object parseYearlyRate(String line) {
        String value = line.split("=")[1];
        return Double.parseDouble(value.split("%")[0]);
    }

    private static Object parseCurrentValue(String line) {
        return Double.parseDouble(line.split("=")[1]);
    }


}
