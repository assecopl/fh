package pl.fhframework.core.security.provider.remote.repository;

import pl.fhframework.core.security.provider.remote.model.UserInfo;

import java.util.List;

/**
 * An interface for remote repository with security data.
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
public interface RemoteSecurityDataRepository extends Comparable<RemoteSecurityDataRepository>{

    /** Finds user info by username */
    UserInfo findUserByName(String username);

    /** Finds all users */
    List<UserInfo> findAllUsers();

    /** Finds all business roles */
    List<String> findAllBusinessRoles();

    /** Gets repository source info, e.g. URL to REST service */
    String getRepositorySource();

    /** Returns component order value, which determines order of search execution if there is more thane one remote repository. Default returns 0. */
    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(RemoteSecurityDataRepository repo) {
        return repo != null ? this.getOrder() - repo.getOrder() : 1;
    }

}
