package pl.fhframework.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import pl.fhframework.core.security.IDefaultUser;

import java.util.Collections;
import java.util.List;

/**
 * Allows to configure additional web properties. FH module should define @Components that implements this interface
 */
public interface FhWebConfiguration extends IFhConfiguration {
    /**
     * Provides list of String ant patterns matching request url that will be publicly accessible
     *
     * @return the list of the ant patterns matching request url publicly accessible
     */
    default List<String> permitedToAllRequestUrls() {
        return Collections.emptyList();
    }

    /**
     * Additional custom HttpSecurity configuration
     *
     * @param http HttpSecurity configuration
     */
    default void configure(HttpSecurity http) {

    }

    /**
     * Provides list of default users to create when database is empty.
     * NOTE: This functionality works only for JDBC Security Data Provider and when
     * <code>fhframework.security.provider.generate-default-data</code> property is set to <code>true</code>.
     * @return list of default users.
     */
    default List<IDefaultUser> getDefaultUsers() {
        return Collections.emptyList();
    }

}
