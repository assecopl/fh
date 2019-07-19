package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMixed;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class SimpleStatement extends Statement implements Serializable {
    @XmlMixed
    private List<String> values = new ArrayList<>();

    public String getValue() {
        if (values.size() == 1) {
            return values.get(0);
        }

        return null;
    }

    public void setValue(String value) {
        values.clear();
        if (value != null) {
            values.add(value);
        }
    }
}
