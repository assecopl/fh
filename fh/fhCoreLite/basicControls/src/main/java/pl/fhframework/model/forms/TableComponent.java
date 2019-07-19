package pl.fhframework.model.forms;


import java.util.Map;

public interface TableComponent<T extends FormElement> {

    /**
     * Create a new component of the same type.
     * @return a new component of the same type
     */
    T createNewSameComponent();

    /**
     * Creates a copy this component with replaced iterators in bindings and actions.
     * @param table table
     * @param iteratorReplacements iterator replacements
     * @return a copy this component
     */
    default T getCopy(Table table, Map<String, String> iteratorReplacements) {
        T clone = createNewSameComponent();
        doCopy(table, iteratorReplacements, clone);
        return clone;
    }

    /**
     * Copies values of attributes with replaced iterators in bindings and actions to a new component of the same type.
     * @param table table
     * @param iteratorReplacements iterator replacements
     * @param clone a new component of the same type
     */
    default void doCopy(Table table, Map<String, String> iteratorReplacements, T clone) {
        FormElement thisElement = (FormElement) this;

        // Component
        clone.setId(thisElement.getId());
        clone.setAvailabilityModelBinding(table.getRowBinding(thisElement.getAvailabilityModelBinding(), clone, iteratorReplacements));

        // FormElement
        clone.setHeight(thisElement.getHeight());
        clone.setWidth(thisElement.getWidth());
        clone.setHorizontalAlign(thisElement.getHorizontalAlign());
        clone.setVerticalAlign(thisElement.getVerticalAlign());
        clone.setStyleClasses(thisElement.getStyleClasses());
        clone.setHintBinding(table.getRowBinding(thisElement.getHintBinding(), clone, iteratorReplacements));
    }

}
