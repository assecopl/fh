package pl.fhframework.dp.commons.fh.declaration.handling;

import pl.fhframework.dp.commons.fh.outline.ElementCT;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.List;

public interface IDeclarationHandler<MODEL extends BaseDeclarationHandlingFormModel, DECL, SUBMODEL, UC, PARAMS extends BaseDeclParams> {
    //allows actions for proper model initiation
    void initModel(PARAMS params, MODEL model);
    void setUseCase(UC declarationHandlingUC);
    default void initLeftMenu(List<TreeElement<ElementCT>> leftMenu) {}
    default void initToolbar(){}
    default void setSelectedObject(Integer index, String objectName){}
    default void setSelectedObject(Integer index, String objectName, Integer parentIndex){}
    default void setSelectedGroup(Integer start, Integer end, String objectName){}
    default void resetSelectedGroup(){}
    void runAction(String actionName, boolean validate);
    String resolveVariant(SUBMODEL entity);
    String resolveVariant(String state);
    void performItemAction(String actionName, Object docFormModel);
    void performItemAction(String actionName, Object docFormModel, Object parm1);
    void performItemAction(String actionName, Object docFormModel, Object parm1, Object docFormModelParent);
    default OperationStateResponseDto checkOperationState(String operationId) {
        return null;
    }
    default String getMessagesForm() {
        return "pl.fhframework.dp.commons.fh.declaration.message.DeclarationMessageForm";
    }

    default boolean prepareActionLocal(String actionName) {return false;}

    String resolveButtonsFormClass(String status, String currentLocalAction);

    void initOperationPendingToolbar();

    void setOperationHandler(BaseOperationHandler operationHandler);

    void refreshEntity(Long id);

    default String getListLoaderClassName() {return null;}

    default AccessibilityEnum getOperationAccessibility(String opCode) {return AccessibilityEnum.EDIT;}

    default Object getIEx15Declaration(DECL declaration){
        return declaration;
    }

    default void performItemActionWithParams(String actionName, Object docFormModel, Object... params) {}
}
