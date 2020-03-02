package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.annotations.CompilationTraversable;
import pl.fhframework.annotations.XMLMetadataSubelements;
import pl.fhframework.model.dto.ElementChanges;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class expected to be used for grouping components like TabContainer consist of Tabs. Gropuping
 * component can containst only object which extends <code>FormElement</code> used by Generics.
 * In this class can be defined some common actions on every component inside created container.
 * This class has no representation in xml or javascript.
 */
@ModelElement(type = ModelElementType.HIDDEN)
public abstract class GroupingComponent<T extends Component> extends FormElement implements IGroupingComponent<T>, IEditableGroupingComponent<T>, IStateHolder {

    // todo: remove this annotation and repair code on js side.
    @JsonIgnore
    private List<T> subcomponents = new LinkedList<>();

    @JsonIgnore
    private ComponentStateSaver componentStateSaver = new ComponentStateSaver();

    @JsonIgnore
    @Getter
    private boolean processComponentChange = true;

    @JsonIgnore
    @Getter
    @Setter
    @CompilationTraversable
    private List<NonVisualFormElement> nonVisualSubcomponents = new ArrayList<>();

    public GroupingComponent(Form form) {
        super(form);
    }

    /**
     * Do given action for every subcomponent in this conteinter.
     *
     * @param action - required consumer
     * @throws NullPointerException - when consumer is not provided
     */
//    @Override
//    public void doActionForEverySubcomponent(Consumer<T> action) {
//        //TODO: Here we should visit all rows and theirs columns
//        //TODO: After that we would be able to simplify logic 'Formatka.updateFormComponents()' to avoid operations on tables
//        Objects.requireNonNull(action);
//        for (T subComponent : subcomponents) {
//            action.accept(subComponent);
//            if (subComponent instanceof IGroupingComponent) {
//                ((IGroupingComponent) subComponent).doActionForEverySubcomponent(action);
//            }
//        }
//    }

    /**
     * Adds component to the container.
     */
    @Override
    public void addSubcomponent(T component) {
        if (component instanceof NonVisualFormElement) {
            nonVisualSubcomponents.add((NonVisualFormElement) component);
        } else {
            subcomponents.add(component);
            component.setGroupingParentComponent(this);
        }
    }

    @Override
    public void removeSubcomponent(T removedFormElement){
        if (removedFormElement instanceof NonVisualFormElement) {
            nonVisualSubcomponents.remove(removedFormElement);
        } else {
            subcomponents.remove(removedFormElement);
            removedFormElement.setGroupingParentComponent(null);
        }
    }

    @Override
    public List<T> getSubcomponents() {
        return subcomponents;
    }

    public void setProcessComponentStateChange(boolean processComponentChange) {
        this.processComponentChange = processComponentChange;
        getSubcomponents().forEach((T t) -> {
            if (t instanceof IStateHolder) {
                ((IStateHolder) t).setProcessComponentStateChange(processComponentChange);
            }
        });
    }

    public void processComponentChange(IGroupingComponent groupingComponent, ElementChanges elementChanges){
        if (processComponentChange) {
            componentStateSaver.processComponentChange(groupingComponent, elementChanges);
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processComponentChange(this, elementChanges);
        return elementChanges;
    }

    protected ComponentStateSaver getComponentStateSaver() {
        return componentStateSaver;
    }

    protected void setComponentStateSaver(ComponentStateSaver componentStateSaver) {
        this.componentStateSaver = componentStateSaver;
    }

}
