package pl.fhframework.core.reports.datasource;

import net.sf.jasperreports.engine.JRRewindableDataSource;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by pawel.ruta on 2017-11-09.
 */
public interface FhReportsFillSource<T, I> extends JRRewindableDataSource {
    Map<String, Object> getValues();

    T getReportModel();

    Iterator<I> getIterator();

    I getRow();
}
