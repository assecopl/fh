package pl.fhframework.fhPersistence.core.repository;

import org.springframework.web.context.request.RequestContextHolder;

import pl.fhframework.fhPersistence.core.EntityManagerRepository;

import javax.persistence.EntityManager;
import lombok.experimental.Delegate;
import pl.fhframework.SessionManager;

/**
 * Class that delegate calls to EntityManager, created for Spring Data Repository API.
 * If there is web context it uses conversation mechanism otherwise plain Jpa Entity Manager
 *
 * @author Paweł Ruta
 */
public class EntityManagerDelegate implements EntityManager {
    EntityManagerRepository emRepository;

    EntityManager emJpa;

    public EntityManagerDelegate(final EntityManagerRepository emRepository, final EntityManager emJpa) {
        this.emRepository = emRepository;

        this.emJpa = emJpa;
    }

    @Delegate
    private EntityManager delegateEm() {
        if (noSessionContext()) {
            return emJpa;
        }
        else {
            return emRepository.getEntityManager();
        }
    }

    private boolean noSessionContext() {
        return SessionManager.getSession() == null;
    }
}
