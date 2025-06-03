package pl.fhframework.compiler.forms;

/**
 * User roles type provider implementation for binding parser
 */
public class UserRoleTypeProvider {

    public static final String ROLE_PREFIX = "ROLE";

    public static final Class<?> ROLE_MARKER_TYPE = IUserRoleHintType.class;

    private static interface IUserRoleHintType {}

}
