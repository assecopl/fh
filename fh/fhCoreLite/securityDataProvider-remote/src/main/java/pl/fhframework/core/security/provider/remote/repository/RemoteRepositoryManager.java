package pl.fhframework.core.security.provider.remote.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.provider.remote.model.UserInfo;

import java.util.*;

/**
 * @author Tomasz Kozlowski (created on 30.07.2019)
 */
@Component
public class RemoteRepositoryManager {

    /** Security data repositories components sorted by its order value */
    private List<RemoteSecurityDataRepository> repositories;

    private final Comparator<UserInfo> userComparator = Comparator.comparing(UserInfo::getUsername);

    @Autowired
    public void setRepositories(List<RemoteSecurityDataRepository> repositories) {
        this.repositories = repositories;
        Collections.sort(this.repositories);
    }

    /** Finds user info by username in all repository according to order value */
    public UserInfo findUserByName(String username) {
        UserInfo userInfo;
        for (RemoteSecurityDataRepository repository : repositories) {
            try {
                userInfo = repository.findUserByName(username);
                if (userInfo != null) {
                    return userInfo;
                }
            } catch (Exception e) {
                // log error and search user in next repository
                FhLogger.errorSuppressed("Exception during user search in hybrid repository", e);
            }
        }
        return null;
    }

    /** Returns all users from all repositories */
    public List<UserInfo> findAllUsers() {
        Set<UserInfo> users = new HashSet<>();
        for (RemoteSecurityDataRepository repository : repositories) {
            try {
                users.addAll(repository.findAllUsers());
            } catch (Exception e) {
                // log error and search users in next repository
                FhLogger.errorSuppressed(e);
            }
        }
        List<UserInfo> result = new ArrayList<>(users);
        result.sort(userComparator);
        return result;
    }

    /** Returns all business roles from all repositories */
    public List<String> findAllBusinessRoles() {
        Set<String> roles = new HashSet<>();
        for (RemoteSecurityDataRepository repository : repositories) {
            try {
                roles.addAll(repository.findAllBusinessRoles());
            } catch (Exception e) {
                // log error and search roles in next repository
                FhLogger.errorSuppressed(e);
            }
        }
        List<String> result = new ArrayList<>(roles);
        Collections.sort(result);
        return result;
    }

    /** Returns repository source info */
    public String getRepositorySource() {
        if (repositories.size() > 1) {
            return "Remote Security Data Provider (repositories: " + repositories.size() + ")";
        } else {
            return repositories.get(0).getRepositorySource();
        }
    }

}
