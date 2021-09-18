package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.resource.ImageRepository;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.events.IEventSource;

import java.util.*;

@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class ImageGrid extends FormElement implements IChangeableByClient {

    private static final String VALUES_ATTR = "values";
    private static final String SUBSYSTEM_ATTR = "subsystem";
    private static final String SELECTED_ITEM_ATTR = "selectedItem";
    private static final String SELECTED_INDEX_ATTR = "selectedIndex";

    @JsonIgnore
    private ImageRepository.ImageEntry selectedItem;

    @JsonIgnore
    private Integer selectedItemIndex;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUES_ATTR)
    @DesignerXMLProperty(allowedTypes = {List.class})
    private ModelBinding<List<ImageRepository.ImageEntry>> valuesBinding;

    @Getter
    private List<ImageRepository.ImageEntry> values;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = SUBSYSTEM_ATTR)
    @DesignerXMLProperty(allowedTypes = {String.class})
    private ModelBinding<String> subsystemBinding;

    @Getter
    private String subsystem;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = SELECTED_ITEM_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Name of model object which will be used to keep information about selected item.")
    private ModelBinding<ImageRepository.ImageEntry> selectedItemBinding;

    public ImageGrid(Form form) {
        super(form);
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (valueChange.hasAttributeChanged(SELECTED_INDEX_ATTR)) {
            Integer selectedIndex = valueChange.getIntAttribute(SELECTED_INDEX_ATTR);
            if (selectedIndex != null) {
                this.selectedItemIndex = selectedIndex;
                this.selectedItem = this.values.get(selectedItemIndex);

                if (selectedItemBinding != null) {
                    selectedItemBinding.setValue(selectedItem);
                }
            } else {
                this.selectedItemIndex = null;
                this.selectedItem = null;

                if (selectedItemBinding != null) {
                    selectedItemBinding.setValue(null);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges changes = super.updateView();

        if (valuesBinding != null) {
            BindingResult valuesBindingResult = valuesBinding.getBindingResult();
            if (valuesBindingResult != null) {
                if (valuesBindingResult.getValue() instanceof Collection) {
                    this.values = (List<ImageRepository.ImageEntry>) valuesBindingResult.getValue();
                    changes.addChange(VALUES_ATTR, values);
                }
            }
        }

        if (selectedItemBinding != null) {
            BindingResult selectedItemBindingResult = selectedItemBinding.getBindingResult();
            if (selectedItemBindingResult.getValue() == null) {
                changes.addChange(SELECTED_ITEM_ATTR, null);
                this.selectedItemIndex = null;
            } else {
                changes.addChange(SELECTED_ITEM_ATTR, selectedItemIndex);
            }
        }

        if (subsystemBinding != null) {
            BindingResult<String> bindingResult = subsystemBinding.getBindingResult();
            if (bindingResult != null) {
                String newNameValue = bindingResult.getValue();
                if (!areValuesTheSame(newNameValue, subsystem)) {
                    this.subsystem = newNameValue;
                    changes.addChange(SUBSYSTEM_ATTR, subsystem);
                }
            }
        }

        refreshView();

        return changes;
    }
}