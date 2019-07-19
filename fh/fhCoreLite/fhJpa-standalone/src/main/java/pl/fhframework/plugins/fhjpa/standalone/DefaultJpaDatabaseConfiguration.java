package pl.fhframework.plugins.fhjpa.standalone;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import pl.fhframework.aop.services.FhAspectsOrder;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
@Configuration
@EnableTransactionManagement(order = FhAspectsOrder.TRANSACTION)
@EnableAutoConfiguration
@EnableConfigurationProperties(AtomikosProperties.class)
@EnableScheduling
public class DefaultJpaDatabaseConfiguration {
    @Value("${spring.jpa.database-platform:org.hibernate.dialect.H2Dialect}")
    private String dialect;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Bean
    @Qualifier("fhDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource fhDataSource() {
        return new AtomikosNonXADataSourceBean();
    }

    @Value("${fh.entity.scan}")
    private String entityPackages;

    @Autowired(required = false)
    List<FhJpaConfiguration> jpaConfigurations = new ArrayList<>();

    @Bean
    @DependsOn("fhTransactionManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean fhEntityManagerFactory(EntityManagerFactoryBuilder factoryBuilder, @Qualifier("fhDataSource") DataSource fhDataSource, SpringJtaPlatform springJtaPlatform) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.transaction.jta.platform", springJtaPlatform);
        properties.put("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT");
        properties.put("show_sql", showSql);
        properties.put("format_sql", "true");

        EntityManagerFactoryBuilder.Builder builder = factoryBuilder.dataSource(fhDataSource)
                .persistenceUnit("FhPU")
                .packages(entityPackages.split(","))
                .properties(properties)
                .jta(true);

        jpaConfigurations.forEach(fhJpaConfiguration -> fhJpaConfiguration.configDefaultBuilder(builder, fhDataSource, springJtaPlatform));

        jpaConfigurations.forEach(fhJpaConfiguration -> fhJpaConfiguration.configEntityManagerFactoryBuilder(factoryBuilder, fhDataSource, springJtaPlatform));

        return builder.build();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager userTransactionManager(UserTransactionService userTransactionService) {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setStartupTransactionService(false);
        manager.setForceShutdown(true);
        return manager;
    }


    @Bean(initMethod = "init", destroyMethod = "shutdownForce")
    public UserTransactionServiceImp userTransactionService(
            AtomikosProperties atomikosProperties) {
        Properties properties = new Properties();
        properties.putAll(atomikosProperties.asProperties());

        String tmpDir = createTmpDir();

        properties.put("com.atomikos.icatch.output_dir", tmpDir);
        properties.put("com.atomikos.icatch.log_base_dir", tmpDir);
        properties.put("com.atomikos.icatch.default_jta_timeout", "300000");
        properties.put("com.atomikos.icatch.max_timeout", "300000");
        return new UserTransactionServiceImp(properties);
    }

    private String createTmpDir() {
        try {
            return Files.createTempDirectory("database-tmp").toAbsolutePath().toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }


    @Bean("fhTransactionManager")
    @Primary
    public JtaTransactionManager transactionManager(UserTransactionManager userTransactionManager) {
        JtaTransactionManager transactionManager = new JtaTransactionManager();
        transactionManager.setTransactionManager(userTransactionManager);
        transactionManager.setUserTransaction(new J2eeUserTransaction());

        return transactionManager;
    }

    @Bean
    SpringJtaPlatform springJtaPlatform(JtaTransactionManager transactionManager) {
        return new SpringJtaPlatform(transactionManager);
    }
}