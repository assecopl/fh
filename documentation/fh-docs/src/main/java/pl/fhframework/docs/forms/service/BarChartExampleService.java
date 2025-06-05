package pl.fhframework.docs.forms.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.time.Month;
import java.util.*;

/**
 * Created by k.czajkowski on 24.01.2017.
 */
@Service
public class BarChartExampleService {

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String ALL_EMPLOYEES = "All Employees";
    public static final String JAVA_DEVELOPER = "Java Developer";
    public static final String JAVA_SCRIPT_DEVELOPER = "JavaScript Developer";
    public static final String DOT_NET_DEVELOPER = ".Net Developer";

    private static Map<Object, Number> malesPerMonth;
    private static Map<Object, Number> femalesPerMonth;
    private Map<Object, Number> allemployeesPerMonth;
    private static List<ChartSeries> employeesPerMonth;

    static {
        malesPerMonth = new LinkedHashMap<>();
        malesPerMonth.put(Month.JANUARY, 12);
        malesPerMonth.put(Month.FEBRUARY, 12);
        malesPerMonth.put(Month.MARCH, 13);
        malesPerMonth.put(Month.APRIL, 12);
        malesPerMonth.put(Month.MAY, 15);
        malesPerMonth.put(Month.JUNE, 16);
        malesPerMonth.put(Month.JULY, 16);
        malesPerMonth.put(Month.AUGUST, 15);
        malesPerMonth.put(Month.SEPTEMBER, 17);
        malesPerMonth.put(Month.OCTOBER, 17);
        malesPerMonth.put(Month.NOVEMBER,21);
        malesPerMonth.put(Month.DECEMBER, 21);
        ChartSeries malesSeries = new ChartSeries();
        malesSeries.setLabel(MALE);
        malesSeries.setData(malesPerMonth);

        femalesPerMonth = new LinkedHashMap<>();
        femalesPerMonth.put(Month.JANUARY, 10);
        femalesPerMonth.put(Month.FEBRUARY, 10);
        femalesPerMonth.put(Month.MARCH, 9);
        femalesPerMonth.put(Month.APRIL, 9);
        femalesPerMonth.put(Month.MAY, 12);
        femalesPerMonth.put(Month.JUNE, 12);
        femalesPerMonth.put(Month.JULY, 13);
        femalesPerMonth.put(Month.AUGUST, 13);
        femalesPerMonth.put(Month.SEPTEMBER, 13);
        femalesPerMonth.put(Month.OCTOBER, 14);
        femalesPerMonth.put(Month.NOVEMBER,15);
        femalesPerMonth.put(Month.DECEMBER, 15);
        ChartSeries femalesSeries = new ChartSeries();
        femalesSeries.setLabel(FEMALE);
        femalesSeries.setData(femalesPerMonth);

        employeesPerMonth = new LinkedList<>();
        employeesPerMonth.add(malesSeries);
        employeesPerMonth.add(femalesSeries);
    }

    public List<ChartSeries> getEmployeesPerMonth() {
        return employeesPerMonth;
    }

    public List<ChartSeries> getAllEmployeesPerMonth() {
        allemployeesPerMonth = new LinkedHashMap<>();
        allemployeesPerMonth.put(Month.JANUARY, 1);
        allemployeesPerMonth.put(Month.FEBRUARY, 2);
        allemployeesPerMonth.put(Month.MARCH, 3);
        allemployeesPerMonth.put(Month.APRIL, 4);
        allemployeesPerMonth.put(Month.MAY, 5);
        allemployeesPerMonth.put(Month.JUNE, 6);
        allemployeesPerMonth.put(Month.JULY, 7);
        allemployeesPerMonth.put(Month.AUGUST, 8);
        allemployeesPerMonth.put(Month.SEPTEMBER, 9);
        allemployeesPerMonth.put(Month.OCTOBER, 10);
        allemployeesPerMonth.put(Month.NOVEMBER,11);
        allemployeesPerMonth.put(Month.DECEMBER, 12);
        ChartSeries employeesSeries = new ChartSeries();
        employeesSeries.setLabel(ALL_EMPLOYEES);
        employeesSeries.setData(allemployeesPerMonth);
        List<ChartSeries> employees = new LinkedList<>();
        employees.add(employeesSeries);
        return employees;
    }

