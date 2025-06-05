package pl.fhframework.docs.validation;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.validation.forms.ValidationInfoForm;
import pl.fhframework.docs.validation.forms.model.ValidationInfoModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.PresentationStyleEnum;

@UseCase
@UseCaseWithUrl(alias = "docs-validation")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ValidationDocumentationUC implements IInitialUseCase {

    private ValidationInfoModel validationInfoModel;

    private ValidationInfoForm validationInfoForm;

    @Override
    public void start() {
        validationInfoModel = new ValidationInfoModel();
        validationInfoForm = showForm(ValidationInfoForm.class, validationInfoModel);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    private void savePerson() {
        reportValidationError(validationInfoModel, "name", "info.test", PresentationStyleEnum.BLOCKER);
        reportValidationError(validationInfoModel, "name", "info test", PresentationStyleEnum.BLOCKER);
        reportValidationError(validationInfoModel, "missing attribute", "info test - missing attribute", PresentationStyleEnum.BLOCKER);
        reportValidationError(validationInfoModel, "nonEditableField", "info test - non editable attribute", PresentationStyleEnum.BLOCKER);
        reportValidationError(validationInfoModel, "hiddenField", "info test - hidden attribute", PresentationStyleEnum.BLOCKER);
    }



    @Action(validate = false, clearContext = false)
    private void saveCustomPerson() {

    }

    @Action
    private void savePersonJsr() {

    }

    @Action(validate = false, clearContext = false)
    private void onTabAction() {
       getUserSession().getValidationResults().clearValidationErrors();
    }
}