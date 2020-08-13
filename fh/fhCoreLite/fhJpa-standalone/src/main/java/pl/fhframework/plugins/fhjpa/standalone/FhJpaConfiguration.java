package pl.fhframework.plugins.fhjpa.standalone;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import pl.fhframework.config.IFhConfiguration;

import javax.sql.DataSource;

/**
 * Created by pawel.ruta on 2018-05-08.
 */
public interface FhJpaConfiguration extends IFhConfiguration {
    default void configEntityManagerFactoryBuilder(EntityManagerFactoryBuilder factoryBuilder,
                    @Qualifier("fhDataSource") DataSource fhDataSource, SpringJtaPlatform springJtaPlatform) {
    }

    default EntityManagerFactoryBuilder.Builder configDefaultBuilder(EntityManagerFactoryBuilder.Builder builder,
                    @Qualifier("fhDataSource") DataSource fhDataSource, SpringJtaPlatform springJtaPlatform) {
        return builder;
    }
}
