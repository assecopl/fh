package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.RequiredArgsConstructor;
import pl.fhframework.dp.commons.fh.adm.i18n.AdmMessageHelper;
import pl.fhframework.dp.commons.fh.adm.security.FhAdmSystemFunction;
import pl.fhframework.dp.commons.fh.adm.service.AdmRoleManagementService;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.transport.roles.RoleDto;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.model.PresentationStyleEnum;

/**
 * @author Tomasz Kozlowski (created on 04.03.2021)
 */
@UseCase
@RequiredArgsConstructor
@SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
public class CreateRoleUC extends FhdpBaseUC implements IUseCaseNoInput<IUseCaseSaveCancelCallback<RoleDto>> {

    private RoleDto model;

    private final AdmRoleManagementService roleManagementService;
    private final AdmMessageHelper messageHelper;

    @Override
    public void start() {
        model = new RoleDto();
        showForm(CreateRoleForm.class, model);
    }

    @Action
    public void confirm() {
        if (validate()) {
            RoleDto roleDto = roleManagementService.saveRole(model);
            exit().save(roleDto);
        }
    }

    private boolean validate() {
        if (roleManagementService.findRoleByName(model.getRoleName()) != null) {
            reportValidationError(model, "roleName", messageHelper.getMessage(AdmMessageHelper.ROLE_EXISTS_MESSAGE), PresentationStyleEnum.ERROR);
        }

        return !getUserSession().getValidationResults().areAnyValidationMessages();
    }

    @Action(validate = false)
    public void cancel() {
        exit().cancel();
    }

}
