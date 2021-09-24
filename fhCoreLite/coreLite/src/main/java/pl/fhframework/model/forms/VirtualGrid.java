package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.uc.FhNotSupportedFormException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam Zareba on 07.03.2017. Class representing two dimensional structure of form
 * elements placed in form. It presents information how form is displayed by Front-End. All form
 * elements are split on rows.
 */
public class VirtualGrid {

    /**
     * Map of form elements split on rows. Key is row number - starting from index equal 0. Value is
     * the list of objects - form elements and {@link VirtualGrid} objects, since element could be
     * {@link VirtualGrid} itself (in case when element is type of {@link IGroupingComponent}).
     */
    @Getter
    private Map<Integer, List<Object>> mapping;

    /**
     * Grouping component that is the parent of structure defined in {@link #mapping} map.
     */
    @Getter
    private IGroupingComponent groupingComponent;

    /**
     * Size of {@link #groupingComponent} that defines the max size of elements that could be
     * inserted for this {@link VirtualGrid}.
     */
    @Getter
    @Setter
    private int size;

    /**
     * Service with reusable methods. For example for getting clone of objects.
     */
    private SpacerService spacerService;

    /**
     * Constructor for setting up context of {@link VirtualGrid}. By default we assume that max size
     * of {@link IGroupingComponent}/{@link VirtualGrid} is defined by {@value
     * SpacerService#RESOLUTION_MAX_SIZE}, since max size of all grouping components is 12.
     */
    VirtualGrid(IGroupingComponent groupingComponent, SpacerService spacerService) {
        this.groupingComponent = groupingComponent;
        this.mapping = new HashMap<>();
        this.size = SpacerService.RESOLUTION_MAX_SIZE;
        this.spacerService = spacerService;
    }

    /**
     * Performs build operation for {@link #groupingComponent} and builds {@link #mapping}
     * collection.
     */
    public void build() {
        buildComponentLevel(groupingComponent);
    }

    /**
     * Iterates over form elements net to visualize real placement on displayed form. Builds net of
     * elements displayed on form. Elements are split on rows and inserted in the {@link #mapping}
     * collection.
     */
    private void buildComponentLevel(IGroupingComponent<Component> groupingComponent) {
        // visited line size. It stores information what is the size of elements that will be displayed in one row by Front-End
        int lineSize = 0;
        // row index. Stores information about specific line that will be displayed by Front-End
        int indexOfLine = 0;

        for (Component component : groupingComponent.getSubcomponents()) {
            if (!(component instanceof FormElement)) {
                throw new FhNotSupportedFormException("Form is not editable in the designer. Not supported component found: " + component.getClass().getSimpleName() + ".");
            }
            FormElement formElement = (FormElement) component;
            // get size of form element
            int elementSize = spacerService.getSize(formElement);

            // check if formElement will be displayed in line with index equal to indexOfLine.
            // If yes, it mean that formElement needs to be added to existing line.
            // If no, it means that formElement needs to be added to the new line.
            if (elementFitToLine(lineSize, elementSize)) {
                // sum visited form elements and sum lineSize of displayed elements in current line/row
                lineSize += elementSize;

                // if it is the first element in line, it has to initialize the collection of elements
                if (mapping.get(indexOfLine) == null) {
                    List<Object> elements = new LinkedList<>();
                    mapping.put(indexOfLine, elements);
                }

                // if formElement is grouping component, it has to create a new VirtualGrid to build new net of elements where parent is now formElement
                // for example PanelGroup, Repeater or Table has own structure of VirtualGrid
                if (formElement instanceof IGroupingComponent) {
                    VirtualGrid subGrid = new VirtualGrid((IGroupingComponent) formElement, spacerService);
                    subGrid.setSize(elementSize);
                    subGrid.build();
                    mapping.get(indexOfLine).add(subGrid);
                } else {
                    // add formElement to the collection displayed in line defined by indexOfLine
                    mapping.get(indexOfLine).add(formElement);
                }
            } else {
                // increment indexOfLine and reset lineSize
                indexOfLine++;
                lineSize = elementSize;
                List<Object> elements = new LinkedList<>();

                // if formElement is grouping component, it has to create a new VirtualGrid to build new net of elements where parent is now formElement
                // for example PanelGroup, Repeater or Table has own structure of VirtualGrid
                if (formElement instanceof IGroupingComponent) {
                    VirtualGrid subGrid = new VirtualGrid((IGroupingComponent) formElement, spacerService);
                    subGrid.setSize(elementSize);
                    subGrid.build();
                    elements.add(subGrid);
                } else {
                    // add formElement to the collection displayed in line defined by indexOfLine
                    elements.add(formElement);
                }

                // insert information about new line with all form elements
                mapping.put(indexOfLine, elements);
            }
        }
    }

    /**
     * Returns line number where component is inserted.
     */
    List<Object> getLineOf(Component component) {
        // iterate over all mappings to get lists of form elements in each line
        for (Map.Entry<Integer, List<Object>> entry : mapping.entrySet()) {
            List<Object> values = entry.getValue();

            for (Object value : values) {
                if (value.equals(component) || (value instanceof VirtualGrid && ((VirtualGrid) value).getGroupingComponent().equals(component))) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Returns information if form element with size {@code #elementSize} could be placed to the
     * line with size {@link SpacerService#RESOLUTION_MAX_SIZE}. Method knows what the size is of
     * form elements already placed in line - it is defined by variable {@code #lineSum}.
     */
    private boolean elementFitToLine(int lineSum, int elementSize) {
        return (lineSum + elementSize) <= SpacerService.RESOLUTION_MAX_SIZE;
    }
}
