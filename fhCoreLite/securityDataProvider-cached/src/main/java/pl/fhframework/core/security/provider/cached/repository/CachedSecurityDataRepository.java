package pl.fhframework.core.security.provider.cached.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.provider.remote.model.UserInfo;
import pl.fhframework.core.security.provider.remote.repository.RemoteSecurityDataRepository;

import java.util.*;

/**
 * @author Tomasz Kozlowski (created on 05.06.2019)
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unused"})
public class CachedSecurityDataRepository implements RemoteSecurityDataRepository {

    /** Cache for users information */
    private final UserInfoCachedRepository userInfoCachedRepository;
    /** Cache for roles information */
    private final RoleCachedRepository roleCachedRepository;
    /** Environment with system properties */
    private final Environment environment;
    /** Password encoder */
    private PasswordEncoder passwordEncoder;

    @Value("${fhframework.security.provider.cached.order:0}")
    private int order;
    @Value("${fhframework.security.provider.remote.pass.encode:true}")
    boolean passEncode;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // U S E R S
    // -------------------------------------------------------------

    /** Finds information about given user in cache */
    @Override
    public UserInfo findUserByName(String username) {
        return userInfoCachedRepository.findAll().get(username);
    }

    @Override
    public List<UserInfo> findAllUsers() {
        return new ArrayList<>(userInfoCachedRepository.findAll().values());
    }

    /** Puts given user and roles information into cache */
    private void putUserInfo(UserInfo userInfo) {
        Map<String, UserInfo> users = userInfoCachedRepository.findAll();
        users.put(userInfo.getUsername(), userInfo);
        userInfoCachedRepository.put(users);
        putRoles(userInfo.getRoles());
    }

    // R O L E S
    // -------------------------------------------------------------

    /** Returns all roles stored in cache */
    @Override
    public List<String> findAllBusinessRoles() {
        return new ArrayList<>(roleCachedRepository.findAll());
    }

    /** Puts new roles into cache */
    private void putRoles(List<String> roles) {
        if (!CollectionUtils.isEmpty(roles)) {
            // read roles from cache
            Set<String> cachedRoles = roleCachedRepository.findAll();
            // add new roles
            cachedRoles.addAll(roles);
            // put updated set of roles into cache
            roleCachedRepository.put(cachedRoles);
        }
    }

    // INIT
    // -------------------------------------------------------------

    @Value("${fhframework.security.provider.cached.max-users:100}")
    private int maxUsers;
    private static final String USERNAME = "username";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PASSWORD = "pass";
    private static final String ROLES = "roles";
    private static final String PREFIX = "fhframework.security.provider.cached.user";

    /** Load user information from application properties */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent refreshedEvent) {
        try {
            for (int i = 0; i < maxUsers; i++) {
                String username = readProperty(USERNAME, i);
                String firstName = readProperty(FIRST_NAME, i);
                String lastName = readProperty(LAST_NAME, i);
                String password = readProperty(PASSWORD, i);
                String[] roles = readProperty(ROLES, i).split(",");
                createUserInfo(username, firstName, lastName, password, roles);
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    /** Returns property value for given name and index*/
    private String readProperty(String name, int index) {
        return environment.getRequiredProperty(PREFIX + "." + index + "." + name);
    }

    /** Creates user info object and puts it into cache */
    private void createUserInfo(String username, String firstName, String lastName, String password, String... roles) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passEncode ? passwordEncoder.encode(password) : password);
        user.setBlocked(false);
        user.setDeleted(false);
        user.setRoles(Arrays.asList(roles));
        putUserInfo(user);
    }

    // -------------------------------------------------------------

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getRepositorySource() {
        return "Cached Security Data Repository";
    }

}

