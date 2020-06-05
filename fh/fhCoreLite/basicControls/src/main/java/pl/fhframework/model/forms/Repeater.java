package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.IRowNumberOffsetSupplier;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.designer.JavaPropertyNameDesignerAttributeSupport;
import pl.fhframework.core.forms.IterationContext;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.core.forms.iterators.ISingleIteratorComponentFactory;
import pl.fhframework.core.forms.iterators.ISingleIteratorRepeatable;
import pl.fhframework.core.forms.iterators.RepeatableIteratorInfo;
import pl.fhframework.model.dto.ElementChanges;

import java.util.*;
import java.util.stream.Collectors;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Created by krzysztof.kobylarek on 2016-11-09.
 */
@Control(canBeDesigned = true)
@DocumentedComponent(ignoreFields = {"width"}, category = DocumentedComponent.Category.ARRANGEMENT, documentationExample = true,
        value = "Component that allows to arrange data like text, images, links, etc. into rows and columns of cells.",
        icon = "fa fa-sitemap")
public class Repeater extends FormElement implements IEditableGroupingComponent<Component>, IRepeatable, IStateHolder, ISingleIteratorRepeatable<Repeater> {

    protected static final IRowNumberOffsetSupplier NO_OFFSET_ROW_NUMBER = () -> 0;

    @JsonIgnore
    @Getter
    private boolean processComponentChange = true;

    @JsonIgnore
    @Setter
    private ISingleIteratorComponentFactory<Repeater> interatorComponentFactory;

    // ###### constructors ######
    public Repeater(Form formatka) {
        super(formatka);
        interatorComponentFactory = new RepeaterComponentCloner();
    }

    @Override
    public void init() {
        super.init();

        // init original subcomponent
        for (Component component : repeatedComponents) {
            if (!component.isInitDone()) {
                component.init();
                if (component instanceof IGroupingComponent) {
                    ((IGroupingComponent<Component>) component).doActionForEverySubcomponent(c -> {
                        if (!component.isInitDone()) {
                            c.init();
                        }
                    });
                }
            }
        }
    }

    // ###### properties ######
    @JsonIgnore
    @Getter
    @Setter
    private List<Component> repeatedComponents = new LinkedList<>();

