package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by krzysztof.kobylarek on 2017-01-02.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OptionsListElement extends ComponentElement {

    private AtomicInteger counter = new AtomicInteger(1);

    private LinkedList<OptionsListElementModel> elements = createList();

    private LinkedList<OptionsListElementModel> createList() {
        return new LinkedList<>(Arrays.asList(
                new OptionsListElementModel(counter.get(), "Element "+counter.getAndIncrement()),
                new OptionsListElementModel(counter.get(), "Element "+counter.getAndIncrement()),
                new OptionsListElementModel(counter.get(), "Element "+counter.getAndIncrement())));
         }

    private List<OptionsListElementModel> listWithEmptyElement = new LinkedList<>(Arrays.asList(new OptionsListElementModel(counter.get(), "One"),
            new OptionsListElementModel(counter.getAndIncrement(), "Two"),
            new OptionsListElementModel(counter.getAndIncrement(), "Three"))
    );

    private OptionsListElementModel emptyListElement= null;

    private List<OptionsListElementModel> selectedList = new LinkedList<>();

    public List<OptionsListElementModel> getSelectedList() {
        return selectedList;
    }
}