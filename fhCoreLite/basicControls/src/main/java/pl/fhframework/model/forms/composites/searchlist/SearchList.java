package pl.fhframework.model.forms.composites.searchlist;

import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.annotations.composite.FireEvent;
import pl.fhframework.model.forms.CompositeForm;
import pl.fhframework.model.forms.model.OptionsListElementModel;
import pl.fhframework.events.ViewEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2017-01-14.
 */

@Composite(template = "SearchList",
        model = SearchListModel.class,
        registeredEvents = "onSearchCompleted"
)
public class SearchList extends CompositeForm<SearchListModel>{

    @Action
    @FireEvent(name="onSearch")
    public void onSearch(ViewEvent<SearchListModel> viewEvent){
        SearchListModel model = viewEvent.getSourceForm().getModel();
        List<OptionsListElementModel> originalSource = model.getOriginalListElements();
        String input = model.getInputValue();
        model.setListElements(originalSource.stream().filter((e -> e.getValue().toLowerCase()
                .contains(nullAsEmpty(input).toLowerCase()))).collect(Collectors.toList()));
    }

    private String nullAsEmpty(String s){
        if (s==null) return "";
        return s;
    }
}
