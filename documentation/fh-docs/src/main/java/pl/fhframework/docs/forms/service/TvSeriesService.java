package pl.fhframework.docs.forms.service;

import pl.fhframework.docs.forms.model.tv.TvSeries;

import java.util.List;
import java.util.Map;

/**
 * Created by Adam Zareba on 25.01.2017.
 */
public interface TvSeriesService {

    List<TvSeries> findAll();

    Map<?, String> findAllColored();
}
