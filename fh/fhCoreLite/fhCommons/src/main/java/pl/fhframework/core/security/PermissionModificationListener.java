package pl.fhframework.core.security;

/**
 * Created by pawel.ruta on 2018-02-15.
 */
public interface PermissionModificationListener {
    void onRoleChange(String roleName);
}
