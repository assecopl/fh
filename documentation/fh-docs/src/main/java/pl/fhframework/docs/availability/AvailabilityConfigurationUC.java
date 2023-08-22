package pl.fhframework.docs.availability;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.availability.model.AvailabilityConfigurationModel;
import pl.fhframework.docs.availability.model.PropertyElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.events.ViewEvent;

@UseCase
@UseCaseWithUrl(alias = "docs-availability")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class AvailabilityConfigurationUC implements IInitialUseCase {

    private AvailabilityConfigurationModel model = new AvailabilityConfigurationModel();

    @Override
    public void start() {
        showForm(AvailabilityConfigurationForm.class, model);
    }

    @Action
    public void variantA() {
        showForm(AvailabilityConfigurationForm.class, model, "a");
    }

    @Action
    public void variantB() {
        showForm(AvailabilityConfigurationForm.class, model, "b");
    }

    @Action
    public void variantC() {
        showForm(AvailabilityConfigurationForm.class, model, "c");
    }

    @Action
    public void variantDefault() {
        start();
    }

    @Action
    public void onEditableClick(PropertyElement propertyElement) {
        boolean editable = propertyElement.isEditable();
        if (editable) {
            propertyElement.setAvailability(AccessibilityEnum.EDIT);
        } else {
            propertyElement.setAvailability(AccessibilityEnum.VIEW);
        }
    }

    @Action
    public void onHideButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.HIDDEN);
        FhLogger.debug(this.getClass(), logger -> logger.log("onHideButtonClick({})", viewEvent));
    }

    @Action
    public void onViewButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.VIEW);
        FhLogger.debug(this.getClass(), logger -> logger.log("onViewButtonClick({})", viewEvent));
    }

    @Action
    public void onEditButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.EDIT);
        FhLogger.debug(this.getClass(), logger -> logger.log("onEditButtonClick({})", viewEvent));
    }
}
