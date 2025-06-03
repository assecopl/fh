package pl.fhframework.compiler.core.uc.dynamic.model.element.detail;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class QueryInput implements ISnapshotEnabled, Cloneable {

    @XmlValue
    private String query = "";

    public QueryInput(QueryInput other) {
        this.query = other.query;
    }

    @Override
    public Object clone() {
        return new QueryInput(this);
    }
}