    public List<NumberOfEmployees> getEmployeesPerYear(List<ChartSeries> employees) {
        List<NumberOfEmployees> numberOfEmployees = new ArrayList<>();
        for(ChartSeries chartSeries : employees) {
            String position = chartSeries.getLabel();
            Map<Object, Number> numberOfEmployeesPerYear = chartSeries.getData();
            for (Map.Entry<Object, Number> valueEntry : numberOfEmployeesPerYear.entrySet()) {
                Object year = valueEntry.getKey();
                Number count = valueEntry.getValue();
                numberOfEmployees.add(new NumberOfEmployees((Integer)year, (Integer)count, position));
            }
        }
        return numberOfEmployees;
    }

    public List<ChartSeries> getEmployeesPerYear() {
        List<ChartSeries> employees = new LinkedList<>();
        Map<Object, Number> javaEmployees = new LinkedHashMap<>();
        javaEmployees.put(2014, 0);
        javaEmployees.put(2015, 10);
        javaEmployees.put(2016, 20);
        ChartSeries javaSeries = new ChartSeries();
        javaSeries.setLabel(JAVA_DEVELOPER);
        javaSeries.setData(javaEmployees);
        employees.add(javaSeries);
        Map<Object, Number> jsEmployees = new LinkedHashMap<>();
        jsEmployees.put(2014, 10);
        jsEmployees.put(2015, 13);
        jsEmployees.put(2016, 17);
        ChartSeries jsSeries = new ChartSeries();
        jsSeries.setLabel(JAVA_SCRIPT_DEVELOPER);
        jsSeries.setData(jsEmployees);
        employees.add(jsSeries);
        Map<Object, Number> dotNetEmployees = new LinkedHashMap<>();
        dotNetEmployees.put(2014,10);
        dotNetEmployees.put(2015,12);
        dotNetEmployees.put(2016,15);
        ChartSeries dotNetSeries = new ChartSeries();
        dotNetSeries.setLabel(DOT_NET_DEVELOPER);
        dotNetSeries.setData(dotNetEmployees);
        employees.add(dotNetSeries);
        return employees;
    }

    public List<ChartSeries> getVehiclesPerYearPlusLine() {
        List<ChartSeries> prevSeries = getVehiclesPerYear();

        ChartSeries expectedSalesSeries = new ChartSeries();
        expectedSalesSeries.setLabel("Expected sales");
        expectedSalesSeries.setType("line");

        Map<Object, Number> expectedSalesData = new LinkedHashMap<>();
        expectedSalesData.put(2013, 100);
        expectedSalesData.put(2014, 120);
        expectedSalesData.put(2015, 150);
        expectedSalesData.put(2016, 170);
        expectedSalesSeries.setFill(false);
        expectedSalesSeries.setData(expectedSalesData);

        List<ChartSeries> result = new ArrayList<>();
        result.add(expectedSalesSeries);
        result.addAll(prevSeries);

        return result;
    }

    public List<ChartSeries> getVehiclesPerYear() {
        List<ChartSeries> vehiclesPerYear = new LinkedList<>();
        Map<Object, Number> cars = new LinkedHashMap<>();
        cars.put(2013, 100);
        cars.put(2014, 120);
        cars.put(2015, 150);
        cars.put(2016, 170);
        ChartSeries carsSeries = new ChartSeries();
        carsSeries.setLabel("Cars");
        carsSeries.setData(cars);
        vehiclesPerYear.add(carsSeries);

        Map<Object, Number> motorbikes = new LinkedHashMap<>();
        motorbikes.put(2013, 35);
        motorbikes.put(2014, 40);
        motorbikes.put(2015, 60);
        motorbikes.put(2016, 90);
        ChartSeries motorbikesSeries = new ChartSeries();
        motorbikesSeries.setLabel("Motorbikes");
        motorbikesSeries.setData(motorbikes);
        vehiclesPerYear.add(motorbikesSeries);

        Map<Object, Number> bicycles = new LinkedHashMap<>();
        bicycles.put(2013, 275);
        bicycles.put(2014, 290);
        bicycles.put(2015, 310);
        bicycles.put(2016, 350);
        ChartSeries bicyclesSeries = new ChartSeries();
        bicyclesSeries.setLabel("Bicycles");
        bicyclesSeries.setData(bicycles);
        vehiclesPerYear.add(bicyclesSeries);

        Map<Object, Number> trucks = new LinkedHashMap<>();
        trucks.put(2013, 10);
        trucks.put(2014, 12);
        trucks.put(2015, 12);
        trucks.put(2016, 15);
        ChartSeries trucksSeries = new ChartSeries();
        trucksSeries.setLabel("Trucks");
        trucksSeries.setData(bicycles);
        vehiclesPerYear.add(trucksSeries);
        return vehiclesPerYear;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class NumberOfEmployees {
        private int year;
        private int count;
        private String position;
    }
}
