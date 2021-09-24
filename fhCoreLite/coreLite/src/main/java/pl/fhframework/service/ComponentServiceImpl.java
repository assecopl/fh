package pl.fhframework.service;

import org.springframework.stereotype.Service;
import pl.fhframework.annotations.Control;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.IGroupingComponent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Adam Zareba on 28.02.2017.
 */
@Service
public class ComponentServiceImpl implements ComponentService {

    public static boolean isPossibleToBeAddedForParent(Component component, Class<?> parentClass) {
        return isPossibleToBeAddedForParent(component.getClass(), parentClass);
    }

    public static boolean isPossibleToBeAddedForParent(Class<? extends Component> componentClass, Class<?> parentClass) {
        Class<?> filteredParentClass = Form.class.isAssignableFrom(parentClass) ? Form.class : parentClass;
        Control formElementAnnotation = componentClass.getAnnotation(Control.class);
        if(formElementAnnotation == null) {
            return true;
        }
        List<? extends Class<?>> parents = Arrays.asList(formElementAnnotation.parents());

        boolean matches = false;
        if (parents.size() == 0 || (parents.size() == 1 && parents.contains(Object.class))) {
            matches = true;
        } else {
            for (Class parent : parents) {
                if (parent.isAssignableFrom(filteredParentClass)) {
                    matches = true;
                }
            }
        }
        for (Class invalidParent : formElementAnnotation.invalidParents()) {
            if (invalidParent.isAssignableFrom(filteredParentClass)) {
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public <T> T getComponent(IGroupingComponent<? extends Component> groupingComponent, String subComponentId, Class<T> subComponentClass) {
        for (Component subComponent : groupingComponent.getSubcomponents()) {
            if (subComponent instanceof IGroupingComponent) {
                if (subComponentId.equals(subComponent.getId()) && subComponentClass.isAssignableFrom(subComponent.getClass())) {
                    return (T) subComponent;
                } else {
                    T component = (T) getComponent(((IGroupingComponent) subComponent), subComponentId, subComponentClass);
                    if (component != null) {
                        return component;
                    }
                }
            } else {
                if (subComponentId.equals(subComponent.getId()) && subComponent.getClass().isAssignableFrom(subComponentClass)) {
                    return (T) subComponent;
                }
            }
        }

        return null;
    }


}
