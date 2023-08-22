package pl.fhframework.docs.forms.service;

import org.springframework.stereotype.Service;

import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam Zareba on 03.02.2017.
 */
@Service
public class CommissionService {

    public List<ChartSeries> getProvisionsMockData() {
        List<ChartSeries> mockData = new LinkedList<>();
        Map<Object, Number> commissionSeries = new LinkedHashMap<>();
        commissionSeries.put("I", 270);
        commissionSeries.put("II", 320);
        commissionSeries.put("III", 650);
        commissionSeries.put("IV", 975);
        commissionSeries.put("V", 870);
        commissionSeries.put("VI", 700);
        commissionSeries.put("VII", 605);
        commissionSeries.put("VIII", 610);
        commissionSeries.put("IX", 430);
        commissionSeries.put("X", 150);
        commissionSeries.put("XI", 100);
        commissionSeries.put("XII", 50);
        ChartSeries series = new ChartSeries();
        series.setLabel("Commission");
        series.setData(commissionSeries);
        mockData.add(series);

        return mockData;
    }

    public List<ChartSeries> getDollarExchangeRateMockData() {
        List<ChartSeries> mockData = new LinkedList<>();
        Map<Object, Number> dolarSeries = new LinkedHashMap<>();
        dolarSeries.put("I", 10);
        dolarSeries.put("II", 12);
        dolarSeries.put("III", 16);
        dolarSeries.put("IV", 10);
        dolarSeries.put("V", 22);
        dolarSeries.put("VI", 33);
        dolarSeries.put("VII", 21);
        dolarSeries.put("VIII", 50);
        dolarSeries.put("IX", 55);
        dolarSeries.put("X", 69);
        dolarSeries.put("XI", 76);
        dolarSeries.put("XII", 80);
        ChartSeries series = new ChartSeries();
        series.setLabel("Dollar exchange rate");
        series.setData(dolarSeries);
        mockData.add(series);

        return mockData;
    }

    public List<ChartSeries> getPopulationRateMockData() {
        List<ChartSeries> mockData = new LinkedList<>();
        Map<Object, Number> populationSeries = new LinkedHashMap<>();
        populationSeries.put("Bialystok", 300);
        populationSeries.put("Warsaw", 2300);
        populationSeries.put("Krakow", 1800);
        populationSeries.put("New York", 20000);
        populationSeries.put("Berlin", 5000);
        populationSeries.put("Tokio", 5000);
        ChartSeries series = new ChartSeries();
        series.setLabel("Population");
        series.setData(populationSeries);
        mockData.add(series);

        return mockData;
    }
}
