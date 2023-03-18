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

    public CompositePlan(){}


    @Builder
    public CompositePlan(int planLengthYears, Fund fund, Home home, Apartment apartment) {
        this.planLengthYears = planLengthYears;
        funds = new ArrayList<>(List.of(fund));
        homes = new ArrayList<>(List.of(home));
        apartments  = new ArrayList<>(List.of(apartment));
    }


    public CompositePlan(List<Home> homes, List<Fund> funds) {
        this.homes = homes;
        this.funds = funds;
    }


    public void displayBasic() {
        Home home = homes.get(0);
        Fund fund = funds.get(0);
        Apartment apartment = apartments.get(0);

        Summary homeSummary = home.getSummary(
                planLengthYears - apartment.getYearsInApartment(),
                (planLengthYears < home.getMortgage().getTermYearsLeft()));
        if (planLengthYears < home.getMortgage().getTermYearsLeft())
            print("Assuming home sale since plan length is less than mortgage length");

        double monthlyPayment = home.getMinMonthlyMortgagePayment();

        Summary eftSummary = fund.getSummary(planLengthYears, false);

        double apartmentProfit =  apartment.getMonthlyPayment() * 12 * apartment.getYearsInApartment();
        double total = homeSummary.profitOrCost() - homeSummary.debt() + eftSummary.profitOrCost() - apartmentProfit;

        print();

        print("*********Results**************");
        formatPrint("-home profit %s", homeSummary);
        formatPrint("-min monthly payment %s", monthlyPayment);
        formatPrint("-down payment %s", home.getDownPayment());
        formatPrint("-fund profit %s", eftSummary);
        print("-home schedule");
        print(home.getContributionSchedule());
        print("-fund schedule");
        print(fund.getContributionSchedule());
        formatPrint("-profit minus debt %s", total);
        print();

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

        print("****************DEBT**********************");
        TerminalGraph debtGraph = new TerminalGraph(null, null, debt);

        print("****************LOAN_INTEREST**********************");
        TerminalGraph debtInterest = new TerminalGraph(null, null, interest);

        print("****************INVESTMENT**********************");
        TerminalGraph debtInvestment = new TerminalGraph(null, null, investment);


    }


}
