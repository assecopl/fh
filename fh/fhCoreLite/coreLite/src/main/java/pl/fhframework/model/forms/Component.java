package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ReflectionUtils;
import pl.fhframework.aspects.snapshots.model.IUnmanagedUseCaseParameter;
import pl.fhframework.core.designer.IdAttributeDesignerSupport;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.events.DesignViewEvent;
import pl.fhframework.events.IDesignEventSource;
import pl.fhframework.events.IEventSource;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

/**
 * Created by krzysztof.kobylarek on 2016-12-20.
 */
@ModelElement(type = ModelElementType.HIDDEN)
public class Component implements Cloneable, IDesignEventSource, IEventSource, IUnmanagedUseCaseParameter {

    public static final String ACCESSIBILITY = "accessibility";
    private static final String GENERATED_ID_PART = "_gen_";
    private static final String EVENT_ON_DESIGNER_TOOLBOX_DROP = "onDesignerToolboxDrop";
    /**
     * Bootstrap resolution size class
     */
    public static String RESOLUTION_SIZE = "md-";
    private static AtomicInteger counter = new AtomicInteger(0);

    /**
     * Id of the component
     */
    @XMLProperty(editable = false)
    @DesignerXMLProperty(functionalArea = CONTENT, priority = 0, support = IdAttributeDesignerSupport.class)
    @DocumentedComponentAttribute("Component identifier (should be unique within the view)")
    @Getter @Setter
    private String id;

    @JsonIgnore
    @Getter
    @Setter
    private String rawId;

