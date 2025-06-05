package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.DictionaryComboForm;
import pl.fhframework.docs.forms.component.model.DictionaryComboElement;
import pl.fhframework.docs.forms.service.DictionaryComboDataProvider;
import pl.fhframework.event.EventRegistry;


/**
 * Use case supporting Wizard documentation
 */
@UseCase
public class DictionaryComboUC implements IDocumentationUseCase<DictionaryComboElement> {
    private DictionaryComboElement model;

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private DictionaryComboDataProvider dictionaryComboDataProvider;


    @Override
    public void start(DictionaryComboElement model) {
        this.model = model;
        this.model.setStartValue(dictionaryComboDataProvider.getCode(dictionaryComboDataProvider.data.get(1)));
        showForm(DictionaryComboForm.class, model);
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

