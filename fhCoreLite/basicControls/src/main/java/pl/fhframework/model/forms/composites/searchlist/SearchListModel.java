package pl.fhframework.model.forms.composites.searchlist;

import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2017-01-14.
 */
public class SearchListModel {

    private List<OptionsListElementModel> originalSource = null;
    private List<OptionsListElementModel> filteredElements = null;
    private String inputValue = null;
    private String title = null;

    public SearchListModel(List<OptionsListElementModel> originalSource){
        this.originalSource=originalSource;
    }

    public SearchListModel(List<OptionsListElementModel> originalSource, String title){
        this(originalSource);
        this.title=title;
    }
    public  List<OptionsListElementModel> getOriginalListElements(){
        return originalSource;
    }

    public List<OptionsListElementModel> getListElements(){
        return filteredElements!=null
                ? filteredElements
                : originalSource;
    }

    public List<OptionsListElementModel> getListElementsView(Predicate<OptionsListElementModel> action){
        return getListElements().stream().filter(action).collect(Collectors.toCollection(LinkedList::new));
    }

    void setListElements(List<OptionsListElementModel> listElements){
         this.filteredElements=listElements;
     }

    public  String getInputValue(){
        return inputValue;
    }

    public void setInputValue(String inputValue){
        this.inputValue=inputValue ;
    }

    public String getListTitle(){
        return title;
    }

    public void setListTitle(String title) {
        this.title = title;
    }
}
