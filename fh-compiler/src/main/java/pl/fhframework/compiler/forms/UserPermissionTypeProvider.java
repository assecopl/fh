package pl.fhframework.compiler.forms;

/**
 * User permissions (fuctionalities) type provider implementation for binding parser
 */
public class UserPermissionTypeProvider {

    public static final String PERM_PREFIX = "PERM";

    public static final Class<?> PERM_MARKER_TYPE = IUserPermissionHintType.class;

    private static interface IUserPermissionHintType {}

}
