package pl.fhframework.dp.security.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.login.UserDto;

import java.util.Optional;

/**
 * @author Tomasz Kozlowski (created on 28.01.2021)
 */
public interface UserESRepository extends ElasticsearchRepository<UserDto, Long> {

    Optional<UserDto> findByUsername(String username);

}
