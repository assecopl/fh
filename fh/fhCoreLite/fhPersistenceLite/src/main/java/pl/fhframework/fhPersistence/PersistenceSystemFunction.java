package pl.fhframework.fhPersistence;

import lombok.Getter;
import pl.fhframework.core.security.ISystemFunctionId;

/**
 * @author pawel.ruta
 */
public enum PersistenceSystemFunction implements ISystemFunctionId {

    // Enums
    FUN_CORE_SERVICES_CHANGES(PersistenceSystemFunction.CORE_SERVICES_CHANGES);

    // Functions names
    public static final String CORE_SERVICES_CHANGES = "core/services/changes"; // NOT USED AT THE MOMENT

    @Getter
    private String name;
    @Getter
    private PersistenceSystemFunction[] basicFunctions;

    PersistenceSystemFunction(String name, PersistenceSystemFunction... basicFunctions) {
        this.name = name;
        this.basicFunctions = basicFunctions;
    }

    @Override
    public ISystemFunctionId[] getFunctions() {
        return basicFunctions;
    }

}
