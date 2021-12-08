package pl.fhframework.dp.commons.fh.adm.security;

import lombok.Getter;
import pl.fhframework.core.security.ISystemFunctionId;

@Getter
public enum FhAdmSystemFunction implements ISystemFunctionId {

    FUN_ADM_PERMISSIONS_VIEW(FhAdmSystemFunction.ADM_PERMISSIONS_VIEW),
    FUN_ADM_PERMISSIONS_EDIT(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT);

    public static final String ADM_PERMISSIONS_VIEW = "administration/permissions/view";
    public static final String ADM_PERMISSIONS_EDIT = "administration/permissions/edit";

    FhAdmSystemFunction(String name, FhAdmSystemFunction... basicRoles) {
        this.name = name;
        this.basicRoles = basicRoles;
    }

    private final String name;
    private final FhAdmSystemFunction[] basicRoles;

    @Override
    public ISystemFunctionId[] getFunctions() {
        return basicRoles;
    }

}
