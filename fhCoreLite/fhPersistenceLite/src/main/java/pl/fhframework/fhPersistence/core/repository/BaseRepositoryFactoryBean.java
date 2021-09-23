package pl.fhframework.fhPersistence.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.fhPersistence.core.model.JpaDynamicEntityInformation;
import pl.fhframework.ReflectionUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Override of JpaRepositoryFactoryBean, so that coversation mechanism can be used with Spring Data Repository
 *
 * @author Paweł Ruta
 */
public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T,
        I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

    @Autowired(required = false)
    EntityManagerRepository emRepository;

    public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        EntityManager emDelegated = new EntityManagerDelegate(emRepository, em);

        return new BaseRepositoryFactory(emDelegated);
    }

    private static class BaseRepositoryFactory<T, I extends Serializable>
            extends JpaRepositoryFactory {

        private final EntityManager delegatedEm;
        public BaseRepositoryFactory(EntityManager delegatedEm) {
            super(delegatedEm);
            this.delegatedEm = delegatedEm;
        }

        @Override
        protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            JpaEntityInformation<?, Serializable> entityInformation;
            // if it is fake repository of BaseEntity then return fake EntityInformation
            if (information.getDomainType().isAssignableFrom(BaseEntity.class) ||
                    ReflectionUtils.isGeneratedDynamicClass(information.getDomainType())){
                entityInformation = new JpaDynamicEntityInformation();
            }
            else {
                entityInformation = getEntityInformation(information.getDomainType());
            }
            return new BaseRepositoryImpl(entityInformation, delegatedEm);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseRepositoryImpl.class;
        }
    }
}