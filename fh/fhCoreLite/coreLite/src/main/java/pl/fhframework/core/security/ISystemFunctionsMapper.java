package pl.fhframework.core.security;

/**
 * Required mapper for module - system functions.
 */
public interface ISystemFunctionsMapper {

    /**
     * This method checks system's functions from provided mapper implementation based on given
     * String name.
     *
     * @param systemFunction - system's function name as String
     * @return mapped object of type ISystemFunctionId from. Can return null.
     */
    ISystemFunctionId getSystemFunction(String systemFunction);

    default ISystemFunctionId[] getAllSubsystemFunctions() {
        return new ISystemFunctionId[0];
    }

}
