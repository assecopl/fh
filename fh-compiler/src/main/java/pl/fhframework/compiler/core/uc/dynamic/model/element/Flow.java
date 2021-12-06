package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 * Holder for use case elements.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class Flow implements Parental, ISnapshotEnabled, Cloneable {

    @XmlElementRef
    private List<UseCaseElement> useCaseElements = new LinkedList<>();

    private Flow(Flow other) {
        if (!CollectionUtils.isEmpty(other.useCaseElements)) {
            other.useCaseElements.forEach(useCaseElement -> this.useCaseElements.add((UseCaseElement) useCaseElement.clone()));
        }
    }

    @Override
    public Object clone() {
        return new Flow(this);
    }

    public void addUseCaseElement(UseCaseElement element) {
        element.setParent(this);
        useCaseElements.add(element);
    }

    @Override
    public boolean removeChild(Child child) {
        if (useCaseElements != null) {
            ((UseCaseElement) child).setParent(null);
            return useCaseElements.remove(child);
        }

        return false;
    }

    @Override
    public void addChild(Child child) {
        addUseCaseElement((UseCaseElement) child);
    }

    @Override
    public String getName() {
        return null;
    }
}
