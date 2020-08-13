package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@Getter
@Setter
@XmlRootElement(name = "Const", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Const extends Var implements Serializable {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ConstBlock";

    public Const() {
        setInit(true);
    }

    @Override
    protected String getTagName() {
        return Const.TAG_NAME;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        Const constBlock = new Const();
        constBlock.convertFromBlocklyInternal(parsedBlock);

        return constBlock;
    }
}