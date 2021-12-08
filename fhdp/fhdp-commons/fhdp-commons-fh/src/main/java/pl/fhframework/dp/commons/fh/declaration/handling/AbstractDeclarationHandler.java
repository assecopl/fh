package pl.fhframework.dp.commons.fh.declaration.handling;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.outline.ElementCT;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.event.dto.NotificationEvent;

import java.lang.reflect.Field;
import java.util.List;

public abstract class AbstractDeclarationHandler<MODEL
        extends BaseDeclarationHandlingFormModel, DECL, DTO,  UC extends BaseDeclarationHandlingUC, SUBMODEL, PARAMS extends BaseDeclParams>
        implements IDeclarationHandler<MODEL, DECL, SUBMODEL, UC, PARAMS> {
    public static String OP_BEAN_SUFFIX = "OperationHandler";

    protected UC useCase;
    protected Class<? extends SUBMODEL> modelClass;

    @Getter @Setter
    protected BaseOperationHandler operationHandler;


    public AbstractDeclarationHandler(Class<? extends SUBMODEL> modelClass) {
        this.modelClass = modelClass;
    }




    protected String getDeclarationTypeName() {
        return "Declaration type name";
    }

    protected void preInitModel(PARAMS params, MODEL model) { }
    protected abstract DTO initDeclarationWhenNew(MODEL model, PARAMS params);
    protected abstract DTO initDeclarationWhenEdit(MODEL model);
    protected abstract SUBMODEL initDeclarationModel();
    protected void postInitModel(MODEL model){ }

    protected abstract SUBMODEL getDeclarationFormModel();

    @Override
    public void setUseCase(UC declarationHandlingUC) {
        this.useCase = declarationHandlingUC;
    }

    @Override
    public void initLeftMenu(List<TreeElement<ElementCT>> leftMenu) {
    }

    @Override
    public void initOperationPendingToolbar() {
        useCase.buttonsFormManagement(true);
        String formClass = "pl.fhframework.dp.commons.fh.declaration.handling.PendingOperationForm";
        useCase.getForm().initPanel("operations", useCase.getModel(), formClass, "default");
    }

    /**
     * Abstract method providing form for main buttons panel.
     * If form is not null, panel will be shown and form assigned to it.
     * WARNING!!! Form should have container set to buttonsForm.
     * @param status
     * @param currentLocalAction
     * @return
     */
    public String resolveButtonsFormClass(String status, String currentLocalAction){
        switch (status) {
            case "DRAFT":
                return "pl.fhframework.dp.commons.fh.declaration.handling.ButtonsDraftForm";
            default:
                break;
        }
        return null;
    }


    /**
     * Abstract method resolving form class for operations based on status
     * Form class is a full qualified (with container package name) class name of the form represented by
     * selected frm file.
     *
     * @param status - current declaration status
     * @return
     */
    protected  String resolveOperationsFormClass(String status) {
        return null;
    }

    /**
     * Entry method for running declaration actions. Default is perform operation.
     * @param actionName
     * @param validate
     */
    @Override
    public void runAction(String actionName, boolean validate) {
        switch (actionName) {
            default: {
                String operationName = actionName + OP_BEAN_SUFFIX;
                operationHandler = useCase.getContext().getBean(operationName, BaseOperationHandler.class);
                if(operationHandler == null) {
                    useCase.getEventRegistry().fireNotificationEvent(NotificationEvent.Level.ERROR,
                            "No" + operationName + " operation handler found");
                } else {
                    operationHandler.setDeclarationHandlingUC(useCase);
                    operationHandler.performOperation();
                }
            }
        }
    }

    @Override
    public OperationStateResponseDto checkOperationState(String operationId) {
        FhLogger.debug("Checking operation state by handler {}", operationHandler.getClass().getName());
        return operationHandler.checkOperationState(operationId,
                useCase.getModel().getDeclarationId(),
                useCase.getModel().getProcessId());
    }

    public Long setSequenceNumber(List<?> list) throws IllegalAccessException {
        if(list.size() != 0){
            int lastIndex = list.size()-1;
            Object ob = list.get(lastIndex);

            for(Field field : ob.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.getName().equals("sequenceNumber") || field.getName().equals("goodsItemNumber")) {
                    Long nextIndex = (Long) field.get(ob);
                    return nextIndex + 1;
                }
            }
        }
        return Long.valueOf(list.size() + 1);
    }
}
