package pl.fhframework.plugins.fhjpa.jee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.fhframework.aop.services.FhAspectsOrder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
@Configuration
@EnableTransactionManagement(order = FhAspectsOrder.TRANSACTION)
@EnableJpaRepositories(basePackages = {
        "pl.fhframework.fhPersistence.core.model"
        },
        entityManagerFactoryRef = "fhEntityManagerFactory",
        transactionManagerRef = "fhTransactionManager")
@Profile("!withoutDataSource")
public class FHJEEDatabaseConfiguration {

    @Value("${fh.dataSource.jndi:java:/FHDS}")
    private String jndi;

    @Value("${fh.dataSource.dialect:org.hibernate.dialect.H2Dialect}")
    private String dialect;

    @Value("${fh.dataSource.hbm2ddl:update}")
    private String hbm2ddl;

    @Value("${fh.dataSource.providerClassName:org.hibernate.ejb.HibernatePersistence}")
    private String providerClassName;

    @Value("${fh.entity.scan}")
    private String entityPackages;

    @Bean
    @Primary
    @Qualifier("fhDataSource")
    public DataSource fhDataSource() {
      try {
        return (DataSource) new InitialContext().lookup(jndi);
      } catch (NamingException e) {
        throw new IllegalStateException("No data source on " + jndi, e);
      }
    }

    @Bean
    @Primary
    @Qualifier("fhEntityManagerFactory")
    @SuppressWarnings({"Duplicates","unchecked"})
    public EntityManagerFactory fhEntityManagerFactory(@Autowired @Qualifier("fhDataSource") DataSource fhDataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("FhPU");
        factory.setJtaDataSource(fhDataSource);
        factory.setPackagesToScan(entityPackages.split(","));

        Class<?> providerClass;
        try {
          providerClass = Class.forName(providerClassName);
        } catch (ClassNotFoundException e) {
          throw new IllegalStateException(e);
        }

        factory.setPersistenceProviderClass((Class<? extends PersistenceProvider>) providerClass);
        Properties props = new Properties();
//        props.put("hibernate.current_session_context_class", "jta");
        props.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform");
        props.put("hibernate.dialect", dialect);
        props.put("hibernate.hbm2ddl.auto", hbm2ddl);

        factory.setJpaProperties(props);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    @Primary
    @Qualifier("fhTransactionManager")
    public PlatformTransactionManager fhTransactionManager(@Qualifier("fhEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
