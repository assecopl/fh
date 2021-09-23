package pl.fhframework.model.forms.designer;

import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Interface of designer's fixed values of properties provider.
 * Objects of this class are meant to be cached and must be stateless.
 */
public interface IDesignerFixedValuesProvider {

    /**
     * Returns preview value
     * @param componentClass component
     * @param field field with XMLProperty annotation
     * @return list of fixed values
     */
    public List<String> getFixedValues(Class<? extends Component> componentClass, Field field);

    /**
     * Checks if free typing is allowed. If not only fixed values are allowed. It is only used if fixed values are present.
     * @param componentClass component
     * @param field field with XMLProperty annotation
     * @return true if free typing is allowed
     */
    public boolean isFreeTypingAllowed(Class<? extends Component> componentClass, Field field);

    /**
     * Checks if filtering is allowed. Filtering is always allowed if free typing is allowed. If true a Combo instead of SelectOneMenu control will be used. It is only used if fixed values are present.
     * @param componentClass component
     * @param field field with XMLProperty annotation
     * @return true if free typing is allowed
     */
    public boolean isFilteringAllowed(Class<? extends Component> componentClass, Field field);

    /**
     * Checks if empty value is allowed. It is only used if fixed values are present.
     * @param componentClass component
     * @param field field with XMLProperty annotation
     * @return true if empty value is allowed
     */
    public boolean isEmptyValueAllowed(Class<? extends Component> componentClass, Field field);
}
