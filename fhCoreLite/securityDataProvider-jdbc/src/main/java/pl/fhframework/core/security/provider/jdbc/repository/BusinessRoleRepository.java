package pl.fhframework.core.security.provider.jdbc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fhframework.core.security.provider.jdbc.model.BusinessRole;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface BusinessRoleRepository extends JpaRepository<BusinessRole, Long> {

    BusinessRole findByRoleNameIgnoreCase(String roleName);

    List<BusinessRole> findBySubBusinessRoles_Id(Long id);

    default BusinessRole getInstance() {
        return new BusinessRole();
    }

}
