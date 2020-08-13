package pl.fhframework.fhPersistence.core.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * Base repository interface, for changing default Spring Data Repository behaviour
 *
 * @author Paweł Ruta
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends Repository<T, ID> {
}