package pl.fhframework.docs.widgets;

import pl.fhframework.model.forms.statistics.model.WidgetStatistic;
import pl.fhframework.model.forms.statistics.service.WidgetStatisticsService;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
@Getter
@Setter
public class WidgetStatisticsModel {

    private List<WidgetStatistic> statistics;

    public WidgetStatisticsModel(WidgetStatisticsService widgetStatisticsService) {
        if (widgetStatisticsService != null) {
            this.statistics = widgetStatisticsService.findAll();
        } else {
            this.statistics = null;
        }
    }
}
