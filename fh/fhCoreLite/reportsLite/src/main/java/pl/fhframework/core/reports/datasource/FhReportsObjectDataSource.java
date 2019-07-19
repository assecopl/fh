package pl.fhframework.core.reports.datasource;

import java.util.*;

/**
 * Created by pawel.ruta on 2017-12-11.
 */
public class FhReportsObjectDataSource extends FhReportsFillSourceImpl<Object, Object> {
    public FhReportsObjectDataSource(Object reportModel, Iterator iterator) {
        super(reportModel, iterator);
    }

}
