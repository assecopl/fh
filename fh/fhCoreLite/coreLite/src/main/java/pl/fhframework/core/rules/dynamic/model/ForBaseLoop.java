package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@XmlTransient
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class ForBaseLoop extends StatementWithList implements Loop {
    @XmlAttribute
    private String iter;

    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();
}