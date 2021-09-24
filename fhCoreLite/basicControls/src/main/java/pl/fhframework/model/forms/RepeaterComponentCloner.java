package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ReflectionUtils;
import pl.fhframework.core.FhFormException;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.fhframework.model.forms.Repeater.RepeaterIterationContext;
import pl.fhframework.core.forms.iterators.ISingleIteratorComponentFactory;

/**
 * Created by krzysztof.kobylarek on 2016-11-09.
 * Copied from Repeater / modified interface by piotr.gmyz.
 */
public class RepeaterComponentCloner implements Cloneable, ISingleIteratorComponentFactory<Repeater> {

    @JsonIgnore
    private static Pattern bindingPatternGroup = Pattern.compile("(\\{[A-Za-z0-9_\\.\\[\\]\\(\\)\\,]+\\})+"); // one and more group of word/white characters in {}
    private static Pattern actionBindingPatternGroup = Pattern.compile("(\\([A-Za-z0-9_\\.]+\\)){1}"); // eg doAction(iterator1.iterator2)

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ClonedComponentHolder {
        Optional<Component> cloneable;
    }

    @Override
    public List<FormElement> createComponentsForIterator(Repeater myRepeater, IRowNumberOffsetSupplier rowNumberOffset, int index) {
        String collectionBinding = remSpecial(myRepeater.getCollection().getBindingExpression());

        List<FormElement> newBindedSubcomponents = new ArrayList<>();
        for (Component component : myRepeater.getRepeatedComponents()) {

            Optional<Component> clone = component.clone();

            if (clone.isPresent()) {
                clone.get().setGroupingParentComponent(myRepeater);
                ClonedComponentHolder holder = new ClonedComponentHolder(clone);
                processBindingDeep(myRepeater, holder, index, myRepeater.getIterator(), collectionBinding);
                if (holder.getCloneable().get() != clone.get())
                    holder.getCloneable().get().setGroupingParentComponent(myRepeater);
                newBindedSubcomponents.add((FormElement) holder.getCloneable().get());
            }
        }
        return newBindedSubcomponents;
    }

    protected Optional<String> processBinding(ClonedComponentHolder cloneHolder, String valueBindingOrig, Integer index, String iterator, String binding) {


        String valueBindingCopy = new String(valueBindingOrig);
        StringBuffer valueBindingResult = new StringBuffer();
        Matcher bindingMatcherGroup = bindingPatternGroup.matcher(valueBindingCopy);
        Matcher actionMatcherGroup = actionBindingPatternGroup.matcher(valueBindingCopy);

        Optional<Component> clone = cloneHolder.cloneable;

        boolean bindingProcessed = false; // binding processed if all groups are procesed
        if (clone.get() instanceof Repeater && bindingMatcherGroup.toMatchResult().groupCount() > 1) {
            throw new RuntimeException("Two or more bindings in Repeater are prohibited");
        }

        if (bindingMatcherGroup.find()) {
            bindingMatcherGroup.reset();
            while (bindingMatcherGroup.find()) {

                String valueBinding = bindingMatcherGroup.group(1);
                valueBinding = remSpecial(valueBinding);

                boolean iteratorOnPath = valueBinding.contains(iterator + ".");
                boolean endsWithIterator = valueBinding.endsWith(iterator);

                if (iteratorOnPath || endsWithIterator) {
                    if (iteratorOnPath) {
                        valueBinding = "{" + valueBinding.replace(iterator + ".", binding + "[" + index + "]" + ".") + "}";
                    } else if (endsWithIterator) {
                        valueBinding = "{" + valueBinding.replace(iterator, binding + "[" + index + "]") + "}";
                    }
                    bindingProcessed = true;
                    bindingMatcherGroup.appendReplacement(valueBindingResult, valueBinding);
                    if (clone.get() instanceof Repeater) {
                        Repeater.class.cast(clone.get()).iterationContext = new RepeaterIterationContext(index, binding, iterator);
                    }
                }
            }
            bindingMatcherGroup.appendTail(valueBindingResult);
        } else if (actionMatcherGroup.find()) {
            actionMatcherGroup.reset();
            while (actionMatcherGroup.find()) {
                String valueBinding = actionMatcherGroup.group(1);
                valueBinding = valueBinding.replace("(", "").replace(")", "");

                boolean iteratorOnPath = valueBinding.contains(iterator + ".");
                boolean endsWithIterator = valueBinding.endsWith(iterator);

                if (iteratorOnPath | endsWithIterator) {
                    if (iteratorOnPath) {
                        valueBinding = valueBinding.replace(iterator + ".", binding + "[" + index + "]" + ".");
                    } else if (endsWithIterator) {
                        valueBinding = valueBinding.replace(iterator, "(" + binding + "[" + index + "]" + ")");
                    }
                    bindingProcessed = true;
                    actionMatcherGroup.appendReplacement(valueBindingResult, valueBinding);
                    if (clone.get() instanceof Repeater) {
                        Repeater.class.cast(clone.get()).iterationContext = new RepeaterIterationContext(index, binding, iterator);
                    }
                }
            }
            actionMatcherGroup.appendTail(valueBindingResult);
        }
        return bindingProcessed ? Optional.of(valueBindingResult.toString()) : Optional.empty();
    }

