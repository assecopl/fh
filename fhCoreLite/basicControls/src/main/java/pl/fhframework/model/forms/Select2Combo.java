package pl.fhframework.model.forms;

 import pl.fhframework.annotations.*;
 import pl.fhframework.core.util.StringUtils;
 import pl.fhframework.model.dto.ElementChanges;
 import pl.fhframework.model.dto.ValueChange;
 import pl.fhframework.model.forms.optimized.ColumnOptimized;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.stream.Collectors;

@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@DesignerControl(defaultWidth = 3)
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class Select2Combo extends Combo {
    protected static final String REMOVED_INDEX_GROUP_ATTR = "removedIndexGroup";

    public Select2Combo(Form<?> form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        setFilterFunction();
        processValuesBinding();
        processFiltering("");
        this.filteredValues = collectValues(filteredObjectValues);
    }

    @Override
    protected void processFiltering(String text) {
        Map<String, List<Object>> filtered = values.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().stream().filter(s -> filterFunction.test(s, text)).collect(Collectors.toList())));
        filteredObjectValues.clear();
        filteredObjectValues.putAll(filtered);
        filterInvoked = true;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        Object textObj = valueChange.getStringAttribute(TEXT);
        Boolean addedTag = valueChange.getBooleanAttribute(ADDED_TAG);

        if (textObj != null && textObj.equals("") && !this.multiselect) {
            this.cleared = true;
            this.filterText = "";
            processFiltering(this.filterText);
            this.selectedItemIndex = -1;
            this.selectedItem = null;
            this.rawValue = null;
            changeSelectedItemBinding();
        } else if (textObj != null) {
            String text = (String) textObj;
            this.filterText = text;
            processFiltering(text);
            firstLoad = false;
            if (!isMultiselect()) {
                selectItemByFilterText();
                changeSelectedItemBinding();
            }
            // if free typing is allowed, use typed value as selected item
            if (freeTyping) {
                this.selectedItem = StringUtils.emptyToNull(text);
                if (isMultiselect() && Boolean.TRUE.equals(addedTag) && !StringUtils.isNullOrEmpty(this.rawValue)) {
                    this.selectedItem = this.rawValue;
                    changeSelectedItemBinding();
                } else {
                    this.rawValue = (String) this.selectedItem;
                }
                if (!isMultiselect()) {
                    changeSelectedItemBinding();
                }
            }
            updateFilterTextBinding();
        }
        Boolean cleared = valueChange.getBooleanAttribute(CLEARED);
        if (cleared != null && cleared && textObj == null) {
            this.cleared = cleared;
            this.filterText = "";
            processFiltering(this.filterText);
            this.selectedItemIndex = -1;
            this.selectedItem = null;
            this.rawValue = null;
            this.multiselectRawValue = null;
            changeSelectedItemBinding();
            updateFilterTextBinding();
        } else if (valueChange.hasAttributeChanged(SELECTED_INDEX_ATTR)) {
            this.cleared = false;
            String key = valueChange.getStringAttribute(SELECTED_INDEX_GROUP_ATTR);
            this.selectedItemIndex = valueChange.getIntAttribute(SELECTED_INDEX_ATTR);
            this.selectedItem = (this.selectedItemIndex >= 0) ? this.filteredObjectValues.get(key).get(selectedItemIndex) : null;
            changeSelectedItemBinding();
            this.rawValue = (!isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.multiselectRawValue = (isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.filterText = rawValue != null ? rawValue : "";
            processFiltering(this.filterText);
            updateFilterTextBinding();
        }
        if (valueChange.hasAttributeChanged(REMOVED_INDEX_ATTR)) {
            int removedIndexAttr = valueChange.getIntAttribute(REMOVED_INDEX_ATTR);
            String key = valueChange.getStringAttribute(REMOVED_INDEX_GROUP_ATTR);
            List<?> multiSelected = (List<?>) getModelBinding().getBindingResult().getValue();
            multiSelected.remove(this.filteredObjectValues.get(key).get(removedIndexAttr));
            this.selectedItem = new ArrayList<>(multiSelected);
            this.rawValue = (!isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.multiselectRawValue = (isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
        }

        if (cursorBinding != null) {
            Integer cursor = valueChange.getIntAttribute(CURSOR);
            if (cursor != null && !Objects.equals(this.cursor, cursor)) {
                this.updateBindingForValue(cursor, cursorBinding, cursorBinding.getBindingExpression(), this.getOptionalFormatter());
                this.cursor = cursor;
            }
        }
    }

    @Override
    protected void setFilterFunction() {
        this.filterFunction = (k, v) -> true;
    }

    @Override
    protected boolean processFilterBinding(ElementChanges elementChanges, boolean valuesChanged) {
        return false;
    }
}
