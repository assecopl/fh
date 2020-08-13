package pl.fhframework.model.forms;

import pl.fhframework.core.forms.IterationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.ComponentsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created by krzysztof.kobylarek on 2016-12-01.
 */
public class BaseComponentUtils {

    // backward compatibilty - just delegate to ComponentsUtils
    public static FormElement find(FormElement component, String id){
        return ComponentsUtils.find(component, id);
    }

    public static Optional<Form<?>> findForm(FormElement component){
        IGroupingComponent group = component.getGroupingParentComponent();
        while (group!=null){
            if (group instanceof Form){
                return Optional.of((Form)group);
            }
            group=((FormElement)group).getGroupingParentComponent();
        }
        return Optional.empty();
    }

    public static Optional<Integer> getIterationIndex(Component element) {
        Optional<IterationContext> ctx  = getIterationContext(element);
        if (ctx.isPresent())
            return Optional.of(ctx.get().getIndex());
        else
            return Optional.empty();
    }

    public static Optional<IterationContext> getIterationContext(Component element) {
        IGroupingComponent<?> c = null;
        if (element instanceof IGroupingComponent){
            c = (IGroupingComponent<?>) element;
        } else if (element.getGroupingParentComponent()==null){
            return Optional.empty();
        } else {
            c = element.getGroupingParentComponent();
        }
        while (c != null) {
            if (c instanceof Repeater){
                if (((Repeater)c).getBindedSubcomponents()!=null){
                    IterationContext currentContext = null;
                    for (IterationContext ctx : ((Repeater)c).getBindedSubcomponents()) {
                        if (ctx.getComponent() instanceof FormElement) {
                            if (ComponentsUtils.find((FormElement) ctx.getComponent(), element.getId()) != null) {
                                currentContext = ctx;
                            }
                        }
                    }
                    if (currentContext!=null)
                        return Optional.of(currentContext);
                } else return Optional.empty();
            }
            c = ((FormElement)c).getGroupingParentComponent();
        }
        return Optional.empty();

    }

    public static  void invokeMethod(Object target, Method m, Object params){
        try {
            m.invoke(target, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            FhLogger.error(e);
        }
    }
}