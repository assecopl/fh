package pl.fhframework.model.forms.statistics.service;

import pl.fhframework.model.forms.statistics.model.WidgetStatistic;

import java.util.List;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
public interface WidgetStatisticsService {

    List<WidgetStatistic> findAll();

    WidgetStatistic find(String name);

    void increment(String componentName);
}
