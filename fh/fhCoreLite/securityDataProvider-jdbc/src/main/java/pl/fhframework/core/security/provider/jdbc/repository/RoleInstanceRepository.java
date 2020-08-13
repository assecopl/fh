package pl.fhframework.core.security.provider.jdbc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fhframework.core.security.provider.jdbc.model.BusinessRole;
import pl.fhframework.core.security.provider.jdbc.model.RoleInstance;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2017-11-29)
 */
public interface RoleInstanceRepository extends JpaRepository<RoleInstance, Long> {

    List<RoleInstance> findByBusinessRole(BusinessRole businessRole);

    default RoleInstance getInstance() {
        return new RoleInstance();
    }

}
