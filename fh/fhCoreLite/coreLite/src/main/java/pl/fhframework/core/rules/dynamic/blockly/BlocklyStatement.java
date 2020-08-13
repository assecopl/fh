package pl.fhframework.core.rules.dynamic.blockly;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statement")
@XmlRootElement(name = "statement")
public class BlocklyStatement {
    @XmlElementRef
    private List<BlocklyBlock> blocks = new ArrayList<>();

    @XmlAttribute
    private String name = "value";

    @Override
    public String toString() {
        return "BlocklyStatement{blocks=" + blocks + '}';
    }
}