    @Getter
    @Setter
    @JsonProperty("accessibility")
    private AccessibilityEnum availability;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = "availability")
    @DesignerXMLProperty(allowedTypes = AccessibilityEnum.class, functionalArea = CONTENT, priority = 10)
    @DocumentedComponentAttribute(boundable = true, type = AccessibilityEnum.class, value = "Accessibility of an Component")
    private ModelBinding<AccessibilityEnum> availabilityModelBinding;

    @Getter
    @Setter
    @XMLProperty(value = "hiddenElementsTakeUpSpace")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 97) // Priority set to show element just after FormElement.verticalAlign
    @DocumentedComponentAttribute("Parameter for HIDDEN components. Makes hidden elements still take up space in the page.")
    private Boolean invisible;

    @JsonIgnore
    private AccessibilityEnum pastAvailability = null;

    /**
     * If user has permission to run availablility affecting actions e.g. press the button.
     * If not this will affect this component availability.
     */
    private boolean availabilityAffectingActionsPermitted = true;

    @Getter
    @JsonIgnore
    private final Set<AccessibilityRule> accessibilityRules = new LinkedHashSet<>();

    @JsonIgnore
    private Form<?> form;

    @JsonIgnore
    @Getter
    boolean generatedId;

    @Getter @Setter
    @JsonIgnore
    @CompilationTraversable
    private IGroupingComponent<? extends Component> groupingParentComponent;

    @Getter @Setter
    @JsonIgnore
    boolean stopProcessingUpdateView = false;

    @JsonIgnore
    @Getter @Setter
    private ComponentBindingContext bindingContext = new ComponentBindingContext();

    @JsonIgnore
    @Setter(value = AccessLevel.NONE)
    private boolean initDone = false;

    /**
     * Flag indicationg that this component is artificially created (not read from XML)
     */
    @JsonIgnore
    @Setter
    @Getter
    private boolean artificial = false;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(skip = true)
    @DocumentedComponentAttribute(value = "If the component is dropped on form edited in designer.")
    private ActionBinding onDesignerToolboxDrop;

    public Component(Form form) {
        this.form = form;
    }

    public void init() {
        if (initDone) {
            throw new RuntimeException("Init already done!");
        }
        if (id == null) {
            generateId();
        }
        initDone = true;
        availabilityAffectingActionsPermitted = !getAvailablityAffectingActions().stream()
                .filter(Objects::nonNull)
                .anyMatch(action -> getForm().getUnpermittedActions().contains(action.getActionName()));
    }

    public boolean isInitDone() {
        return initDone;
    }

    public void setId(String value) {
        if (value == null) return;
        else this.id = value;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public <T> Form<T> getForm() {
        return (Form<T>) form;
    }

    protected boolean areModelValuesTheSame(Object firstValue, Object secondValue) {
        return areValuesTheSame(firstValue, secondValue);
    }

    protected boolean areValuesTheSame(Object firstValue, Object secondValue) {
        return (firstValue == secondValue) ||
                (firstValue != null && firstValue.equals(secondValue)) ||
                (secondValue != null && secondValue.equals(firstValue))
                ;
    }

    protected void refreshAvailability(ElementChanges elementChanges) {
        calculateAvailability();
        AccessibilityEnum newAvailability = getAvailability();
        if (!areValuesTheSame(pastAvailability, newAvailability)) {
            setAvailability(newAvailability);
            elementChanges.addChange(ACCESSIBILITY, newAvailability);
            this.pastAvailability = newAvailability;
        }
    }

    public void calculateAvailability() {
        // inactive form state -> VIEW
        if (getForm().getState() == FormState.INACTIVE_PENDING || getForm().getState() == FormState.INACTIVE) {
            if (this.availability == null || this.availability == AccessibilityEnum.EDIT) {
                this.availability = AccessibilityEnum.VIEW;
            }
            return;
        }

        // starting point
        AccessibilityEnum calculatedAvailability = AccessibilityEnum.EDIT;

        // any of availablity affecting actions is not permitted for current user - reduce availability
        if (!availabilityAffectingActionsPermitted) {
            calculatedAvailability = AccessibilityEnum.VIEW;
        }

        boolean inherit = true;

        if (this.availabilityModelBinding != null) {
            inherit = false;
            BindingResult availabilityBindingResult = this.availabilityModelBinding.getBindingResult();
            Object availabilityBindingValue = availabilityBindingResult != null ? availabilityBindingResult.getValue() : null;
            if (availabilityBindingValue instanceof String) {
                calculatedAvailability = calculatedAvailability.sumAccessibility(AccessibilityEnum.valueOf((String) availabilityBindingValue));
            } else if (availabilityBindingValue instanceof AccessibilityEnum) {
                calculatedAvailability = calculatedAvailability.sumAccessibility(((AccessibilityEnum) availabilityBindingValue));
            }
        }

        for (AccessibilityRule accessibilityRule : accessibilityRules) {
            AccessibilityEnum availabilityFromRule;
            try {
                availabilityFromRule = accessibilityRule.getAccessibilityFunction().apply(accessibilityRule);
            } catch (Exception e) {
                FhLogger.error("Applying rule failure!", e);
                availabilityFromRule = AccessibilityEnum.DEFECTED;
            }
            if (availabilityFromRule != null) {
                inherit = false;
                calculatedAvailability = calculatedAvailability.sumAccessibility(availabilityFromRule);
                // We should not continue counting (because nothing will change) if we found damage or any element is not visible
                if (calculatedAvailability == AccessibilityEnum.HIDDEN || calculatedAvailability == AccessibilityEnum.DEFECTED) {
                    this.availability = calculatedAvailability;
                    return;
                }
            }
        }

        // get parent's availability - assuming that parent has already calculated availability
        AccessibilityEnum parentAvailability = null;
        AccessibilityEnum variantDefaultAvailability = null;
        if (groupingParentComponent != null) {
            parentAvailability = ((Component) groupingParentComponent).getAvailability();
        }

        // form is the parent and form availability is not changed - use default availability for current variant
        if (groupingParentComponent == getForm()
                && !StringUtils.isNullOrEmpty(getForm().getVariant())
                && parentAvailability == AccessibilityEnum.EDIT) {
            variantDefaultAvailability = getForm().getVariantsDefaultAvailability().get(getForm().getVariant());
        }

        if (parentAvailability == AccessibilityEnum.HIDDEN || inherit) {
            calculatedAvailability = calculatedAvailability.sumAccessibility(parentAvailability);
        }
        if (variantDefaultAvailability != null && inherit) {
            calculatedAvailability = calculatedAvailability.sumAccessibility(variantDefaultAvailability);
        }

        this.availability = calculatedAvailability;
    }


    @Override
    public String toString() {
        return getType() + " " + super.toString();
    }

    protected void postClone(FormElement originalComponent) {
    }

    @Override
    final protected Optional<Component> clone() {
        try {
            FormElement clone = (FormElement) super.clone();

            clone.setBindingContext(getBindingContext().clone());
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                if (Collection.class.isAssignableFrom(field.getType())) {

                    field.setAccessible(true);
                    Collection thisCollection = (Collection) ReflectionUtils.getField(field, Component.this);
                    //Optional<? extends FormElement> clonedCollection = getCollectionCopy(thisCollection);
                    Optional<? extends FormElement> clonedCollection = getInstance(thisCollection.getClass());
                    if (clonedCollection.isPresent()) {
                        if (!Objects.equals("subcomponents", field.getName())) // nie kopiuj referencji do podelemementow
                            ((Collection<?>) clonedCollection.get()).addAll(thisCollection);
                        ReflectionUtils.setField(field, clone, clonedCollection.get());
                    }

                } else if (ModelBinding.class.isAssignableFrom(field.getType())) {

                    field.setAccessible(true);
                    ModelBinding md = ((ModelBinding) ReflectionUtils.getField(field, this));
                    if (md != null) {
                        ReflectionUtils.setField(field, clone, md.clone(clone));
                    }

                } else if (field.isAnnotationPresent(XMLProperty.class)) {

                    field.setAccessible(true);
                    ReflectionUtils.setField(field, clone, ReflectionUtils.getField(field, Component.this));

                } else if (ComponentStateSaver.class.isAssignableFrom(field.getType())) {

                    field.setAccessible(true);
                    ReflectionUtils.setField(field, clone, new ComponentStateSaver());

                } else if (field.isAnnotationPresent(RepeaterTraversable.class)
                        || field.getType().isAnnotationPresent(RepeaterTraversable.class)) {
                    field.setAccessible(true);
                    if (ReflectionUtils.getField(field, this) instanceof FormElement) {
                        ReflectionUtils.setField(field, clone,
                                FormElement.class.cast(ReflectionUtils.getField(field, this))
                                        .clone().orElseThrow(RuntimeException::new));
                    }
                }
            });

            if (this instanceof IGroupingComponent<?>) {
                IGroupingComponent<Component> thisGroupingComponent = IGroupingComponent.class.cast(this);
                IGroupingComponent<Component> cloneGroupingComponent = IGroupingComponent.class.cast(clone);
                for (Component subcomponent : thisGroupingComponent.getSubcomponents()) {
                    Optional<Component> subComponentClone = subcomponent.clone();
                    if (subComponentClone.isPresent()) {
                        cloneGroupingComponent.addSubcomponent(subComponentClone.get());
                        subComponentClone.get().setGroupingParentComponent(cloneGroupingComponent);
                    }
                }
            }
            clone.postClone((FormElement) this);
            return Optional.of(clone);
        } catch (CloneNotSupportedException e) {
            FhLogger.error(e);
        }
        return Optional.empty();
    }

    private static Optional<Collection<? super Component>> getCollectionCopy(Collection<? extends Component> toCloneCollection) {

        Optional<Collection<? super Component>> clonedCollection = getInstance(toCloneCollection.getClass());

        if (clonedCollection.isPresent()) {
            for (Component componentToClone : toCloneCollection) {
                clonedCollection.get().add(componentToClone.clone().get());
            }
        }
        return clonedCollection;
    }


    private static Optional getInstance(Class<?> type) {
        try {
            return Optional.of(type.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            FhLogger.error(e);
        }
        return Optional.empty();
    }

    public void onProcessingStart() {
    }

    public void onProcessingFinish() {
        this.setStopProcessingUpdateView(false);
    }

    public String convertBindingValueToString(BindingResult bindingResult) {
        return getForm().convertBindingValueToString(bindingResult);
    }

    public String convertBindingValueToString(BindingResult bindingResult, Optional<String> converterName) {
        return getForm().convertBindingValueToString(bindingResult, converterName);
    }

    public <T> T convertValue(Object value, Class<T> clazz) {
        return getForm().convertValue(value, clazz);
    }

    public String convertValueToString(Object value) {
        return getForm().convertValueToString(value);
    }

    public String convertValueToString(Object value, String converter) {
        return getForm().convertValueToString(value, converter);
    }


    public void refreshView(Set<ElementChanges> changeSet) {
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (EVENT_ON_DESIGNER_TOOLBOX_DROP.equals(eventData.getEventType())) {
            return Optional.ofNullable(onDesignerToolboxDrop);
        } else {
            return Optional.empty();
        }
    }

    // TODO: Get rid of this method.
    @Deprecated
    public <T> AdHocModelBinding<T> createAdHocModelBinding(String binding) {
        return new AdHocModelBinding<T>(getForm(), this, binding);
    }

    public void generateId() {
        generatedId = true;
        id = getGeneratedIdPrefix() + counter.getAndIncrement();
    }

    @JsonIgnore
    public String getGeneratedIdPrefix() {
        return getType() + GENERATED_ID_PART;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;
        if (id != null ? !id.equals(component.id) : this != o) return false; // this != o  -> if id is null compare if it is the same object

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void finalizeReading(String text) {
    }

    @Override
    public ViewEvent<?> prepareEventDataArgument(InMessageEventData eventData) {
        if (getForm().getViewMode() == Form.ViewMode.DESIGN
                || Component.EVENT_ON_DESIGNER_TOOLBOX_DROP.equals(eventData.getEventType())) {
            return new DesignViewEvent(this, getForm(), extractSourceComponentAttrs(eventData));
        } else {
            return new ViewEvent(this, getForm(), eventData.getOptionalValue());
        }
    }

    protected List<ActionBinding> getAvailablityAffectingActions() {
        return Collections.emptyList();
    }

    public void setOnDesignerToolboxDrop(ActionBinding onDesignerToolboxDrop) {
        this.onDesignerToolboxDrop = onDesignerToolboxDrop;
    }

    public IActionCallbackContext setOnDesignerToolboxDrop(IActionCallback onDesignerToolboxDrop) {
        return CallbackActionBinding.createAndSet(onDesignerToolboxDrop, this::setOnDesignerToolboxDrop);
    }

    private void delegateRunAction(String actionName, Object... arguments) {
        Form form = getForm();
        if (form instanceof CompositeForm) {
            CompositeForm compositeForm = (CompositeForm) form;
            compositeForm.runAction(actionName, arguments);
        } else {
            form.getAbstractUseCase().runAction(actionName, pl.fhframework.ReflectionUtils.getClassName(form.getClass()), arguments);
        }
    }

    private Map<String, Object> extractSourceComponentAttrs(InMessageEventData eventData) {
        return eventData.getChangedFields().stream()
                .filter(change -> change.getFieldId().equals(eventData.getEventSourceId()))
                .map(ValueChange::getChangedAttributes)
                .findFirst()
                .orElse(new HashMap<>());
    }

    public void preConfigureClear() {
        getAccessibilityRules().clear();
    }

    protected void resetInitDone() {
        initDone = false;
    }

}
