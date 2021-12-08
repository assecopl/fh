package pl.fhframework.dp.security.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.Optional;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
public interface RoleESRepository extends ElasticsearchRepository<RoleDto, Long> {

//    @Query("{\"bool\": {\"must\": {\"terms\": {\"roleName\":\"?0\"}}}}")
    @Query("{    \"match\": {\n" +
            "      \"roleName.keyword\": \"?0\"\n" +
            "    }}")
    Optional<RoleDto> findByName(String roleName);

}
