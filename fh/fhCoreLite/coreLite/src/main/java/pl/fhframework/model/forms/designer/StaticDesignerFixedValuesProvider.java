package pl.fhframework.model.forms.designer;

import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Attribute's designer fixed values provider with a static list of values.
 */
public abstract class StaticDesignerFixedValuesProvider implements IDesignerFixedValuesProvider {

    private List<String> fixedValues;

    private boolean freeTypingAllowed;

    private boolean filteringAllowed;

    private boolean emptyValueAllowed;

    public StaticDesignerFixedValuesProvider(boolean freeTypingAllowed, boolean filteringAllowed, boolean emptyValueAllowed, String... values) {
        this.fixedValues = Arrays.asList(values);
        this.freeTypingAllowed = freeTypingAllowed;
        this.filteringAllowed = filteringAllowed;
        this.emptyValueAllowed = emptyValueAllowed;
    }

    @Override
    public List<String> getFixedValues(Class<? extends Component> componentClass, Field field) {
        return fixedValues;
    }

    @Override
    public boolean isFreeTypingAllowed(Class<? extends Component> componentClass, Field field) {
        return freeTypingAllowed;
    }

    @Override
    public boolean isFilteringAllowed(Class<? extends Component> componentClass, Field field) {
        return filteringAllowed;
    }

    @Override
    public boolean isEmptyValueAllowed(Class<? extends Component> componentClass, Field field) {
        return emptyValueAllowed;
    }
}
