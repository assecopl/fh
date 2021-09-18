package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.resource.MarkdownRepository;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class MarkdownGrid extends FormElement implements IChangeableByClient {

    private static final String VALUES_ATTR = "values";
    private static final String SUBSYSTEM_ATTR = "subsystem";
    private static final String SELECTED_ITEM_ATTR = "selectedItem";
    private static final String SELECTED_INDEX_ATTR = "selectedIndex";

    @JsonIgnore
    private MarkdownRepository.MarkdownEntry selectedItem;

    @JsonIgnore
    private Integer selectedItemIndex;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUES_ATTR)
    @DesignerXMLProperty(allowedTypes = {List.class})
    private ModelBinding<List<MarkdownRepository.MarkdownEntry>> valuesBinding;

    @Getter
    private List<MarkdownRepository.MarkdownEntry> values;

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
    private ModelBinding<MarkdownRepository.MarkdownEntry> selectedItemBinding;

    @Setter
    @Getter
    protected List<String> subDirectories = new ArrayList<>();

    public MarkdownGrid(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
//        this.resolveSubdirectories();

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
                    this.values = (List<MarkdownRepository.MarkdownEntry>) valuesBindingResult.getValue();
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