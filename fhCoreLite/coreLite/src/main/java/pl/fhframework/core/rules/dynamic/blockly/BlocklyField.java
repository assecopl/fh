package pl.fhframework.core.rules.dynamic.blockly;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "field")
@XmlRootElement(name = "field")
public class BlocklyField implements Serializable {
    public static enum EditorType {TEXT, FIXED, COMBO, COMBO_FIXED}

    @XmlAttribute
    private String name;
    @XmlValue
    private String value;


    //editor config
    @XmlTransient
    @JsonIgnore
    private EditorType editorType = EditorType.TEXT;

    @XmlTransient
    @JsonIgnore
    private boolean required = false;

    @XmlTransient
    @JsonIgnore
    private String typeMask;

    @XmlTransient
    @JsonIgnore
    private boolean emptyValueAllowed = true;

    @XmlTransient
    @JsonIgnore
    private Class fieldType;

    public BlocklyField setEditorType(EditorType editorType) {
        this.editorType = editorType;
        return this;
    }

    public BlocklyField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public BlocklyField setTypeMask(String typeMask) {
        this.typeMask = typeMask;
        return this;
    }

    public BlocklyField setEmptyValueAllowed(boolean emptyValueAllowed) {
        this.emptyValueAllowed = emptyValueAllowed;
        return this;
    }

    public BlocklyField setFieldType(Class fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    @SuppressWarnings("unused")
    public BlocklyField() {
    }

    public BlocklyField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "BlocklyField [name = " + name + ", value = " + value + "]";
    }
}
