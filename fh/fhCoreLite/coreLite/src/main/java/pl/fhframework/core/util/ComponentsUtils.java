package pl.fhframework.core.util;

import pl.fhframework.core.forms.IterationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormElement;
import pl.fhframework.model.forms.GroupingComponent;
import pl.fhframework.model.forms.IGroupingComponent;
import pl.fhframework.model.forms.IRepeatable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by krzysztof.kobylarek on 2016-12-01.
 */
public class ComponentsUtils {

    private static List<FormElement> filterFormElements(List<? extends Component> list) {
        List newList = new ArrayList<>();
        for (Component c : list) {
            if (c instanceof FormElement) {
                newList.add(c);
            }
        }
        return newList;
    }

    public static FormElement find(FormElement component, String id) {
        if (component.getId().equals(id)) {
            return component;
        } else if (component instanceof IGroupingComponent) {
            IGroupingComponent<? extends Component> groupingComponent = (IGroupingComponent) component;
            for (Component _component : groupingComponent.getSubcomponents()) {
                if (FormElement.class.isInstance(_component) && id.equals(_component.getId()))
                    return (FormElement) _component;
            }
            for (Component _component : groupingComponent.getSubcomponents()) {
                if (FormElement.class.isInstance(_component)) {
                    FormElement c = find((FormElement) _component, id);
                    if (c != null)
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Not restricted search. Looks for first component with given id.
     *
     * @param component -root component
     * @param id        - id of looking component
     * @return null if component was not found.
     */
    public static Component find(Component component, String id) {
        if (component.getId().equals(id)) {
            return component;
        } else if (component instanceof IGroupingComponent) {
            IGroupingComponent<? extends Component> groupingComponent = (IGroupingComponent) component;
            for (Component _component : groupingComponent.getSubcomponents()) {
                if (Component.class.isInstance(_component) && id.equals(_component.getId()))
                    return _component;
            }
            for (Component _component : groupingComponent.getSubcomponents()) {
                if (Component.class.isInstance(_component)) {
                    Component c = find(_component, id);
                    if (c != null)
                        return c;
                }
            }
        }
        return null;
    }

    public static void findByExpression(FormElement component, Predicate<FormElement> predicate, List<FormElement> found) {
        if (predicate.test(component)) {
            found.add(component);
        } else if (component instanceof GroupingComponent) {
            GroupingComponent<FormElement> groupingComponent = (GroupingComponent) component;
            for (FormElement _component : filterFormElements(groupingComponent.getSubcomponents())) {
                findByExpression(_component, predicate, found);
            }
        } else if (component instanceof IRepeatable) {
            IRepeatable repeater = (IRepeatable) component;
            for (IterationContext context : repeater.getBindedSubcomponents()) {
                if (context.getComponent() instanceof FormElement) {
                    FormElement _component = (FormElement) context.getComponent();
                    findByExpression(_component, predicate, found);
                }
            }
        }
    }

    public static Optional<Form<?>> findForm(FormElement component) {
        IGroupingComponent group = component.getGroupingParentComponent();
        while (group != null) {
            if (group instanceof Form) {
                return Optional.of((Form) group);
            }
            group = ((FormElement) group).getGroupingParentComponent();
        }
        return Optional.empty();
    }

    public static void invokeMethod(Object target, Method m, Object params) {
        try {
            m.invoke(target, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            FhLogger.error(e);
        }
    }
}