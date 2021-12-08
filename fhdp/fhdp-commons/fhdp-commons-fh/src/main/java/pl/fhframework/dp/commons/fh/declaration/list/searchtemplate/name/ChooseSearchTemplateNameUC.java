package pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.name;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.fhPersistence.anotation.Approve;
import pl.fhframework.fhPersistence.anotation.Cancel;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.Form;

import java.util.List;
import java.util.function.Function;

@UseCase
public class ChooseSearchTemplateNameUC implements IUseCaseTwoInput<SearchTemplateDto, Function<String, List<SearchTemplateDto>>, IUseCaseSaveCancelCallback<SearchTemplateDto>> {

    private ChooseSearchTemplateNameModalForm.Model model;
    private Function<String, List<SearchTemplateDto>> existingFilterCheckFunction;
    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start(SearchTemplateDto filter, Function<String, List<SearchTemplateDto>> existingFilterCheckFunction) {
        this.model = new ChooseSearchTemplateNameModalForm.Model();
        this.model.setPkwdSearchTemplateDto(filter);
        this.model.setTemplateName(filter.getTemplateName());
        this.existingFilterCheckFunction = existingFilterCheckFunction;
        eventRegistry.fireFocusEvent(Form.MODAL_VIRTUAL_CONTAINER, "templateNameIT");
        showForm(ChooseSearchTemplateNameModalForm.class, model);
    }

    @Action
    @Approve
    public void confirm() {
        if (validate()) {
            model.getPkwdSearchTemplateDto().setTemplateName(model.getTemplateName());
            exit().save(model.getPkwdSearchTemplateDto());
        }
    }

    private boolean validate() {
        if (StringUtils.isNullOrEmpty(model.getTemplateName())) {
            reportValidationError(model, "templateName", " - pole nie może być puste", PresentationStyleEnum.ERROR);
        } else {
            boolean alreadyExist = false;
            for(SearchTemplateDto f : existingFilterCheckFunction.apply(model.getTemplateName())) {
                //skip ourself
                if (f.getTemplateName().equals(model.getTemplateName())) {
                    alreadyExist = true;
                    break;
                }
            }
            if (alreadyExist) {
                reportValidationError(model, "templateName", " - w systemie istnieje już filtr o takiej nazwie", PresentationStyleEnum.ERROR);
            }
        }

        return !getUserSession().getValidationResults().areAnyValidationMessages();
    }

    @Action(immediate = true)
    @Cancel
    public void cancel() {
        exit().cancel();
    }

}

