package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pl.fhframework.core.FhException;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.OptionsListDesignerPreviewProvider;
import pl.fhframework.model.forms.model.OptionsListElementModel;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.*;
import java.util.Iterator;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * List options control
 * Created by krzysztof.kobylarek on 2017-01-02.
 */

@Control(parents = {Form.class, PanelGroup.class, Repeater.class, Group.class}, invalidParents = {Table.class})
@DocumentedComponent(documentationExample = true, value = "One column list of options to be selected. ", icon = "fa fa-list-ul")
public class OptionsList extends GroupingComponent<OptionsListElement> implements IChangeableByClient, CompactLayout {

    private static final String ON_ICON_CLICK_ATTR = "onIconClick";
    private static final String ATTR_TITLE = "title";
    private static final String ON_CHANGE_ATTR = "onChange";
    private static final String ATTR_EMPTY_VALUE_LABEL = "emptyValueLabel";

    @Getter
    @Setter
    @XMLProperty(value = "values")
    @DesignerXMLProperty(commonUse = true, previewValueProvider = OptionsListDesignerPreviewProvider.class)
    @DocumentedComponentAttribute(value = "Collection of elements displayed on the list", type = String.class, boundable = true)
    @JsonIgnore
    private ModelBinding valuesBinding = null;

    //@JsonIgnore
    private Map<String, ModelElementWrapper> valuesCollection = null;

    @Getter
    @Setter
    @JsonIgnore
    @RepeaterTraversable
    @XMLProperty(value = ATTR_TITLE)
    @DocumentedComponentAttribute(value = "List title displayed as the first element of the list", type = String.class, boundable = true)
    private ModelBinding titleBinding = null;

    //@JsonIgnore
    private LinkedList<ModelElementWrapper> bindingCollection = null;

    @JsonProperty(value = ATTR_TITLE)
    private OptionsListElement titleLabel = null;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Defines if it possible to define empty element on the list")
    private boolean emptyValue;

    @Getter
    @Setter
    @JsonIgnore
    @RepeaterTraversable
    @XMLProperty(value = ATTR_EMPTY_VALUE_LABEL)
    @DocumentedComponentAttribute(value = "Label of empty value", type = String.class, boundable = true)
    private ModelBinding emptyValueBinding = null;

    @JsonProperty(value = "displayCheckbox")
    private boolean displayCheckbox = true;

    @Getter
    @XMLProperty
    @DocumentedComponentAttribute("Name of action which will be invoked after clicking on icon of element.")
    private ActionBinding onIconClick;

    @Getter
    @XMLProperty
    @DocumentedComponentAttribute("Name of action which will be invoked after clicking on element checkbox.")
    private ActionBinding onChange;

    public OptionsList(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (titleBinding != null) {
            BindingResult bindingResult = titleBinding.getBindingResult();
            if (bindingResult != null) {
                titleLabel = new OptionsListElement(getForm());
                titleLabel.setGroupingParentComponent(this);
                titleLabel.setTitle(true);
                titleLabel.setValue(titleBinding.getBindingResult().getValue());
                titleLabel.setIcon("");
                titleLabel.init();
            }
        }

        if (valuesBinding != null && valuesBinding instanceof StaticBinding) {
            StaticBinding staticBinding = (StaticBinding) valuesBinding;
            String values = (String) staticBinding.getStaticValue();
            if (values != null && !isBinding(values) && values.indexOf("|") > 0) {
                List<String> valuesList = new ArrayList<>(Arrays.asList(values.split("\\|")));
                if (emptyValue) {
                    String result = "";
                    if (emptyValueBinding != null) {
                        BindingResult bindingResult = emptyValueBinding.getBindingResult();
                        if (bindingResult != null) {
                            result = (String) emptyValueBinding.getBindingResult().getValue();
                        }
                    }
                    valuesList.add(0, result);
                }
                valuesCollection = Arrays.stream(valuesList.toArray(new String[]{})).collect(Collectors.toMap(k -> k, this::createWrappedElement, (v1, v2) -> v1, LinkedHashMap::new));
                getSubcomponents().clear();
                valuesCollection.values().forEach((c) -> getSubcomponents().add(c.getModelElementAsFormElement()));
            }
        }


    }

