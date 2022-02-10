package pl.fhframework.dp.commons.model.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BasePersistentObject implements Serializable, Cloneable {

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

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : super.hashCode();
    }

}