    @JsonIgnore
    @Getter
    private List<IterationContext> bindedSubcomponents = new LinkedList<>();

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 20, support = JavaPropertyNameDesignerAttributeSupport.class)
    @DocumentedComponentAttribute(value = "Name of the iterator variable used to refer each data")
    private String iterator;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(allowedTypes = Collection.class, commonUse = true, bindingOnly = true, functionalArea = CONTENT, priority = 15)
    @DocumentedComponentAttribute(boundable = true, value = "Collection of data to be displayed")
    private ModelBinding<?> collection;

    @JsonIgnore
    private ComponentStateSaver componentStateSaver = new ComponentStateSaver();

    @Override
    public List<Component> getSubcomponents() {
        // in design mode, template components are presented instead of copied components for every row
        if (getForm().getViewMode() == Form.ViewMode.NORMAL) {
            return bindedSubcomponents.stream().map(ctx -> ctx.getComponent()).collect(Collectors.toList());
        } else {
            return repeatedComponents;
        }
    }

    @Override
    public List<NonVisualFormElement> getNonVisualSubcomponents() {
        return Collections.emptyList();
    }

    @Override
    public IRepeatableIteratorInfo getIteratorInfo() {
        if (iterator != null) {
            return new RepeatableIteratorInfo(
                    Repeater.this.getIterator(),
                    Repeater.this.getCollection() != null ? Repeater.this.getCollection().getBindingExpression() : null);
        } else {
            return null;
        }
    }

    /*
     * internal info structure todo change to private after tests
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class RepeaterIterationContext {
        protected Integer iterationIndex;
        protected String iterationBinding;
        protected String iterationIterator;
    }

    @JsonIgnore
    protected RepeaterIterationContext iterationContext = null;

    @JsonIgnore
    private Integer modelRefSize = null;

    // ###### public methods ######

    @Override
    public void addSubcomponent(Component formElement) {
        repeatedComponents.add(formElement);
    }

    @Override
    public void removeSubcomponent(Component removedFormElement) {
        repeatedComponents.remove(removedFormElement);
    }


    // ###### private helpers ######

    @Override
    public void processComponents() {
        // in design mode Repeater returns its template components, no collection / coping of components needed
        if (getForm().getViewMode() != Form.ViewMode.NORMAL) {
            return;
        }
        if (collection == null)
            return;

        Collection<?> collectionData = getBoundCollection();

        int oldModelRefSize = modelRefSize != null ? modelRefSize.intValue() : 0;

        modelRefSize = collectionData.size();
        if (oldModelRefSize != modelRefSize) {
            List<IterationContext> newBindedSubcomponents = new LinkedList<>();
            List<?> collectionArray = new ArrayList<>(collectionData);
            for (int index = 0; index < modelRefSize; ++index) {
                // already have theese rows
                if (oldModelRefSize > index) {
                    // copy rows
                    for (IterationContext existingComponent : bindedSubcomponents) { // not using lambda as index is not final...
                        if (existingComponent.getIndex().equals(index)) {
                            newBindedSubcomponents.add(existingComponent);
                            existingComponent.getComponent().getBindingContext().getIteratorContext().put(iterator, collectionArray.get(index));
                            existingComponent.getComponent().getBindingContext().setCachePrefix(this.getBindingContext().getCachePrefix() + "_" + getId() + "_" + index + "_");
                        }
                    }
                } else {
                    // create new rows
                    List<FormElement> newComponents = interatorComponentFactory.createComponentsForIterator(this, NO_OFFSET_ROW_NUMBER, index);
                    for (FormElement newComponent : newComponents) {
                        newBindedSubcomponents.add(new IterationContext(index, newComponent));
                        newComponent.getBindingContext().getIteratorContext().put(iterator, collectionArray.get(index));
                        newComponent.getBindingContext().setCachePrefix(this.getBindingContext().getCachePrefix() + "_" + getId() + "_" + index + "_");
                    }
                }
            }

            bindedSubcomponents.clear();
            bindedSubcomponents.addAll(newBindedSubcomponents);
        }
    }

    @JsonIgnore
    public Collection<?> getBoundCollection() {
        BindingResult collectionBinding = collection.getBindingResult();
        if (collectionBinding.getValue() == null) {
            return Collections.emptyList();
        } else if (!Collection.class.isAssignableFrom(collectionBinding.getValue().getClass())) {
            throw new RuntimeException("Not a Collection: " + collection.getBindingExpression());
        }
        return (Collection<?>) collectionBinding.getValue();
    }

    @Override
    protected void postClone(FormElement originalComponent) {
        if (!(interatorComponentFactory instanceof RepeaterComponentCloner)) {
            throw new RuntimeException("Cannot clone Repeater with compiled cloner: " + interatorComponentFactory.getClass());
        }
    }

    @Override
    public IGroupingComponent getGroupingComponentForNewComponents() {
        return this;
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processComponentChange(this, elementChanges);
        return elementChanges;
    }

    @Override
    public void refreshView(Set<ElementChanges> changeSet) {
        super.refreshView(changeSet);
        doActionForEverySubcomponent((component) -> {
            if (component instanceof GroupingComponent) {
                component.refreshView(changeSet);
            }
        });
    }

    protected static String remSpecial(String binding) {
        return binding.replace("{", "").replace("}", "");
    }

    public void setProcessComponentStateChange(boolean processComponentChange) {
        this.processComponentChange = processComponentChange;
        this.getRepeatedComponents().stream().forEachOrdered((Component c) -> {
            if (c instanceof IStateHolder) {
                ((IStateHolder) c).setProcessComponentStateChange(processComponentChange);
            }
        });

        getSubcomponents().stream().forEachOrdered((Component c) -> {
            if (c instanceof IStateHolder) {
                ((IStateHolder) c).setProcessComponentStateChange(processComponentChange);
            }
        });
    }

    public void processComponentChange(IGroupingComponent groupingComponent, ElementChanges elementChanges) {
        if (processComponentChange) {
            componentStateSaver.processComponentChange(groupingComponent, elementChanges);
        }
    }

    private String getIterationContextInfo() {
        return (iterationContext != null ? " --- [iterationBinding: " + iterationContext.getIterationBinding() + "]" : " [iterationContext=null]")
                + (iterationContext != null ? " --- [iterationIterator: " + iterationContext.getIterationIterator() + "]" : "")
                + (iterationContext != null ? " --- [iterationIndex: " + iterationContext.getIterationIndex() + "]" : "");
    }
}
