package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.docs.forms.component.Select2DictionaryComboForm;
import pl.fhframework.docs.forms.component.model.Select2DictionaryComboElement;
import pl.fhframework.docs.forms.service.DictionaryComboDataProvider;
import pl.fhframework.event.EventRegistry;


/**
 * Use case supporting Wizard documentation
 */
@UseCase
public class Select2DictionaryComboUC implements IDocumentationUseCase<Select2DictionaryComboElement> {
    private Select2DictionaryComboElement model;

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private DictionaryComboDataProvider dictionaryComboDataProvider;


    @Override
    public void start(Select2DictionaryComboElement model) {
        this.model = model;
        this.model.setStartValue(dictionaryComboDataProvider.getCode(dictionaryComboDataProvider.data.get(1)));
        showForm(Select2DictionaryComboForm.class, model);
    }

    @Action
    public void setCodeListId(String codeListId){
        this.model.setSecondValue(null);
        this.model.setCodeListId(codeListId);
    }

    @Action
    public void clearStartValue(){
        this.model.setStartValue(null);
    }



}

