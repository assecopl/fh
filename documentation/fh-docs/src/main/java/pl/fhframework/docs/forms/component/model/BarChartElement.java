package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.service.BarChartExampleService;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by k.czajkowski on 24.01.2017.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BarChartElement extends ComponentElement {
    private List<ChartSeries> barChartExampleData;
    private List<ChartSeries> allEmployeesPerMonthExampleData;
    private String barChartTitle = "Employees per month";
    private String axisYLabel = "Number of employees";
    private String axisXLabel = "Month";
    private Number axisYIntegerMin = new Integer(0);
    private Number axisYIntegerMax = new Integer(30);
    private List<ChartSeries> vehiclesPerYear;
    private List<ChartSeries> vehiclesPerYearPlusLine;
    private boolean stacked = true;

    private List<ChartSeries> employeesPerYearSeries;
    private List<BarChartExampleService.NumberOfEmployees> employeesPerYearList;

    public BarChartElement (BarChartExampleService service) {
        barChartExampleData = service.getEmployeesPerMonth();
        allEmployeesPerMonthExampleData = service.getAllEmployeesPerMonth();
        employeesPerYearSeries = service.getEmployeesPerYear();
        employeesPerYearList = service.getEmployeesPerYear(employeesPerYearSeries);
        vehiclesPerYear = service.getVehiclesPerYear();
        vehiclesPerYearPlusLine = service.getVehiclesPerYearPlusLine();
    }
}
