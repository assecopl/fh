package pl.fhframework.model.forms;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adam Zareba on 14.03.2017. Class provides reusable methods for filling in form lines
 * with empty spacers.
 */
@Service
public class SpacerServiceImpl implements SpacerService {

    /**
     * Size pattern for getting 'md' bootstrap class.
     */
    private static final Pattern sizePattern = Pattern.compile("md-\\d+");

    /**
     * Default height of Spacer.
     */
    private static final String DEFAULT_SIZE = "40px";

    /**
     * Returns new object of Spacer type
     */
    @Override
    public FormElement getSpacerTemplate(Form form) {
        return new Spacer(form);
    }

    @Override
    public void fillEmptyForm(Form form) {
        for (int i = 0; i < 3; i++) {
            form.getSubcomponents().add(new Row(form));
        }
    }

    /**
     * Returns new object of Spacer type with size of {@code size} and grouping component {@code
     * groupingComponent}.
     */
    @Override
    public FormElement getSingleSpacerClone(final FormElement spacerTemplate, IGroupingComponent groupingComponent, String sizeClass, int size) {
        FormElement cloneSpacer = (FormElement) spacerTemplate.clone().get();
        cloneSpacer.setWidth(sizeClass + size);
        cloneSpacer.setHeight(DEFAULT_SIZE);
        cloneSpacer.setGroupingParentComponent(groupingComponent);
        cloneSpacer.init();

        return cloneSpacer;
    }

    /**
     * Returns integer value of {@code formElement} size based on bootstrap class. By default it
     * returns {@value RESOLUTION_MAX_SIZE} if size is empty for {@link IGroupingComponent}.
     */
    @Override
    public Integer getSize(FormElement formElement) {
        if (formElement.getWidth() != null) {
            // get value in mattern md-x/md-xx, when x in digit
            Matcher matcher = sizePattern.matcher(formElement.getWidth());
            if (matcher.find()) {
                // skip first 3 signs to get integer value of size
                return Integer.valueOf(matcher.group().substring(3));
            } else {
                return RESOLUTION_MAX_SIZE;
            }
        } else if (formElement instanceof IGroupingComponent) {
            return RESOLUTION_MAX_SIZE;
        } else {
            return 2;
        }
    }

