package pl.fhframework.app.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import pl.fhframework.app.config.repository.EnableFhJpaRepositories;
import pl.fhframework.fhPersistence.core.repository.BaseRepositoryFactoryBean;
import pl.fhframework.format.FhConverter;
import pl.fhframework.format.FhFormatter;

import java.lang.annotation.*;

/**
 * Created by pawel.ruta on 2018-05-07.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

@SpringBootApplication
@EnableWebSocket
@EnableWebSecurity
@EnableAsync
@EnableCaching
@ComponentScan(
        basePackages = {"pl.fhframework", "pl.fhframework", "${fh.component.scan}"},
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = FhFormatter.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = FhConverter.class)})
@EnableFhJpaRepositories(
        basePackages = {"${fh.repository.scan}"},
        entityManagerFactoryRef = "fhEntityManagerFactory",
        transactionManagerRef = "fhTransactionManager",
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@PropertySource("classpath:config/fh-application.properties")
public @interface FhApplication {
}
