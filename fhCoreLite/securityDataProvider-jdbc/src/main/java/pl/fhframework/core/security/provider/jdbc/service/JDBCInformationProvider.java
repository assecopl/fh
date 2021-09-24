package pl.fhframework.core.security.provider.jdbc.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Keeps information of an underling JDBC source.
 */
@Service
public class JDBCInformationProvider {

    @Qualifier("fhDataSource")
    @Autowired
    private DataSource dataSource;

    @Getter
    private String url;

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            url = connection.getMetaData().getURL();
        } catch (SQLException e) {
            FhLogger.error("Cannot get data source URL for information purpose: " + e.getMessage());
        }
    }
}
