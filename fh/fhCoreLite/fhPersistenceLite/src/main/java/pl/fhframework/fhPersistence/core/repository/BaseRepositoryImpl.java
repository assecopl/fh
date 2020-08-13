package pl.fhframework.fhPersistence.core.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import pl.fhframework.fhPersistence.core.EntityManagerRepositoryImpl;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * Base repository implementation, for changing default Spring Data Repository behaviour. It allows for using
 * conversation mechanism if available.
 *
 * @author Paweł Ruta
 */
public class BaseRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    private final JpaEntityInformation<T, ?> entityInformation;

    public BaseRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation, final EntityManager emDelegowany) {
        super(entityInformation, emDelegowany);

        this.entityInformation = entityInformation;
        this.entityManager = emDelegowany;
    }

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);
    }

    @Override
    public <S extends T> S save(S entity) {
        if (!EntityManagerRepositoryImpl.isConversationOn()) {
            return super.save(entity);
        }

        entityManager.persist(entity);
        return entity;
    }
}