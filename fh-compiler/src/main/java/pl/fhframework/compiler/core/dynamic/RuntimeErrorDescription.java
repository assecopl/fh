package pl.fhframework.compiler.core.dynamic;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by pawel.ruta on 2018-03-01.
 */
@Data
@AllArgsConstructor
public class RuntimeErrorDescription {
    private String path;

    private String pathDescription;

    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuntimeErrorDescription that = (RuntimeErrorDescription) o;

        if (!path.equals(that.path)) return false;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
