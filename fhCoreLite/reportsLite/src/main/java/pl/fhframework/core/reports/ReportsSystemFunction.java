package pl.fhframework.core.reports;

import lombok.Getter;
import pl.fhframework.core.security.ISystemFunctionId;

/**
 * @author pawel.ruta
 */
public enum ReportsSystemFunction implements ISystemFunctionId {

    // Enums
    FUN_REPORTS_SERVICES_GENERATE(ReportsSystemFunction.REPORTS_SERVICES_GENERATE);

    // Functions names
    public static final String REPORTS_SERVICES_GENERATE = "reportsCore/services/generate";

    @Getter
    private String name;
    @Getter
    private ReportsSystemFunction[] basicFunctions;

    ReportsSystemFunction(String name, ReportsSystemFunction... basicFunctions) {
        this.name = name;
        this.basicFunctions = basicFunctions;
    }

    @Override
    public ISystemFunctionId[] getFunctions() {
        return basicFunctions;
    }

}
