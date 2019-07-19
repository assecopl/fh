package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.annotations.Control;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.XMLMetadataSubelementParent;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.binding.IndexIgnoringModelBindingWrapper;
import pl.fhframework.binding.IndexedModelBinding;
import pl.fhframework.binding.ModelBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam Zareba on 20.01.2017.
 */
@Control(parents = {Table.class})
public class Iterator extends GroupingComponent<FormElement> implements IRepeatableIteratorInfo, IIndexedBindingOwner {

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    private String id;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    private IndexedModelBinding collection;

    @JsonIgnore
    @Getter
    @Setter
    @XMLMetadataSubelementParent
    private Table table;

    @JsonIgnore
    private List<IRepeatableIteratorInfo> iteratorInfos;

    public Iterator(Form form) {
        super(form);
    }

    public Iterator(Form form, String id, ModelBinding collection) {
        this(form);
        this.id = id;
        this.collection = new IndexIgnoringModelBindingWrapper<>(collection);
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public String getCollectionBinding() {
        return collection.getBindingExpression();
    }

    @Override
    public List<IRepeatableIteratorInfo> getIteratorInfos() {
        // init once
        if (iteratorInfos == null) {
            List<IRepeatableIteratorInfo> iterators = new ArrayList<>();
            for (IRepeatableIteratorInfo iterator : getTable().getAllIterators()) {
                // stop on own iterator - own collection binding cannot use this or child iterators
                if (iterator.getName().equals(this.getName())) {
                    break;
                }

                iterators.add(iterator);
            }
            iteratorInfos = iterators;
        }
        return iteratorInfos;
    }
}
