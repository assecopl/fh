package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Model extends ParameterDefinition implements Cloneable {
    protected Model(Model other) {
        super(other);
    }

    @Override
    public Object clone() {
        return new Model(this);
    }
}
