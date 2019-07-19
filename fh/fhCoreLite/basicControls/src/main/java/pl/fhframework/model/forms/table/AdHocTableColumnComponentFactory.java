package pl.fhframework.model.forms.table;

import pl.fhframework.core.FhFormException;
import pl.fhframework.core.forms.iterators.IMultipleIteratorComponentFactory;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.binding.AdHocIndexedModelBinding;
import pl.fhframework.binding.IRowNumberOffsetSupplier;
import pl.fhframework.binding.RowNumberBindingContext;
import pl.fhframework.model.forms.Column;
import pl.fhframework.model.forms.FormElement;
import pl.fhframework.model.forms.Table;
import pl.fhframework.model.forms.TableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Piotr on 2017-03-27.
 */
public class AdHocTableColumnComponentFactory implements IMultipleIteratorComponentFactory<Table> {

    private Column column;

    private Map<String, String> replacementTemplates;

    private String[] iteratorNameArray;

    public AdHocTableColumnComponentFactory(Column column) {
        this.column = column;
    }

    private boolean initDone = false;

    private void init() {
        replacementTemplates = AdHocIndexedModelBinding.buildIteratorReplacementTemplates(column.getIteratorInfos());
        iteratorNameArray = buildIteratorNameArray(column.getIteratorInfos());
        initDone = true;
    }

    @Override
    public List<FormElement> createComponentsForIterators(Table myGroupingParent,
                                                          IRowNumberOffsetSupplier rowNumberOffsetSupplier,
                                                          int[] indices) {
        if (!initDone) {
            init();
        }
        Map<String, String> replacements = AdHocIndexedModelBinding.fillIteratorReplacementTemplates(replacementTemplates, indices);

        List<FormElement> clonedComponents = new ArrayList<>();
        for (FormElement formElement : column.getPrototype().getSubcomponents()) {
            if (formElement instanceof TableComponent) {
                FormElement clone = ((TableComponent) formElement).getCopy(myGroupingParent, replacements);
                setUpClonedFormComponent(myGroupingParent, clone, indices);
                clonedComponents.add(clone);
            } else {
                throw new FhFormException("Form component of class '" + formElement.getClass().getSimpleName() + "' could not be presented in table because it does not implement interface called 'TableComponent'!");
            }
        }
        return clonedComponents;
    }

    private void setUpClonedFormComponent(Table owningTable, FormElement clone, int[] indices) {
        final String uniqueIdSuffix = getMergedIdBasedOnIterators(indices);
        clone.setId(owningTable.getId() + uniqueIdSuffix + clone.getId());

        clone.setGroupingParentComponent(owningTable);

        boolean isMainIterator = true;
        for (int i = 0; i < Math.min(indices.length, iteratorNameArray.length); i++) {
            RowNumberBindingContext rowContext;
            // only main iterator has offset based on table paging
            if (isMainIterator) {
                rowContext = new RowNumberBindingContext(iteratorNameArray[i], indices[i] + 1, owningTable);
                isMainIterator = false;
            } else {
                rowContext = new RowNumberBindingContext(iteratorNameArray[i], indices[i] + 1);
            }
            clone.getBindingContext().getRowNumberBindingContexts().add(rowContext);

            // inherit table's own row number contexts
            clone.getBindingContext().getRowNumberBindingContexts().addAll(owningTable.getBindingContext().getRowNumberBindingContexts());
        }
    }

    // to provide uniqueness of form elements ID merge all iterators values
    private String getMergedIdBasedOnIterators(int[] indices) {
        StringBuilder idBuilder = new StringBuilder();
        for (int index : indices) {
            idBuilder.append('[');
            idBuilder.append(index);
            idBuilder.append(']');
        }

        return idBuilder.toString();
    }

    private String[] buildIteratorNameArray(List<IRepeatableIteratorInfo> currentIterators) {
        String[] iterators = new String[currentIterators.size()];
        for (int i = 0; i < iterators.length; i++) {
            iterators[i] = currentIterators.get(i).getName();
        }
        return iterators;
    }
}
