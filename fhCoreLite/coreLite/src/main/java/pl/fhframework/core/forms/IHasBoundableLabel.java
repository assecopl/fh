package pl.fhframework.core.forms;

import pl.fhframework.binding.ModelBinding;

/**
 * Interface of component that has a label model binding.
 */
public interface IHasBoundableLabel {

    /**
     * Returns label model binding
     * @return label model binding
     */
    public ModelBinding getLabelModelBinding();
}
