package pl.fhframework.fhPersistence.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.fhPersistence.core.model.ModelConfig;
import pl.fhframework.fhPersistence.snapshots.EntitySnapshotListener;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@MappedSuperclass
@EntityListeners(EntitySnapshotListener.class)
public abstract class BasePersistentObject implements BaseEntity<Long>, ISnapshotEnabled, Serializable, Cloneable {

    @Id
    @Setter
    @Getter
    @Column(name = "ID", nullable = false)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
/*
    @GeneratedValue(strategy = GenerationType.TABLE, generator="bazowy")
    @TableGenerator(
            name="base",
            table="GENERATOR_TABLE",
            pkColumnName = "key",
            valueColumnName = "next",
            pkColumnValue="course",
            allocationSize=1
    )
*/
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_GENERATOR")
    private Long id;

    @Setter
    @Getter
    @Column(name = "VERSION", nullable = false)
    @Version
    private Long version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasePersistentObject)) return false;

        BasePersistentObject that = (BasePersistentObject) o;

        if (getId() == null && that.getId() == null) {
            return this == that;
        }
        else if (getId() == null || that.getId() == null) {
            return false;
        }

        return getId().equals(that.getId()) && ModelConfig.getEntityClass(this).equals(ModelConfig.getEntityClass(o));

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public Long getEntityId() {
        return getId();
    }

    @Override
    public void setEntityId(Long id) {
        setId(id);
    }
}



