package pl.fhframework.core.security.permission.jdbc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fhframework.core.security.permission.jdbc.model.Permission;

import java.util.Collection;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 16.10.2019)
 */
public interface JpaPermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByBusinessRoleNameIgnoreCase(String businessRoleName);

    List<Permission> findByModuleUUIDAndFunctionNameIn(String moduleUUID, Collection<String> functions);

}
