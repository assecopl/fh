package pl.fhframework.docs.forms.component.searchlist;

import lombok.Getter;
import pl.fhframework.model.forms.composites.searchlist.SearchListModel;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2017-01-14.
 */
public class SearchListExampleModel {

    private List<OptionsListElementModel> createData() {
        AtomicInteger counter = new AtomicInteger(0);
        return Arrays.asList("Nadazero", "Unaone", "Bissotwo", "Terrathree", "Kartefour", "Pantafive", "Soxisix", "Setteseven", "Oktoeight", "Noveniner")
                .stream().map((s -> new OptionsListElementModel(counter.incrementAndGet(), s))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Getter
    private SearchListModel searchListModel = new SearchListModel(createData(), "IMO phonetic alphabet");

    public List<OptionsListElementModel> getCheckedElements(){
        return searchListModel.getListElementsView(OptionsListElementModel.checkedElements);
    }

    public List<OptionsListElementModel> getNotCheckedElements(){
        return searchListModel.getListElementsView(OptionsListElementModel.notCheckedElements);
    }

    public List<OptionsListElementModel> getAllElements(){
        return searchListModel.getListElements();
    }
}
