package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.repository.Function;

import javax.xml.bind.annotation.*;
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
public class Repository implements Parental, ISnapshotEnabled, Cloneable {

    @XmlElements({
            @XmlElement(name = "Function", type = Function.class)
    })
    @XmlElementWrapper(name = "Functions")
    private List<Function> functions = new LinkedList<>();

    private Repository(Repository other) {
        if (!CollectionUtils.isEmpty(other.functions)) {
            other.functions.forEach(function -> this.functions.add((Function) function.clone()));
        }
    }

    @Override
    public Object clone() {
        return new Repository(this);
    }

    public void addFunction(Function function) {
        function.setParent(this);
        functions.add(function);
    }

    @Override
    public boolean removeChild(Child child) {
        if (functions != null) {
            ((Function) child).setParent(null);
            return functions.remove(child);
        }

        return false;
    }

    @Override
    public void addChild(Child child) {
        functions.add((Function) child);
    }

    @Override
    public String getName() {
        return null;
    }
}
