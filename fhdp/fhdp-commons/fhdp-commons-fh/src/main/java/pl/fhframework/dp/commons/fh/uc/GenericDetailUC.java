package pl.fhframework.dp.commons.fh.uc;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.model.GenericFormModel;
import pl.fhframework.dp.commons.fh.model.IParamVariant;
import pl.fhframework.dp.commons.fh.utils.FhUtils;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.FhException;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.fhPersistence.anotation.Approve;
import pl.fhframework.fhPersistence.anotation.Cancel;
import pl.fhframework.model.forms.Form;

import java.util.ResourceBundle;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-06
 */
public abstract class GenericDetailUC<MODEL extends GenericFormModel, PARAMS> extends FhdpBaseUC implements IUseCaseOneInput<PARAMS, IGenericListOutputCallback<PARAMS>> {

    @Getter @Setter
    private MODEL data;
    @Getter @Setter
    private Form form;



    @Override
    public void start(PARAMS params) {
        data = initFormData(params);
        init();
        String formId = getFormClassId();
//        Class<Form> formClass = (Class<Form>) dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(formId));
        String variant = null;
        if(params instanceof IParamVariant) {
            IParamVariant p = (IParamVariant) params;
            variant = p.getFormVariant();
        }
        if(variant == null) {
            form =  showForm(formId, data);
        } else {
            form =  showForm(formId, data, variant);
        }
        afterDisplay();
    }

    /**
     * Method should return qualified dynamic form class ID.
     * @return
     */
    protected abstract String getFormClassId();

    /**
     * Method should init form data instance based on parameters.
     * @param params
     * @return
     */
    protected abstract MODEL initFormData(PARAMS params);

    /**
     * Optional init method called before showing form
     */
    protected void init() {}

    /**
     * Optional method called after showing form
     */
    private void afterDisplay() {}

    /**
     * Optional callback method before close.
     * @return true - close form. false - do not close.
     */
    private boolean beforeClose() {
        return true;
    }

    /**
     * Method saving data. Depends on backend architecture.
     * @return
     */
    protected abstract PARAMS saveData();

    /**
     * Method deleting data. Depends on backend architecture.
     */
    protected abstract void deleteData();

    @Action(value="save", validate=true, breakOnErrors=BreakLevelEnum.BLOCKER)
    public void save() {
        PARAMS result = saveData();
        exit().save(result);
    }

    @Action(value="delete", intermediary=true, validate=false, breakOnErrors= BreakLevelEnum.BLOCKER)
    @Approve
    public void delete()
    {
        //TODO: switch to MessageService
        ResourceBundle messages = ResourceBundle.getBundle("translations", getUserSession().getLanguage());
        FhUtils.showConfirmDialogYesNo(messages.getString("button.delete.confirm.title"),
                messages.getString("button.delete.confirm.message"),
                messages.getString("button.yes"),
                this::deleteData,
                messages.getString("button.no"),
                () -> {
                        performDelete();
                        exit().delete();
                    }
                );
//        Messages.builder(getUserSession())
//                .withDialogTitle("$.button.delete.confirm.title")
//                .withMessage("$.button.delete.confirm.message")
//                .withSeverityLevel(Messages.Severity.WARNING)
//                .withButtonAction(pl.fhframework.model.forms.messages.ActionButton.get("$.button.yes", (v) -> {
//                    Messages.close(v);
//                    deleteData();
//                    exit().delete();
//                }))
//                .withButtonAction(ActionButton.getClose("$.button.no"))
//                .enableBindableText()
//                .build();
    }

    protected void performDelete() {
        throw new FhException("Not implemented yet");
    }


    @Action(value="close", validate=false, breakOnErrors= BreakLevelEnum.BLOCKER)
    public void close()
    {
        if(beforeClose()) {
            runAction("close-action");
        }
    }

    @Action("close-action")
    @Cancel
    private void closeAction() {
        exit().cancel();
    }



}