    /**
     * Returns integer value of {@code size} based on bootstrap class.
     */
    @Override
    public Integer getSize(String size) {
        Matcher matcher = sizePattern.matcher(size);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group().substring(3));
        } else {
            return RESOLUTION_MAX_SIZE;
        }
    }

    /**
     * Method performs: - inserting Spacers for old parent in place of {@code movedComponent} -
     * inserting (or moving) {@code movedComponent} to {@code groupingParentFormElement} - removing
     * {@code movedComponent} from old parent (if movedComponent is moved to new groupingComponent
     * or just newly added) - removing Spacers from {@code groupingParentFormElement}
     */
    @Override
    public void replaceComponentWithSpacers(Form form, final FormElement spacerTemplate, IGroupingComponent groupingParentFormElement, Component movedComponent, Integer index, boolean isAdded, boolean updateParent, String sizeClass) {
        IGroupingComponent originalParent = getParentComponent(movedComponent, form);

        if (isAdded) {
            // if movedComponent is added, it has to be added to groupingParentFormElement
            if (groupingParentFormElement instanceof Table) {
                ((Table) groupingParentFormElement).getColumns().add((Column) movedComponent);
            } else {
                //noinspection unchecked
                groupingParentFormElement.getSubcomponents().add(index, movedComponent);
            }
        } else {
            // if movedComponent is moved, it has to be removed from old parent - originalParent and added for groupingParentFormElement
            int indexOfElement = originalParent.getSubcomponents().indexOf(movedComponent);
            originalParent.getSubcomponents().remove(indexOfElement);
            groupingParentFormElement.getSubcomponents().add(index, movedComponent);
        }
    }

    private int getIndexOfFirstInLine(IGroupingComponent<Component> groupingComponent, int targetIndex) {
        int lineSize = 0;
        int firstInLine = 0;
        for (int index = 0; index < groupingComponent.getSubcomponents().size(); index++) {
            Component component = groupingComponent.getSubcomponents().get(index);
            if (lineSize == 0) {
                firstInLine = index;
            }
            if (index >= targetIndex) {
                return firstInLine;
            }
            lineSize += getSize((FormElement) component);
            if (lineSize % RESOLUTION_MAX_SIZE == 0) {
                lineSize = 0;
            }
        }
        return firstInLine;
    }

    /**
     * Returns parent component for {@code component}
     */
    @Override
    public IGroupingComponent getParentComponent(Component component, Form form) {
        if (component.getGroupingParentComponent() != null) {
            return component.getGroupingParentComponent();
        } else {
            return form;
        }
    }

    /**
     * Returns information how many space is available to insert component with size equal to {@code
     * componentSize} to {@code groupingParentFormElement} on position {@code index}.
     */
    @Override
    public int getSpaceAvailableToAddNewElement(IGroupingComponent groupingParentFormElement, Optional<FormElement> componentToIgnore, Integer index) {
        VirtualGrid virtualGrid = new VirtualGrid(groupingParentFormElement, this);
        virtualGrid.build();
        if (groupingParentFormElement instanceof CompactLayout) {
            return 0;
        }

        int counter = countComponentInline(groupingParentFormElement, index);
        return RESOLUTION_MAX_SIZE - counter;
    }

    /**
     * Deletes a line of spacers.
     *
     * @param groupingComponent parent component
     * @param index             index of an element in line
     * @return true if the line was empty
     */
    @Override
    public boolean deleteDesignerRow(IGroupingComponent groupingComponent, int index) {
        //noinspection unchecked
        index = getIndexOfFirstInLine(groupingComponent, index);
        for (int i = 0; i < RESOLUTION_MAX_SIZE; i++) {
            if (!(groupingComponent.getSubcomponents().get(index + i) instanceof Invisible)) {
                return false;
            }
        }
        for (int i = 0; i < RESOLUTION_MAX_SIZE; i++) {
            groupingComponent.getSubcomponents().remove(index);
        }
        return true;
    }

    /**
     * Returns information if there's enough space to insert component with size equal to {@code
     * componentSize} to {@code groupingParentFormElement} on position {@code index}.
     */
    @Override
    public boolean isEnoughSpaceToAddNewElement(IGroupingComponent groupingParentFormElement, Optional<FormElement> componentToIgnore, Integer index, int componentSize) {
        if (groupingParentFormElement instanceof CompactLayout) {
            return true;
        }

        int counter = countComponentInline(groupingParentFormElement, index);

        if (counter == RESOLUTION_MAX_SIZE) {
            return true;
        }
        return componentSize <= RESOLUTION_MAX_SIZE - counter;
    }

    private int countComponentInline(IGroupingComponent groupingParentFormElement, int index) {
        VirtualGrid virtualGrid = new VirtualGrid(groupingParentFormElement, this);
        virtualGrid.build();

        if (groupingParentFormElement.getSubcomponents().isEmpty()) {
            return 0;
        }

        Component component = (Component) groupingParentFormElement.getSubcomponents().get(index);
        List<Object> lineOf = virtualGrid.getLineOf(component);

        int counter = 0;
        if (lineOf != null) {
            // if any following form element is not instance of Spacer, it means that there's no empty place to put new form element
            for (Object o : lineOf) {
                if (o instanceof FormElement) {
                    FormElement element = (FormElement) o;
                    counter += getSize(element);
                } else if (o instanceof VirtualGrid) {
                    counter += getSize((FormElement) ((VirtualGrid) o).getGroupingComponent());
                }
            }
        }

        return counter;
    }
}

