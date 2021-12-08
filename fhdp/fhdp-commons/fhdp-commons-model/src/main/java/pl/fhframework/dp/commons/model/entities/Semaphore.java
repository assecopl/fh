/*
 * Semafor.java
 *
 * Created on 23 styczeń 2008, 18:13
 *
 */

package pl.fhframework.dp.commons.model.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Semaphore entity.
 */
@Entity()
@IdClass(SemaphorePK.class)
@Table(name = "fhdp_semaphores")
public class Semaphore implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Long version = 0L;
    private String type;
    private String key;
    private String value;
    private LocalDateTime lockTime;

    public Semaphore() {
    }

    public Semaphore(String type, String key, String value, LocalDateTime lockTime) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.lockTime = lockTime;
    }

    public Semaphore(Enum enumType, String key, String value, LocalDateTime lockTime) {
        this.type = enumType.name();
        this.key = key;
        this.value = value;
        this.lockTime = lockTime;
    }

    @Version
    @Column(name = "version", nullable = false)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "semaphore_type", nullable = false, length = 20)
    @Id
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "semaphore_key", nullable = false, length = 255)
    @Id
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(name = "semaphore_value", nullable = false, length = 80)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "lock_tm")
    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semaphore)) return false;

        Semaphore semaphore = (Semaphore) o;
        
        if(key == null || !key.equals(semaphore.key)) return false;
        if(type == null || !type.equals(semaphore.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Semaphore{")
                .append("type=").append(type).append(",")
                .append("key=").append(key).append(",")
                .append("value=").append(value).append(",")
                .append("lockTime=").append(lockTime).append(",")
                .append("}").toString();
    }
}
