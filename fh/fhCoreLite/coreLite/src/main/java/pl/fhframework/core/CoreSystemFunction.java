package pl.fhframework.core;

import lombok.Getter;
import pl.fhframework.core.security.ISystemFunctionId;

/**
 * @author pawel.ruta
 */
public enum CoreSystemFunction implements ISystemFunctionId {

    // Enums
    FUN_CORE_RULES_PERSISTENCE_WRITE(CoreSystemFunction.CORE_RULES_PERSISTENCE_WRITE),
    FUN_CORE_RULES_PERSISTENCE_READ(CoreSystemFunction.CORE_RULES_PERSISTENCE_READ),
    FUN_CORE_RULES_PERSISTENCE_DELETE(CoreSystemFunction.CORE_RULES_PERSISTENCE_DELETE),
    FUN_CORE_SERVICES_NOTIFICATION(CoreSystemFunction.CORE_SERVICES_NOTIFICATION),
    FUN_CORE_SERVICES_USER(CoreSystemFunction.CORE_SERVICES_USER),
    FUN_CORE_MAIL_SENDING(CoreSystemFunction.CORE_MAIL_SENDING),
    FUN_CORE_LOGS(CoreSystemFunction.CORE_LOGS);

    // Functions names
    public static final String CORE_RULES_PERSISTENCE_WRITE = "core/rules/persistence/write";
    public static final String CORE_RULES_PERSISTENCE_READ = "core/rules/persistence/read";
    public static final String CORE_RULES_PERSISTENCE_DELETE = "core/rules/persistence/delete";
    public static final String CORE_SERVICES_NOTIFICATION = "core/services/notification";
    public static final String CORE_SERVICES_USER = "core/services/user";
    public static final String CORE_MAIL_SENDING = "core/mail/sending";
    public static final String CORE_LOGS = "core/logs";

    @Getter
    private String name;
    @Getter
    private CoreSystemFunction[] basicFunctions;

    CoreSystemFunction(String name, CoreSystemFunction... basicFunctions) {
        this.name = name;
        this.basicFunctions = basicFunctions;
    }

    @Override
    public ISystemFunctionId[] getFunctions() {
        return basicFunctions;
    }

}
