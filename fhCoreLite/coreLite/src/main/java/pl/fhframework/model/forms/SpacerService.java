package pl.fhframework.model.forms;

import java.util.Optional;

/**
 * Created by Adam Zareba on 14.03.2017.
 */
public interface SpacerService {

    /**
     * Max size of form elements displayed in one row. It is defined by bootstrap max size.
     */
    int RESOLUTION_MAX_SIZE = 12;

    /**
     * Minimum size of Spacer.
     */
    int SPACER_MINIMUM_SIZE = 1;

    FormElement getSpacerTemplate(Form form);

    void fillEmptyForm(Form form);

    FormElement getSingleSpacerClone(final FormElement spacerTemplate, IGroupingComponent groupingComponent, String sizeClass, int size);

    Integer getSize(FormElement formElement);

    void replaceComponentWithSpacers(Form form, final FormElement spacerTemplate, IGroupingComponent groupingParentFormElement, Component movedComponent, Integer index, boolean isAdded, boolean updateParent, String sizeClass);

    Integer getSize(String size);

    IGroupingComponent getParentComponent(Component component, Form form);

    boolean deleteDesignerRow(IGroupingComponent groupingComponent, int index);

    int getSpaceAvailableToAddNewElement(IGroupingComponent groupingParentFormElement, Optional<FormElement> componentToIgnore, Integer index);

    boolean isEnoughSpaceToAddNewElement(IGroupingComponent groupingParentFormElement, Optional<FormElement> componentToIgnore, Integer index, int componentSize);
}
