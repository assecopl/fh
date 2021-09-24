package pl.fhframework.core.security;

import lombok.AccessLevel;
import lombok.Setter;
import pl.fhframework.core.security.AuthorizationManager.Function;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Component defines which functionality are available in application and which are not.
 * @author Tomasz.Kozlowski (created on 24.10.2018)
 */
@SuppressWarnings("WeakerAccess")
public class ApplicationPrivilegesCache {

    /** A collection of system functions allowed to perform in application */
    private final Set<Function> allowFunctions;
    /** A collection of system functions denied to perform in application */
    private final Set<Function> deniedFunctions;
    /** A global privilege flag.
     *    <p>- <code>null</code>  : privileges depend of functions from allow and denied collections</p>
     *    <p>- <code>true</code>  : allow all application functions</p>
     *    <p>- <code>false</code> : denied all application functions</p>
     */
    @Setter(AccessLevel.PACKAGE)
    private Boolean globalPrivilege;

    ApplicationPrivilegesCache() {
        this.allowFunctions = new HashSet<>();
        this.deniedFunctions = new HashSet<>();
    }

    /**
     * Checks whether application has prvilages to access given system function.
     * @param verifiedFunction verified system function
     * @return <code>true</code> if application has access to given function.
     */
    boolean isValidFunctionForApplication(Function verifiedFunction) {
        if (globalPrivilege == null) {

            // check denied functions
            for (Function applicationFunction : deniedFunctions) {
                if (isMatching(verifiedFunction, applicationFunction)) {
                    return false;
                }
            }

            // check allowed functions
            for (Function applicationFunction : allowFunctions) {
                if (isMatching(verifiedFunction, applicationFunction)) {
                    return true;
                }
            }

            return false;
        } else {
            return globalPrivilege;
        }
    }

    void addFunction(Function function) {
        if (function.isDenial()) {
            deniedFunctions.add(function);
        } else {
            allowFunctions.add(function);
        }
    }

    private boolean isMatching(Function verifiedFunction, Function applicationFunction) {
        return Objects.equals(verifiedFunction.getModuleUUID(), applicationFunction.getModuleUUID()) &&
                (verifiedFunction.getName().equals(applicationFunction.getName()) || verifiedFunction.getName().startsWith(applicationFunction.getName() + "/"));
    }

}
