package pl.fhframework.docs;

import pl.fhframework.core.security.ISystemFunctionId;

import lombok.Getter;

public enum DocsSystemFunction implements ISystemFunctionId {

    // Functionality enum
    FUN_DOCUMENTATION_VIEW(DocsSystemFunction.FH_DOCUMENTATION_VIEW),

    // Test enums
    FUN_HIDDEN_MESSAGE(DocsSystemFunction.FH_DOCUMENTATION_HIDDEN_MESSAGE),
    FUN_CONSTRAIN_TEST(DocsSystemFunction.FH_DOCUMENTATION_CONSTRAIN_TEST);

    // Functions names
    public static final String FH_DOCUMENTATION_VIEW = "fh/documentation/view";
    public static final String FH_DOCUMENTATION_HIDDEN_MESSAGE = "fh/documentation/hidden/message";
    public static final String FH_DOCUMENTATION_CONSTRAIN_TEST = "fh/documentation/constrain/test";


    DocsSystemFunction(String name, DocsSystemFunction... basicRoles) {
        this.name = name;
        this.basicRoles = basicRoles;
    }

    @Getter
    private String name;

    @Getter
    private DocsSystemFunction[] basicRoles;

    @Override
    public ISystemFunctionId[] getFunctions() {
        return basicRoles;
    }

}
