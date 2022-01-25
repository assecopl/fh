package pl.fhframework.dp.commons.model.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * User: dariuszs
 * Date: 28.10.11
 * Time: 11:41
 *
 * @version $Revision: 1381 $, $Date: 2018-10-12 22:31:29 +0200 (Fri, 12 Oct 2018) $
 */
public class SemaphorePK implements Serializable {
    private static final long serialVersionUID = 1;

    private String type;
    private String key;

    public SemaphorePK() {
    }

    public SemaphorePK(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemaphorePK)) return false;

        SemaphorePK semaphorePK = (SemaphorePK) o;

        if (!Objects.equals(key, semaphorePK.key)) return false;
        if (type != semaphorePK.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
