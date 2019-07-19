package pl.fhframework.core.security.provider.jdbc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fhframework.core.security.provider.jdbc.model.UserAccount;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    UserAccount findByLogin(String login);

    default UserAccount getInstance() {
        return new UserAccount();
    }

}
