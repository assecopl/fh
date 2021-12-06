package pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour;

import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;

/**
 * Created by pawel.ruta on 2017-04-10.
 */
public interface Activity<T extends Parental> extends Child<T> {
    ActivityTypeEnum getActivityType();

    String getTargetName();

    String getCondition();

    void setCondition(String string);
}
