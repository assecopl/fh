package pl.fhframework.model.forms.widgets.service;

import pl.fhframework.model.forms.widgets.model.WidgetInfo;

import java.util.List;

/**
 * Created by szkiladza on 22.02.2017.
 */
public interface IWidgetInfoService {

    void deleteAll(long userId);

    List<WidgetInfo> findByUserId(long userId) ;

    Iterable<WidgetInfo> save(Iterable<WidgetInfo> widgetInfos);

    Iterable<WidgetInfo> saveDashboardState(long userId, Iterable<WidgetInfo> widgetInfos) ;
}
