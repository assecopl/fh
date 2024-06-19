package pl.fhframework.docs.i18n;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.i18n.forms.InternationalizationForm;
import pl.fhframework.docs.i18n.forms.InternationalizationModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.messages.Messages;

import java.util.Locale;

@UseCase
@UseCaseWithUrl(alias = "docs-internationalization")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class InternationalizationUC implements IInitialUseCase {

    private static final String VALIDATION_MSG_KEY = "fh.docs.validation.example.msg";
    private static final String VALIDATION_MSG_WITH_PARAM_KEY = "fh.docs.validation.example.msgWithParam";
    private InternationalizationModel model;

    @Autowired
    private MessageService messageService;

    private InternationalizationForm form;

    @Override
    public void start() {
        initModel();
        form = showForm(InternationalizationForm.class, model);
    }

    private void initModel() {
        model = new InternationalizationModel();
        if (getUserSession().getLanguage().getLanguage().equals(Language.PL.getValue())) {
            model.setActiveBtn(0);
        }
        else {
            model.setActiveBtn(1);
        }
        model.getTableMsg().add("Student");
        model.getTableMsg().add("Teacher");
        provideMessageTranslation();
    }

    @Action
    void setPL() {
        getUserSession().setLanguage(getLocale(Language.PL.getValue()));
        model.setActiveBtn(0);
        provideMessageTranslation();
    }

    @Action
    void setEng() {
        getUserSession().setLanguage(getLocale(Language.ENG.getValue()));
        model.setActiveBtn(1);
        provideMessageTranslation();
    }

    @Action
    void reportError() {
        reportValidationError(model, "1", getAllBundles().getMessage(VALIDATION_MSG_KEY), PresentationStyleEnum.ERROR);
        reportValidationTemplateError(model, "2", VALIDATION_MSG_KEY, PresentationStyleEnum.ERROR);
        String[] args = {model.getArgA()};
        reportValidationTemplateError(model, "3", VALIDATION_MSG_WITH_PARAM_KEY, args, PresentationStyleEnum.ERROR);
    }

    private MessageService.MessageBundle getAllBundles() {
        return messageService.getAllBundles();
    }

    @Action
    void displayDialog() {
        Messages.showInfoMessage(getUserSession(), getAllBundles().getMessage("fh.docs.model.example.binding"));
    }

    private void provideMessageTranslation() {
        model.setMessageFromModel(getAllBundles().getMessage("fh.docs.model.example.binding"));
    }

    private Locale getLocale(String languageTag) {
        if (!StringUtils.isNullOrEmpty(languageTag)) {
            return Locale.forLanguageTag(languageTag);
        } else {
            return Locale.getDefault();
        }
    }

}
