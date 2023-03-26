package com.freedommuskrats.fineengine.dal.models.comparison;

import com.freedommuskrats.fineengine.dal.models.TimeUnit;
import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import com.freedommuskrats.fineengine.dal.models.property.Apartment;
import com.freedommuskrats.fineengine.dal.models.property.Home;
import com.freedommuskrats.fineengine.service.projections.ProjectionLine;
import com.freedommuskrats.fineengine.util.TerminalGraph;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.freedommuskrats.fineengine.util.GeneralUtil.formatPrint;
import static com.freedommuskrats.fineengine.util.GeneralUtil.print;


@Entity
@Data
public class CompositePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compositePlanId;

    private int planLengthYears;

    @OneToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Fund> funds;

    @OneToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Home> homes;

    @OneToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Apartment> apartments;

    private StringBuilder builder;

    public CompositePlan(){}


    @Builder
    public CompositePlan(int planLengthYears, Fund fund, Home home, Apartment apartment, StringBuilder stringBuilder) {
        this.planLengthYears = planLengthYears;
        funds = new ArrayList<>(List.of(fund));
        homes = new ArrayList<>(List.of(home));
        apartments  = new ArrayList<>(List.of(apartment));
        this.builder = stringBuilder;
    }


    public CompositePlan(List<Home> homes, List<Fund> funds) {
        this.homes = homes;
        this.funds = funds;
    }


    public void displayBasic(String resultFile) throws IOException {
        Home home = homes.get(0);
        Fund fund = funds.get(0);
        Apartment apartment = apartments.get(0);

        Summary homeSummary = home.getSummary(
                planLengthYears - apartment.getYearsInApartment(),
                (planLengthYears < home.getMortgage().getTermYearsLeft()));
        if (planLengthYears < home.getMortgage().getTermYearsLeft())
            builder.append("Assuming home sale since plan length is less than mortgage length");

        double monthlyPayment = home.getMinMonthlyMortgagePayment();

        Summary eftSummary = fund.getSummary(planLengthYears, false);

        double apartmentProfit =  apartment.getMonthlyPayment() * 12 * apartment.getYearsInApartment();
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() - apartmentProfit;

        builder.append("\n");

        builder.append("*********Results**************");
        builder.append("\n");
        builder.append(String.format("-home profit %s", homeSummary));
        builder.append("\n");
        builder.append(String.format("-min monthly payment %s", monthlyPayment));
        builder.append("\n");
        builder.append(String.format("-down payment %s", home.getDownPayment()));
        builder.append("\n");
        builder.append(String.format("-fund interest %s", eftSummary));
        builder.append("\n");
        builder.append("-home schedule");
        builder.append("\n");
        builder.append(home.getContributionSchedule());
        builder.append("\n");
        builder.append("-fund schedule");
        builder.append("\n");
        builder.append(fund.getContributionSchedule());
        builder.append("\n");
        builder.append(String.format("-profit minus debt %s", total));
        builder.append("\n");
        builder.append("\n");

        List<Double> debt = home.getMonthlyMortgagePaymentSchedule(home.getContributionSchedule())
                .getLines().stream()
                .map(ProjectionLine::getEndBalance)
                .toList();

        List<Double> interest = home.getMonthlyMortgagePaymentSchedule(home.getContributionSchedule())
                .getLines().stream()
                .map(ProjectionLine::getInterest)
                .toList();

        List<Double> investment = fund.makeProjection(
                fund.getCurrentValue(),
                fund.getContributionSchedule(),
                fund.getContributionPeriod(),
                false,
                TimeUnit.YEAR).getLines().stream()
                .map(ProjectionLine::getEndBalance)
                .toList();

        builder.append("****************DEBT**********************");
        builder.append("\n");
        TerminalGraph debtGraph = new TerminalGraph(null, null, debt);
        builder.append(debtGraph.display());

        builder.append("****************LOAN_INTEREST**********************");
        builder.append("\n");
        TerminalGraph debtInterest = new TerminalGraph(null, null, interest);
        builder.append(debtInterest.display());

        builder.append("****************INVESTMENT**********************");
        builder.append("\n");
        TerminalGraph debtInvestment = new TerminalGraph(null, null, investment);
        builder.append(debtInvestment.display());

        System.out.println(builder.toString());

        File file = new File(resultFile);
        FileWriter writer = new FileWriter(file);
        writer.write(builder.toString());
        writer.close();

    }


}
