package pl.fhframework.model.forms;

import java.util.List;

/**
 * Created by Adam Zareba on 23.03.2017.
 */
public interface ITabular {

    List<? extends GroupingComponent> getColumns();
}
