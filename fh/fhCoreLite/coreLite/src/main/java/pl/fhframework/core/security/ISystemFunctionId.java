package pl.fhframework.core.security;

public interface ISystemFunctionId {

    /** Separator which is use in system function name for building functions hierarchy */
    String SEPARATOR = "/";

    /**
     * Defining name of systems' function that can be used on @UseCase or @Action
     * one day
     */
    String getName();

    /**
     * Getting system functions that are possible for that system function definition.
     *
     * @return array of system functions
     */
    ISystemFunctionId[] getFunctions();

    /**
     * Checks if this system functions fits to given systemFunction. Watch out, this method works in
     * recursive way.
     *
     * @param systemFunction - given system functions to check
     * @return true if this object fits to systemFunction. False otherwise.
     */
    default <T extends ISystemFunctionId> boolean fitTo(T systemFunction) {
        // watch out for closed cycles in recursion
        if (systemFunction == this) return true;
        else {
            for (ISystemFunctionId role : getFunctions()) {
                if (role.fitTo(systemFunction)) {
                    return true;
                }
            }
        }
        return false;
    }

}