    @Override
    public void processComponents() {
        if (titleBinding != null) {
            BindingResult titleResult = titleBinding.getBindingResult();
            if (titleResult != null && titleResult.getValue() != null) {
                if (titleLabel != null) {
                    if (!Objects.equals(titleLabel.getValue(), titleBinding.getBindingResult().getValue())) {
                        titleLabel.setValue(titleResult.getValue());
                    }
                } else {
                    titleLabel = new OptionsListElement(getForm());
                    titleLabel.setGroupingParentComponent(this);
                    titleLabel.setTitle(true);
                    titleLabel.setValue(titleBinding.getBindingResult().getValue());
                    titleLabel.init();
                }
            }
        }

        // set elements
        if (valuesBinding != null) {

            BindingResult valuesBindingResult = valuesBinding.getBindingResult();
            checkNotNull(valuesBinding.getBindingExpression(), valuesBindingResult);
            checkNotNull(valuesBinding.getBindingExpression(), valuesBindingResult.getValue());

            if (valuesBindingResult.getValue() != null && valuesBindingResult.getValue() instanceof java.util.List) {
                //noinspection unchecked
                List<OptionsListElementModel> newCollection = (List<OptionsListElementModel>) valuesBindingResult.getValue();
                List<ModelElementWrapper> oldCollection = bindingCollection; // alias
                String result = "";
                if (emptyValueBinding != null) {
                    BindingResult bindingResult = emptyValueBinding.getBindingResult();
                    if (bindingResult != null) {
                        result = (String) emptyValueBinding.getBindingResult().getValue();
                    }
                }
                OptionsListElementModel emptyElementModel = new OptionsListElementModel(-1, result);
                if (emptyValue && !newCollection.contains(emptyElementModel)) {
                    newCollection.add(0, emptyElementModel);
                }

                if (oldCollection != null) {

                    // elements to remove from list. cycle through old list to check if element exists on new list. If not, then remove it.
                    Iterator<ModelElementWrapper> oldCollectionIterator = oldCollection.iterator();
                    while (oldCollectionIterator.hasNext()) {
                        ModelElementWrapper oldElement = oldCollectionIterator.next();
                        boolean isInNewCollection = false;
                        for (OptionsListElementModel newElement : newCollection) {
                            if (newElement.getId().equals(oldElement.getModelElementReference().getId())) { // poprawic equals
                                isInNewCollection = true;
                                break;
                            }
                        }
                        if (!isInNewCollection) {
                            oldCollectionIterator.remove();
                        }
                    }

                    // Cycle trough new list. If element doesn't exist on old list, then add it.
                    int previousFoundIdx = -1;
                    for (OptionsListElementModel newElement : newCollection) {
                        boolean isInOldCollection = false;
                        for (int idx = 0; idx < oldCollection.size(); idx++) {
                            ModelElementWrapper oldElement = oldCollection.get(idx);
                            if (newElement.getId().equals(oldElement.getModelElementReference().getId())) {
                                isInOldCollection = true;
                                previousFoundIdx = idx;
                                break;
                            }
                        }
                        if (!isInOldCollection) {
                            if (previousFoundIdx + 1 > oldCollection.size())
                                oldCollection.add(createWrappedElement(newElement));
                            else
                                oldCollection.add(previousFoundIdx + 1, createWrappedElement(newElement));

                            ++previousFoundIdx;
                        }
                    }
                } else {
                    bindingCollection = new LinkedList<>();
                    for (OptionsListElementModel listElementModel : newCollection) {
                        bindingCollection.add(createWrappedElement(listElementModel));
                    }
                }
            }
        } else if (valuesCollection != null) {
            getSubcomponents().clear();
            valuesCollection.values().forEach((c) -> getSubcomponents().add(c.getModelElementAsFormElement()));
        }
    }

