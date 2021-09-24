package pl.fhframework.core.rules.dynamic.blockly;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "value", propOrder = {"name", "value"})
@XmlRootElement(name = "value")
public class BlocklyValue implements Serializable {
    @XmlAttribute
    private String name;

    @XmlElementRef
    private BlocklyBlock value;


    @SuppressWarnings("unused")
    public BlocklyValue() {
    }

    public BlocklyValue(String name, BlocklyBlock value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "BlocklyValue [name = " + name + ", value = " + value.toString() + "]";
    }
}