    /*
     * Current assumption: binding have XMLProperty decorated field as String type. It begins with '{' and finish with '}'
     * freshClone - after cloning but before rebinding.
     */

    protected void processBindingDeep(Repeater myRepeater, ClonedComponentHolder cloneHolder, Integer index, String iterator, String binding) {
        Optional<Component> freshClone = cloneHolder.cloneable;

        String newComponentId = computeIdOfClonedElement(freshClone, index, myRepeater);

        boolean walkAndProcess = true;

        if (walkAndProcess) {
            // prepare
            ReflectionUtils.FieldCallback resolveBindingFieldCallback = createResolveBindingCallback(myRepeater, cloneHolder, index, iterator, binding);

            ReflectionUtils.FieldFilter resolveBindingFieldFilter = createResolveBindingFieldFilter();

            ReflectionUtils.FieldCallback changeClonedElementNameFieldCallback = createChangeClonedElementNameFieldCallback(freshClone, index, myRepeater);

            // add row number context
            freshClone.get().getBindingContext().getRowNumberBindingContexts().add(new RowNumberBindingContext(iterator, index + 1));
            // add repeaters own row number contexts
            freshClone.get().getBindingContext().getRowNumberBindingContexts().addAll(myRepeater.getBindingContext().getRowNumberBindingContexts());

            // Change names of iterated elements todo maybe it'll be better if we split to seperate callbacks.
            // fire
            ReflectionUtils.doWithFields(freshClone.get().getClass(), changeClonedElementNameFieldCallback, field -> Objects.equals("id", field.getName().toLowerCase()));

            ReflectionUtils.doWithFields(freshClone.get().getClass(), resolveBindingFieldCallback, resolveBindingFieldFilter);
        }

        if (walkAndProcess && freshClone.get() instanceof IGroupingComponent){
            List<Component> subcomponents =  ((IGroupingComponent) freshClone.get()).getSubcomponents();
            List<Component> subcomponentsCopy = new ArrayList<>(subcomponents);
            for (int i = 0; i < subcomponentsCopy.size(); ++i) {
                Component iterated = subcomponentsCopy.get(i);
                ClonedComponentHolder iteratedHolder = new ClonedComponentHolder(Optional.of(iterated));
                processBindingDeep(myRepeater, iteratedHolder, index, iterator, binding);
                subcomponents.remove(i);
                subcomponents.add(i, iteratedHolder.getCloneable().get());
            }
        }

    }

