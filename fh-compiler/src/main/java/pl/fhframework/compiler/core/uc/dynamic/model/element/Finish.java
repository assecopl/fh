package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Finish")
@XmlAccessorType(XmlAccessType.FIELD)
public class Finish extends Action {
    @XmlAttribute
    Boolean discardChanges;

    public boolean isSave() {
        return Boolean.FALSE.equals(discardChanges);
    }

    public void setSave(boolean save) {
        if (save) {
            discardChanges = Boolean.FALSE;
        }
        else {
            discardChanges = null;
        }
    }

    public boolean isDiscard() {
        return Boolean.TRUE.equals(discardChanges);
    }

    public void setDiscard(boolean discard) {
        if (discard) {
            discardChanges = Boolean.TRUE;
        }
        else {
            discardChanges = null;
        }
    }

    @Override
    public UseCaseElement copy() {
        Finish finish = (Finish) super.copy();
        finish.discardChanges = discardChanges;

        return finish;
    }
}