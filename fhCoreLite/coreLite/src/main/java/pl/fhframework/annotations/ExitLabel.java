package pl.fhframework.annotations;

import java.lang.annotation.Repeatable;

/**
 * Created by Gabriel on 17.01.2016.
 */
@Repeatable(value = MultipleExitLabels.class)
public @interface ExitLabel {
    int exitNumber() default -1;
    String value();
}
