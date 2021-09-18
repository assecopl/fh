package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pl.fhframework.events.IEventSource;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.table.LowLevelRowMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TableRow {

    @JsonIgnore
    private Table table;

    /**
     * Table cells holds data stored/wrapped in visual components Structure acts as an indirect
     * layer between model and presentation layer
     */
    @Getter
    private List<FormElement> tableCells = new ArrayList<>();

    public TableRow(Table table, LowLevelRowMetadata lowLevelRowMetadata) {
        this.table = table;

        for (Column column : table.getColumns()) {
            tableCells.addAll(column.createTableCells(lowLevelRowMetadata));
        }
    }

    public void updateFormComponents(Set<ElementChanges> formComponentsChanges) {
        for (FormElement tableCell : tableCells) {
            if (tableCell instanceof TableCell) {
                ((TableCell) tableCell).doActionForEverySubcomponent((FormElement component) -> {
                    updateFromComponent(component, formComponentsChanges);
                });
            } else {
                updateFromComponent(tableCell, formComponentsChanges);
            }
        }
    }

    // probably never used method - in future it should be removed
    public IEventSource getEventSource(String elementId) {
        final IEventSource[] result = new IEventSource[1];
        for (FormElement tableCell : tableCells) {
            if (tableCell instanceof TableCell) {
                ((TableCell) tableCell).doActionForEverySubcomponent((FormElement component) -> {
                    if (component.getId().equals(elementId))
                        result[0] = (IEventSource) component;
                });
            } else {
                if (tableCell.getId().equals(elementId)) result[0] = (IEventSource) tableCell;
            }
            if (result[0] != null) break;
        }
        return result[0];
    }

    private void updateFromComponent(FormElement formElement, Set<ElementChanges> formComponentsChanges) {
        ElementChanges elementChanges = formElement.updateView();
        if (!elementChanges.getChangedAttributes().isEmpty()) {
            formComponentsChanges.add(elementChanges);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableRow other = (TableRow) obj;
        if (table == null) {
            if (other.table != null) {
                return false;
            }
        } else if (!table.equals(other.table)) {
            return false;
        }

        if (tableCells == null) {
            if (other.tableCells != null) {
                return false;
            }
        } else if (!tableCells.equals(other.tableCells)) {
            return false;
        }

        return true;
    }
}
