package pl.fhframework.dp.commons.services.operations;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.dp.commons.model.dao.OperationStepDAO;


@Configuration
@PropertySource("classpath:test.properties")
@EnableJpaRepositories(
        basePackages = "pl.fhframework.dp.commons.model",
        entityManagerFactoryRef = "icdtsEntityManager",
        transactionManagerRef = "icdtsTransactionManager")
@ComponentScan("pl.fhframework.dp.commons.model")
@EntityScan("pl.fhframework.dp.commons.model")
@EnableTransactionManagement
public class TestConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean icdtsEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(icdtsDataSource());
        em.setPackagesToScan(
                "pl.fhframework.dp.commons.model");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("spring.jpa.database-platform"));
   
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public DataSource icdtsDataSource() {
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager icdtsTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                icdtsEntityManager().getObject());
        return transactionManager;
    }	
	
	
    @Bean
    OperationStepDtoServiceJPA operationStepDtoServiceJPA() {
        return new OperationStepDtoServiceJPA();
    }

//    @Bean
//    OperationStepDAO operationStepDAO() {
//    	return new OperationStepDAO();
//    }
//    
//    @Bean
//    MessageService messageService() {
//        return new MessageService();
//    }
}
