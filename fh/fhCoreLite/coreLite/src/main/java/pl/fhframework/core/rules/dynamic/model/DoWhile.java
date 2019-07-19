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

@Getter
@Setter
@XmlRootElement(name = "DoWhile", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DoWhile extends While implements Loop {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "DoWhileBlock";

    @Override
    protected String getTagName() {
        return DoWhile.TAG_NAME;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        DoWhile doWhileBlock = new DoWhile();
        doWhileBlock.convertFromBlocklyInternal(parsedBlock);

        return doWhileBlock;
    }
}