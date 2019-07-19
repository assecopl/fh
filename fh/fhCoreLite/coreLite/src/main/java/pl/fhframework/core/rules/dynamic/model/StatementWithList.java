package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@XmlTransient
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class StatementWithList extends Statement implements StatementsList {
    @Override
    public Class getInputType(String name) {
        if ("statements".equals(name)) {
            return StatementsList.class;
        }

        return Statement.class;
    }
}