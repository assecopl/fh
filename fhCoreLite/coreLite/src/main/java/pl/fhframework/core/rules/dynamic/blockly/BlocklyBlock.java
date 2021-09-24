package pl.fhframework.core.rules.dynamic.blockly;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "block", propOrder = {"id", "type", "x", "y", "fields", "values", "statement", "next"})
@XmlRootElement(name = "block")
public class BlocklyBlock implements Serializable {
    @XmlAttribute
    private String id;

    @XmlElementRef
    private List<BlocklyField> fields = new ArrayList<>();

    @XmlElementRef
    private List<BlocklyValue> values = new ArrayList<>();

    @XmlElementRef
    private BlocklyStatement statement;

    @XmlElementRef
    private BlocklyNext next;

    @XmlTransient
    @JsonIgnore
    private Class<? extends RuleElement> aClass;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private Double y;

    @XmlAttribute
    private Double x;

    public Optional<String> getFieldValue(String name) {
        if (fields == null || fields.isEmpty()) {
            return Optional.empty();
        }

        for (BlocklyField field : fields) {
            if (field.getName().equals(name)) {
                if (field.getValue() == null) {
                    return Optional.empty();
                }

                return Optional.of(field.getValue());
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Block [id = " + id + ", fields = " + fields + ", type = " + type + ", y = " + y + ", x = " + x + "]";
    }
}
