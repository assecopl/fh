package pl.fhframework.dp.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.security.repositories.UserESRepository;
import pl.fhframework.dp.security.utils.SimpleIdGenerator;
import pl.fhframework.dp.transport.login.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Tomasz Kozlowski (created on 28.01.2021)
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserESRepository userESRepository;

    public UserDto saveUser(UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(SimpleIdGenerator.generateId());
        }
        return userESRepository.save(userDto);
    }

    public Optional<UserDto> findUser(String username) {
        return userESRepository.findByUsername(username);
    }

    public List<UserDto> findAllUsers() {
        return StreamSupport.stream(userESRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteUser(UserDto userDto) {
        userESRepository.delete(userDto);
    }

}
