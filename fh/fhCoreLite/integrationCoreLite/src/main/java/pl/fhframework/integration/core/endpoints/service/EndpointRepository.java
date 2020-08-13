package pl.fhframework.integration.core.endpoints.service;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.fhframework.integration.IEndpointAccess;
import pl.fhframework.integration.core.endpoints.model.Endpoint;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-04-12.
 */
@Profile("!withoutDataSource")
public interface EndpointRepository extends JpaRepository<Endpoint, Long>, IEndpointAccess {
    Endpoint findOneByName(String name);

    @Query("select endpoint.name from Endpoint endpoint order by endpoint.name asc")
    List<String> findAllNames();

    @Query("select endpoint.url from Endpoint endpoint where endpoint.name = ?1")
    String findOneUrlByName(String name);
}
