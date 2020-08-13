package pl.fhframework.binding;

import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.BindingResult;

/**
 * Wrapper for non-indexed model binding
 */
public class IndexIgnoringModelBindingWrapper<T> extends IndexedModelBinding<T> {

    private ModelBinding<T> modelBinding;

    public IndexIgnoringModelBindingWrapper(ModelBinding<T> modelBinding) {
        super(modelBinding == null ? null : modelBinding.getBindingExpression());
        this.modelBinding = modelBinding;
    }

    @Override
    public T getValue(int[] indices) {
        if (modelBinding != null) {
            BindingResult<T> bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                return bindingResult.getValue();
            }
        }
        return null;
    }
}
