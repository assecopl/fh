package pl.fhframework.core.rules.dynamic.blockly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "next")
@XmlRootElement(name = "next")
@AllArgsConstructor
@NoArgsConstructor
public class BlocklyNext {
    @XmlElementRef
    private BlocklyBlock block;

    @Override
    public String toString() {
        return "BlocklyStatement{" + "block=" + block + '}';
    }
}
