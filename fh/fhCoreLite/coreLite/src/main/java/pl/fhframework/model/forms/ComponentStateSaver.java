package pl.fhframework.model.forms;

import pl.fhframework.core.FhException;
import pl.fhframework.model.dto.ElementChanges;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2016-12-08.
 */
public class ComponentStateSaver implements Cloneable{

    private Map<Integer, String> previousSubcomponentsHashes = new LinkedHashMap<>();

    private boolean asFormElement(FormElement c){
        return c instanceof FormElement;
    }

    public void processComponentChange(IGroupingComponent<FormElement> groupingComponent, ElementChanges elementChanges){

        final List<Includeable> includeables = filter(groupingComponent.getSubcomponents(), Includeable.class);

        final List<Component> componentsFromInclude = includeables.stream().flatMap(i -> i.getIncludedComponents().stream())
                .collect(Collectors.toList());

        List<FormElement> subcomponents = filter(groupingComponent.getSubcomponents(), FormElement.class);
        subcomponents.addAll(filter(componentsFromInclude, FormElement.class));

        Map<Integer, FormElement> subcomponentsHashes =
                subcomponents.stream().collect(
                        Collectors.toMap(FormElement::hashCode, (FormElement component) -> component,
                                createDuplicateFormElementError(), LinkedHashMap::new));

        Map<Integer, FormElement> newComponentsHashes = subcomponentsHashes.keySet().stream()
                .filter(hash->!previousSubcomponentsHashes.containsKey(hash))
                .collect(Collectors.toMap(Function.identity(), hash-> subcomponentsHashes.get(hash),
                        createDuplicateFormElementError(), LinkedHashMap::new));

        Map<Integer, String> removedComponentsHashes = previousSubcomponentsHashes.keySet().stream()
                .filter(hash->!subcomponentsHashes.containsKey(hash))
                .collect(Collectors.toMap(Function.identity(), hash->previousSubcomponentsHashes.get(hash),
                        createDuplicateStringHashError(), LinkedHashMap::new));

        LinkedHashSet<FormElement> newComponentsNoPreviousComponent = new LinkedHashSet<>();
        Map<FormElement, LinkedHashSet<FormElement>> newComponentsExistsPreviousComponent = new LinkedHashMap<>();

        FormElement previousComponent = null;
        if (!newComponentsHashes.isEmpty()) {
            for (FormElement component : subcomponents) {
                Integer componentKey = component.hashCode();
                if (removedComponentsHashes.containsKey(componentKey))
                    continue;
                if (newComponentsHashes.containsKey(componentKey)) {
                    if (previousComponent == null) {
                        newComponentsNoPreviousComponent.add(component);
                    } else {
                        if (!newComponentsExistsPreviousComponent.containsKey(previousComponent)) {
                            newComponentsExistsPreviousComponent.put(previousComponent, new LinkedHashSet<>());
                        }
                        newComponentsExistsPreviousComponent.get(previousComponent).add(component);
                    }
                } else {
                    previousComponent = component;
                }
            }
        }

        if (!newComponentsNoPreviousComponent.isEmpty()){
            elementChanges.getAddedComponents().put("-", newComponentsNoPreviousComponent);
        }

        elementChanges.getAddedComponents().putAll(newComponentsExistsPreviousComponent.keySet().stream().collect(
                Collectors.toMap( (prevComponent)->prevComponent.getId(),
                        (prevComponent)->newComponentsExistsPreviousComponent.get(prevComponent) )));

        /*for (FormElement prevComponent : newComponentsExistsPreviousComponent.keySet()) {
            elementChanges.getAddedComponents().put(prevComponent.getId(), newComponentsExistsPreviousComponent.get(prevComponent));
        }*/

        elementChanges.getRemovedComponents().addAll(removedComponentsHashes.values());

        newComponentsHashes.values().forEach(this::setStopProcesingUpdateView);

        previousSubcomponentsHashes.clear();
        previousSubcomponentsHashes.putAll(subcomponentsHashes.entrySet().stream().collect(
                Collectors.toMap(entry->entry.getKey(), entry->entry.getValue().getId())));

    }

    private void setStopProcesingUpdateView(Component component){
        component.setStopProcessingUpdateView(true);
        if (component instanceof IGroupingComponent) {
            IGroupingComponent<FormElement> groupingComponent = IGroupingComponent.class.<Component>cast(component);
            filter(groupingComponent.getSubcomponents(), FormElement.class).forEach(this::setStopProcesingUpdateView);
        }
    }

    private static <T> List<T> filter(List<?> list, Class<T> filterTypeClass){
        List<T> ret = new LinkedList<>();
        for (Object o : list){
            if (filterTypeClass.isInstance(o)){
                ret.add( (T)o);
            }
        }
        return ret;
    }

    private BinaryOperator<FormElement> createDuplicateFormElementError() {
        return (FormElement c1, FormElement c2) -> {
            throw new FhException("Duplicate hash: " + c1.getId() + " : " + c2.getId());
        };
    }

    private BinaryOperator<String> createDuplicateStringHashError() {
        return (String s1, String s2) -> {
            throw new FhException("Duplicate hash: " + s1 + " : " + s2);
        };
    }
}
