package pl.fhframework.core.security.provider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fhframework.core.security.provider.model.Permission;

import java.util.Collection;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByBusinessRoleNameIgnoreCase(String businessRoleName);

    List<Permission> findByModuleUUIDAndFunctionNameIn(String moduleUUID, Collection<String> functions);

    default Permission getInstance() {
        return new Permission();
    }

}