    private /*static*/ ReflectionUtils.FieldCallback createResolveBindingCallback(
            Repeater myRepeater,
            ClonedComponentHolder cloneHolder,
            Integer index, String iterator, String binding) {

        return field -> {

            Optional<Component> clone = cloneHolder.getCloneable();

            boolean isPrivate = !field.isAccessible();
            if (isPrivate) field.setAccessible(true);

            ModelBinding valueBindingOrigObj = null;
            String valueBindingOrig = null;

            if (field.isAnnotationPresent(RepeaterTraversable.class)) {
                if (FormElement.class.isAssignableFrom(field.getType())) {
                    Component componentToBeTraversed = Component.class.cast(ReflectionUtils.getField(field, clone.get()));
                    processBindingDeep(myRepeater,
                            new ClonedComponentHolder(Optional.of(componentToBeTraversed)),
                            index, iterator, binding);
                } else {
                    throw new FhFormException("Field: " + field.getType() + " of " + field.getType().getSimpleName() + " is not instance" +
                            "of FormElement");
                }
            } else if (ModelBinding.class.isAssignableFrom(field.getType())) {
                ModelBinding modelBinding = (ModelBinding) ReflectionUtils.getField(field, clone.get());
                if (modelBinding != null) {
                    valueBindingOrigObj = modelBinding;
                    valueBindingOrig = modelBinding.getBindingExpression();
                }
            } else {
                valueBindingOrig = (String) ReflectionUtils.getField(field, clone.get());
            }

            if (valueBindingOrig != null &&
                    (bindingPatternGroup.matcher(valueBindingOrig).find() || isAction(valueBindingOrig))) {

                Optional<String> bindingProcessed = processBinding(cloneHolder, valueBindingOrig, index, iterator, binding);

                // if it can't bind from current repeater, check it out.
                if (!bindingProcessed.isPresent()) {
                    IGroupingComponent<?> chainedGroup = myRepeater;
                    while (chainedGroup != null) {
                        if (chainedGroup instanceof Repeater) {
                            Repeater chainedRepeater = Repeater.class.cast(chainedGroup);
                            if (chainedRepeater.iterationContext != null) {
                                bindingProcessed = processBinding(cloneHolder,
                                        valueBindingOrig,
                                        chainedRepeater.iterationContext.iterationIndex,
                                        chainedRepeater.iterationContext.iterationIterator,
                                        chainedRepeater.iterationContext.iterationBinding);

                            }
                        }
                        // Binding found.
                        if (bindingProcessed.isPresent())
                            break;
                        else
                            chainedGroup = FormElement.class.cast(chainedGroup).getGroupingParentComponent();
                    }
                }

                if (bindingProcessed.isPresent()) {
                    if (ModelBinding.class.isAssignableFrom(field.getType())) {
                        ModelBinding modelBinding = (ModelBinding) ReflectionUtils.getField(field, clone.get());
                        if (!(valueBindingOrigObj instanceof StaticBinding)) {
                            ReflectionUtils.setField(field, clone.get(), new AdHocModelBinding<>(myRepeater.getForm(), clone.get(), bindingProcessed.get()));
                        }
                    } else {
                        ReflectionUtils.setField(field, clone.get(), bindingProcessed.get());
                    }

                }
            }
        };

    }

    private boolean isAction(String binding) {
        return binding.contains("(");
        //binding = binding.contains("(") ? binding.substring(0, binding.indexOf("(")) : binding;
        //return  actionMethods.contains(binding);
    }

    static ReflectionUtils.FieldFilter createResolveBindingFieldFilter() {
        return field ->
                !Modifier.isStatic(field.getModifiers())
                        && (field.getType().equals(String.class)
                        || field.isAnnotationPresent(RepeaterTraversable.class)
                        || field.getType().isAnnotationPresent(RepeaterTraversable.class))
                        /*&& field.isAnnotationPresent(XMLProperty.class)*/;
    }

    private static ReflectionUtils.FieldCallback createChangeClonedElementNameFieldCallback(Optional<Component> clone, Integer index, Repeater thisRepeater) {
        return field -> {
            String parentGluePath = "";
            IGroupingComponent<? extends Component> container = thisRepeater;
            while (container != null && !Objects.equals(container.getClass(), Form.class)) {
                if (container instanceof Repeater) {
                    Repeater parentRepeater = (Repeater) container;
                    parentGluePath = parentRepeater.getId() + "_" + parentGluePath;
                    break;
                }
                container = ((FormElement) container).getGroupingParentComponent();
            }
            if (parentGluePath.endsWith("_"))
                parentGluePath = parentGluePath.substring(0, parentGluePath.lastIndexOf("_"));

            boolean isPrivate = !field.isAccessible();
            if (isPrivate) field.setAccessible(true);
            String val = ReflectionUtils.getField(field, clone.get()).toString();

            ReflectionUtils.setField(field, clone.get(), val + (!parentGluePath.isEmpty() ? "_" + parentGluePath : "") + "_" + index + "");

            if (clone.get() instanceof FormElement)
                thisRepeater.getForm().addToElementIdToFormElement((FormElement) clone.get());

        };
    }

    private static String computeIdOfClonedElement(Optional<Component> originalComponent, Integer index, Repeater thisRepeater) {
        String parentGluePath = "";
        IGroupingComponent<? extends Component> container = thisRepeater;
        while (container != null && !Objects.equals(container.getClass(), Form.class)) {
            if (container instanceof Repeater) {
                Repeater parentRepeater = (Repeater) container;
                parentGluePath = parentRepeater.getId() + "_" + parentGluePath;
                break;
            }
            container = ((FormElement) container).getGroupingParentComponent();
        }
        if (parentGluePath.endsWith("_"))
            parentGluePath = parentGluePath.substring(0, parentGluePath.lastIndexOf("_"));


        return originalComponent.get().getId() + (!parentGluePath.isEmpty() ? "_" + parentGluePath : "") + "_" + index + "";

    }

    protected static String remSpecial(String binding) {
        return binding.replace("{", "").replace("}", "");
    }
}
