package pl.fhframework.docs.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.model.forms.statistics.service.WidgetStatisticsService;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-widget-stats")
public class WidgetStatisticsUC implements IInitialUseCase {

    @Autowired(required = false)
    private WidgetStatisticsService widgetStatisticsService;

    private WidgetStatisticsModel model;

    @Override
    public void start() {
        model = new WidgetStatisticsModel(widgetStatisticsService);
        showForm(WidgetStatisticsForm.class, model);
    }
}