    private static void checkNotNull(String cause, Object value) {
        if (Objects.isNull(value)) {
            throw new FhException(cause + " referenced from view is null or not found");
        }
    }

    @Override
    public List<OptionsListElement> getSubcomponents() {

        if (bindingCollection != null) {
            LinkedList<OptionsListElement> returnedCollection =
                    bindingCollection.stream()
                            .map(ModelElementWrapper::getModelElementAsFormElement)
                            .collect(Collectors.toCollection(LinkedList::new));

            if (!getForm().getViewMode().equals(Form.ViewMode.DESIGN) && titleLabel != null) {
                returnedCollection.addFirst(titleLabel);
            }

            return returnedCollection;
        } else {
            return super.getSubcomponents();
        }
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (valueChange.getMainValue() != null) {
            List<Map<String, Object>> changedElementState = valueChange.getMapListAttribute(ValueChange.MAIN_VALUE_ATTRIBUTE);
            for (Object element : changedElementState) {
                Map elementMap = (Map) element;
                String id = null, value = null;
                for (Object sendedElement : elementMap.keySet()) {
                    if (Objects.equals("id", sendedElement)) {
                        id = elementMap.get(sendedElement).toString();
                    } else if (Objects.equals("value", sendedElement)) {
                        value = elementMap.get(sendedElement).toString();
                    }
                }

                if (bindingCollection != null) {
                    for (ModelElementWrapper modelWrapperElement : bindingCollection) {
                        if (Objects.equals(modelWrapperElement.getModelElementAsFormElement().getId(), id)) {
                            modelWrapperElement.getModelElementReference().setChecked(Boolean.valueOf(value));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "OptionsList";
    }

    /*private static <T> T TODO(String desc){
        throw new TodoException(desc);
    }

    static class TodoException extends RuntimeException {
        TodoException(String s){
            super(s);
        }
    }*/

    private ModelElementWrapper createWrappedElement(String value) {
        OptionsListElementModel listElementModel = new OptionsListElementModel();
        listElementModel.setValue(value);
        return createWrappedElement(listElementModel);
    }

    private ModelElementWrapper createWrappedElement(OptionsListElementModel listElement) {
        OptionsListElement label = new OptionsListElement(getForm());
        label.setGroupingParentComponent(this);
        label.setValue(listElement.getValue());
        label.setChecked(listElement.isChecked());
        label.setIcon(listElement.getIcon());
        label.init();

        ModelElementWrapper modelElementWrapper = new ModelElementWrapper();
        modelElementWrapper.setModelElementAsFormElement(label);
        modelElementWrapper.setModelElementReference(listElement);
        return modelElementWrapper;
    }

    private static boolean isBinding(String expr) {
        return expr.contains("{") && expr.contains("}");
    }

    // Getters Setters

    public OptionsListElement getTitle() {
        return titleLabel;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_ICON_CLICK_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onIconClick);
        } else if (ON_CHANGE_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnIconClick(ActionBinding onIconClick) {
        this.onIconClick = onIconClick;
    }

    public IActionCallbackContext setOnIconClick(IActionCallback onIconClick) {
        return CallbackActionBinding.createAndSet(onIconClick, this::setOnIconClick);
    }

    public void setOnChange(ActionBinding onChange) {
        this.onChange = onChange;
    }

    public IActionCallbackContext setOnChange(IActionCallback onChange) {
        return CallbackActionBinding.createAndSet(onChange, this::setOnChange);
    }

    // auxiliary classes
    @Getter
    @Setter
    private class ModelElementWrapper {
        private OptionsListElement modelElementAsFormElement;
        private OptionsListElementModel modelElementReference;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ModelElementWrapper && modelElementReference.equals(obj);
        }

        @Override
        public int hashCode() {
            return modelElementReference.hashCode();
        }
    }
}

