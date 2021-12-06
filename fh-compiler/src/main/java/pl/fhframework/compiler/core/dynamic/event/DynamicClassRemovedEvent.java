package pl.fhframework.compiler.core.dynamic.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.core.dynamic.DynamicClassName;

/**
 * Created by pawel.ruta on 2017-10-19.
 */
public class DynamicClassRemovedEvent extends ApplicationEvent {

    @Getter
    private DynamicClassArea dynamicClassArea;

    /**
     * Creates new dynamic class removing event, when artifact has been removed
     * @param dynamicClassName name/id of dynamic class
     * @param dynamicClassArea class erea
     */
    public DynamicClassRemovedEvent(DynamicClassName dynamicClassName, DynamicClassArea dynamicClassArea) {
        super(dynamicClassName);
        this.dynamicClassArea = dynamicClassArea;
    }

    public DynamicClassName getDynamicClassName() {
        return (DynamicClassName) getSource();
    }
}
