package pl.fhframework.dp.security.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.security.repositories.RoleESRepository;
import pl.fhframework.dp.security.utils.SimpleIdGenerator;
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
@Service
@AllArgsConstructor
public class RoleService {

    private final RoleESRepository roleESRepository;

    public RoleDto saveRole(RoleDto roleDto) {
        if (roleDto.getId() == null) {
            roleDto.setId(SimpleIdGenerator.generateId());
        }
        return roleESRepository.save(roleDto);
    }

    public List<RoleDto> findAllRoles() {
        return StreamSupport.stream(roleESRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<RoleDto> findRole(String roleName) {
        return roleESRepository.findByName(roleName);
    }

    public Optional<RoleDto> findRole(Long id) {
        return roleESRepository.findById(id);
    }

    public void deleteRole(RoleDto roleDto) {
        roleESRepository.delete(roleDto);
    }

}
