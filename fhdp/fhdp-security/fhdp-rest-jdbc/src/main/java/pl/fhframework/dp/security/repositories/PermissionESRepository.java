package pl.fhframework.dp.security.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.permissions.PermissionDto;

import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 27.01.2021)
 */
public interface PermissionESRepository extends ElasticsearchRepository<PermissionDto, Long> {

    @Query("{    \"match\": {\n" +
            "      \"businessRoleName.keyword\": \"?0\"\n" +
            "    }}")
    List<PermissionDto> findByRoleName(String businessRoleName);

}
