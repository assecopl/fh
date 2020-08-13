package pl.fhframework.fhPersistence.core.model;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;

/**
 * Created by pawel.ruta on 2017-02-16.
 */
public class JpaDynamicEntityInformation implements JpaEntityInformation {
    @Override
    public SingularAttribute getIdAttribute() {
        return null;
    }

    @Override
    public SingularAttribute getRequiredIdAttribute() throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean hasCompositeId() {
        return false;
    }

    @Override
    public Iterable<String> getIdAttributeNames() {
        return null;
    }

    @Override
    public Object getCompositeIdAttributeValue(Object id, String idAttribute) {
        return null;
    }

    @Override
    public String getEntityName() {
        return null;
    }

    @Override
    public boolean isNew(Object o) {
        return true;
    }

    @Override
    public Serializable getId(Object o) {
        return null;
    }

    @Override
    public Object getRequiredId(Object entity) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Class getIdType() {
        return null;
    }

    @Override
    public Class getJavaType() {
        return null;
    }
}
