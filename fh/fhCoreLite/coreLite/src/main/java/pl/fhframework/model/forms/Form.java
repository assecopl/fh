package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.Binding;
import pl.fhframework.BindingResult;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.annotations.*;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.uc.IFormUseCaseContext;
import pl.fhframework.core.util.SpelUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.IEventSource;
import pl.fhframework.events.IEventSourceContainer;
import pl.fhframework.forms.FiledsHighlightingList;
import pl.fhframework.forms.IFieldsHighlightingList;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.Layout;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Form is base component that groups together all components defined in usecase, adds common logic
 * and wires data model. Usecase components inherits from the Form: layout, position and display
 * mode. Created by Gabriel on 2015-11-20.
 */
@TemplateControl(tagName = "fh-form")
@Control(parents = {})
@ModelElement(type = ModelElementType.HIDDEN)
public abstract class Form<T> extends GroupingComponentWithHeadingHierarchy<Component> implements Boundable, IHasBoundableLabel {

    public static final String MODAL_VIRTUAL_CONTAINER = "MODAL_VIRTUAL_CONTAINER";
    public static final String ON_MANUAL_MODAL_CLOSE = "onManualModalClose";
    private static final String DEFAULT_LAYOUT = "vertical";
    private static final String STATE_ATTRIBUTE = "state";

    public enum ViewMode {
        NORMAL,
        DESIGN,
        PREVIEW
    }

    public static final String FORM_EXTENSION = ".frm";
    private final static Map<Class<? extends Form>, Map<String, Method>> formClassToVariantToElementIdToMethod = new HashMap<>();

    @Autowired
    private AuthorizationManager authorizationManager;

    /**
     * Binding rules and logic created for this form are first parsed and cached for faster
     * retrieval. Parsing is done using reflection and cache is cleared after form's bindings are
     * refreshed.
     */
    @JsonIgnore
    @Getter
    @Setter
    private Binding bindingMethods;

    @JsonIgnore
    @Getter
    @Setter
    private List<Includeable> included = new LinkedList<>();

    private final HashSet<FormElement> elementsToBeRefreshed = new HashSet<>(20);
    @JsonIgnore
    private final Map<String, FormElement> elementIdToFormElement = new LinkedHashMap<>();

    @JsonIgnore
    @Getter
    @Setter
    @XMLMetadataSubelement
    private AvailabilityConfiguration availabilityConfiguration;

    @Getter
    @JsonIgnore
    private Map<String, AccessibilityEnum> variantsDefaultAvailability = new HashMap<>();

    @Getter
    @JsonIgnore
    private final Set<AccessibilityRule> allAccessibilityRules = new LinkedHashSet<>();

    @Getter
    @Setter
    @XMLMetadataSubelements
    private List<LocaleBundle> localeBundle = new ArrayList<>();

    /**
     * Form label Displayed on top of a window's Form
     */
    @Getter
    private String label;

    @Getter
    @Setter
    @XMLProperty(value = "label")
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @JsonIgnore
    private ModelBinding labelModelBinding;

    /**
     * Container - an id of generic HTML element (eg. DIV) a form component is contained in (or
     * belongs to)
     */
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = "container")
    private String declaredContainer;

    @JsonIgnore
    @Setter
    @Getter
    private String overriddenContainer;

    /**
     * Is form blocked against any user's actions Use to display/create forms in higher order
     * UseCases and not triggered by client side.
     */
    @Getter
    @Setter
    private boolean blocked;


    /**
     * Should form header be visiable.
     */
    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    private boolean hideHeader;

    /**
     * Form's content layout. Currently mapped to HTML display: block, inline or absolute
     */
    @Getter
    @Setter
    @XMLProperty(defaultValue = DEFAULT_LAYOUT)
    @Deprecated
    private Layout layout;
    /**
     * Is created form modal true - modal, false otherwise
     */
    @Getter
    @Setter
    @XMLProperty
    @Deprecated
    private boolean modal;

    /**
     * Form type standard, modal, floating, header
     */
    @Getter
    @Setter
    @XMLProperty(defaultValue = "STANDARD")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 103)
    private FormType formType = FormType.STANDARD;

    /**
     * Form modal size, regular, small, large, xlarge, xxlarge, full
     */
    @Getter
    @Setter
    @XMLProperty(defaultValue = "REGULAR")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 102)
    private FormModalSize modalSize = FormModalSize.REGULAR;

    /**
     * Maximum height of modal
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 101)
    @DocumentedComponentAttribute("Form's max height in \"px\".")
    private Integer modalMaxHeight;

    /**
     * XSD reference
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(readOnlyInDesigner = true)
    @JsonIgnore
    private String xmlns;

    /**
     * Model with data read and updated by the form Other components using model through bindings
     */
    @JsonIgnore
    private T model;

    @Setter
    @JsonIgnore
    private Supplier<T> modelProvider;

    @JsonIgnore
    @Getter
    @Setter
    @XMLMetadataSubelement
    private Comment comment;

    /**
     * Usecase which displayed this form
     */
    @JsonIgnore
    private IFormUseCaseContext abstractUseCase;

    /**
     * Usecase which displayed this form
     */
    @JsonIgnore
    @Getter
    private IFieldsHighlightingList fieldsHighlightingList = new FiledsHighlightingList();

    @Getter
    @Setter
    private ViewMode viewMode = ViewMode.NORMAL;

    @JsonIgnore
    @Getter
    @Setter
    @XMLMetadataSubelement
    private Model modelDefinition = new Model(this);

    @Getter
    @Setter
    private boolean fromCloud = false;

    @Getter
    @Setter
    private String cloudServerName;

    /**
     * Prefix of any URL-base resources within this form. This may be non-null for cloud propagated forms.
     */
    @Getter
    @Setter
    private String resourcesUrlPrefix = null;

    @Setter
    private String variant;

    @JsonIgnore
    @Getter
    @Setter
    private FormState state;

    @JsonProperty(STATE_ATTRIBUTE)
    private FormState clientKnownState;

    @JsonIgnore
    @Getter
    @Setter
    private Instant showingTimestamp;

    @Getter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Action in use case called when this modal form is manually closed " +
            "using X icon in the corner of form. Is not called if form closing is caused by something else. " +
            "To simply close form with this action use - or +. To do something more or else use action on use case. " +
            "If left empty the X icon will not be present.")
    @DesignerXMLProperty(priority = 100, functionalArea = BEHAVIOR)
    private ActionBinding onManualModalClose;

    @Getter
    @Setter
    @JsonIgnore
    private Set<String> unpermittedActions = new HashSet<>();

    /**
     * If this form was at least once refreshed.
     */
    @Getter
    @JsonIgnore
    private boolean alreadyRefreshed = false;

    /**
     * Unassigned accessibility rules at form init phase. May be used by compiled factories of repeated components, e.g. in Columns or Repeaters.
     */
    @Getter
    @JsonIgnore
    private final MultiValueMap<String, AccessibilityRule> unassignedAccessibilityRules = new LinkedMultiValueMap<>();

    @Getter
    @JsonIgnore
    private final List<Throwable> processComponentsExceptions = new ArrayList<>();

    @JsonIgnore
    @Getter
    @Setter
    private IComponentBindingCreator componentBindingCreator = this::createModelBindingForComponent;

    @JsonIgnore
    @Getter
    @Setter
    private Supplier<Binding> bindingMethodsCreator = Binding::new;

    @JsonIgnore
    @Getter
    @Setter
    @SkipSnapshot
    private IFormGenerationUtils generationUtils;

    public Form() {
        super(null);
        bindingMethods = bindingMethodsCreator.get();
        if (!formClassToVariantToElementIdToMethod.containsKey(this.getClass())) {
            buildProgrammingAvailabilityMethodMap();
        }
    }

    public IFormUseCaseContext getAbstractUseCase() {
        return abstractUseCase;
    }

    protected void fillCompleteElementIdToFormElementMap(Map<String, Component> componentMap, Map<String, List<Component>> rawComponentMap) {
        // form components
        doActionForEverySubcomponentInlcudingRepeated((component) -> {
            componentMap.put(component.getId(), component);
            if (component.getRawId() != null) {
                rawComponentMap.computeIfAbsent(component.getRawId(), key -> new ArrayList<>());
                rawComponentMap.get(component.getRawId()).add(component);
            }
        });
        // this form
        componentMap.put(this.getId(), this);
    }

    public void refreshElementIdToFormElement() {
        elementIdToFormElement.clear();
        doActionForEverySubcomponent((formElement) -> {
            if (formElement instanceof FormElement) {
                elementIdToFormElement.put(formElement.getId(), (FormElement) formElement);
            }
        });
    }

    public void close() {
        throw new FhFormException("Not ready");
    }

    @Override
    public Form<?> getForm() {
        return this;
    }

    @JsonIgnore
    public Form<?> getEventProcessingForm() {
        if (getGroupingParentComponent() instanceof Generable && getGroupingParentComponent() instanceof Component) {
            return ((Component) getGroupingParentComponent()).getForm();
        }
        return this;
    }

    protected void addAdHocField() {
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = new ElementChanges();
        elementChanges.setFormId(getForm().getId());
        elementChanges.setFormElementId(this.getId());

        // calculate form's own availability
        calculateAvailability();

        if (labelModelBinding != null) {
            BindingResult bindingResult = labelModelBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabel = this.convertBindingValueToString(bindingResult);
                if (!areValuesTheSame(newLabel, this.label)) {
                    // refreshView();
                    this.label = newLabel;
                    elementChanges.addChange("label", this.label);
                }
            }
        }
        return elementChanges;
    }

    @Deprecated
    public void setLabelModelBindingAdHoc(String labelBinding) {
        setLabelModelBinding(createAdHocModelBinding(labelBinding));
    }

    /**
     * Returns data stored in model for passed binding
     *
     * @param binding Binding without chars: '{', '}'. Sections of the binding are separated with
     *                dot [.]
     * @return model data for binding
     */
    @Deprecated
    public T getModelValue(String binding, Component bindingOwner) {
        try {
            if (!bindingMethods.isActive()) {
                activateBindings();
                FhLogger.warn("Ad hoc access to model '{}'!", binding);
                T result = bindingMethods.getModeValue(binding, bindingOwner != null ? bindingOwner.getBindingContext() : null);
                deactivateBindings();
                return result;
            } else {
                return bindingMethods.getModeValue(binding, bindingOwner != null ? bindingOwner.getBindingContext() : null);
            }
        } catch (IndexOutOfBoundsException exc) {
            FhLogger.error("Error with binding: '{}' of {} in form {} - collection size exceeded!!!", binding,
                    bindingOwner != null ? bindingOwner.getId() : null,
                    bindingOwner != null ? (bindingOwner.getForm() != null ? bindingOwner.getForm().getId() : null) : null);
            throw exc;
        } catch (Exception exc) {
            FhLogger.error("Error with binding: '{}' of {} in form {}", binding,
                    bindingOwner != null ? bindingOwner.getId() : null,
                    bindingOwner != null ? (bindingOwner.getForm() != null ? bindingOwner.getForm().getId() : null) : null);
            throw exc;
        }
    }

    /**
     * Returns data stored in model for passed binding
     *
     * @param binding      Binding without chars: '{', '}'. Sections of the binding are separated
     *                     with dot [.]
     * @param bindingOwner binding owner with ComponentBindingContext
     * @return model data for binding
     */
    public BindingResult getBindingResult(String binding, Component bindingOwner) {
        try {
            if (!bindingMethods.isActive()) {
                activateBindings();
                FhLogger.warn("Ad hoc access to model '{}'!", binding);
                BindingResult result = bindingMethods.getBindingResult(binding, bindingOwner != null ? bindingOwner.getBindingContext() : null);
                deactivateBindings();
                return result;
            } else {
                return bindingMethods.getBindingResult(binding, bindingOwner != null ? bindingOwner.getBindingContext() : null);
            }
        } catch (IndexOutOfBoundsException exc) {
            FhLogger.error("Error with binding: '{}' of {} in form {} - collection size exceeded!!!", binding,
                    bindingOwner != null ? bindingOwner.getId() : null,
                    bindingOwner != null ? (bindingOwner.getForm() != null ? bindingOwner.getForm().getId() : null) : null);
            throw exc;
        } catch (Exception exc) {
            FhLogger.error("Error with binding: '{}' of {} in form {}", binding,
                    bindingOwner != null ? bindingOwner.getId() : null,
                    bindingOwner != null ? (bindingOwner.getForm() != null ? bindingOwner.getForm().getId() : null) : null);
            throw exc;
        }
    }

    /**
     * Method modifies model data. Data to be replaced is indicated by passed binding.
     *
     * @param binding      Binding without chars: '{', '}'. Sections of the binding are separated
     *                     with dot [.]
     * @param bindingOwner binding owner. Can be null.
     * @param wartosc      Replacement value. Can be null.
     */
    public void setModelValue(String binding, Object wartosc, Component bindingOwner) {
        setModelValue(binding, wartosc, Optional.empty(), bindingOwner);
    }

    /**
     * Method modifies model data. Data to be replaced is indicated by passed binding.
     *
     * @param binding      Binding without chars: '{', '}'. Sections of the binding are separated
     *                     with dot [.]
     * @param formatter    Optional value formatter.
     * @param bindingOwner binding owner. Can be null.
     * @param wartosc      Replacement value. Can be null.
     */
    public void setModelValue(String binding, Object wartosc, Optional<String> formatter, Component bindingOwner) {
        bindingMethods.setModelValue(binding, wartosc, formatter, bindingOwner != null ? bindingOwner.getBindingContext() : null);
    }

    /**
     * Returns Event Source object indicated by passed id
     *
     * @param eventSourceId event source id, in case of form component it is an id of form's element
     * @return EventSource instance
     */
    public IEventSource getEventSource(String eventSourceId) {
        if (Objects.equals(this.getId(), eventSourceId)) {
            return this;
        }
        Component formElement = this.elementIdToFormElement.get(eventSourceId);
        if (formElement == null && !eventSourceId.contains("[")) {
            refreshElementIdToFormElement();
            formElement = this.elementIdToFormElement.get(eventSourceId);
        }
        if (formElement == null && eventSourceId.contains("[")) {
            int startPositionElementNumber = eventSourceId.indexOf("[");
            String parentId = eventSourceId.substring(0, startPositionElementNumber);
            IEventSource parent = getEventSource(parentId);
            if (parent instanceof IEventSourceContainer) {
                return ((IEventSourceContainer) parent).getEventSource(eventSourceId);
            } else {
                if (parent != null) {
                    throw new FhFormException("Cannot handle taular type '" + parent.getClass().getName() + "' - no implementation of interface 'IEventSourceContainer'!");
                } else {
                    throw new FhFormException("Cannot find parent with id '" + parentId + "'!");
                }
            }
        } else if (formElement != null && formElement instanceof IEventSource) {
            return (IEventSource) formElement;
        } else {
            return null;
        }
    }

    /**
     * Returns Event Source object indicated by passed id
     *
     * @param idOfFormElement event source id, in case of form component it is an id of form's element
     * @return EventSource instance
     */
    public FormElement getFormElement(String idOfFormElement) {
        FormElement element = this.elementIdToFormElement.get(idOfFormElement);
        if (element == null) {
            refreshElementIdToFormElement();
            element = this.elementIdToFormElement.get(idOfFormElement);
        }
        return element;
    }

    @Override
    public String toString() {
        return label != null ? label : "<no label>";
    }

    public Set<ElementChanges> updateFormComponents() {
        activateBindings();

        doActionForEverySubcomponent(formElement -> {
            if(formElement instanceof Includeable) {
                ((Includeable) formElement).activateBindings();
            }
        });

        doActionForEverySubcomponent((formElement) -> {
            if (formElement instanceof IGroupingComponent) {
                ((IGroupingComponent) formElement).processComponents();
            }
        });

        doActionForEverySubcomponent(formElement -> {
            if(formElement instanceof Includeable) {
                ((Includeable) formElement).afterNestedComponentsProcess();
            }
        });

        Set<ElementChanges> changesInFormComponents = new LinkedHashSet<>();

        ElementChanges elementChanges = this.updateView();
        if (elementChanges.containsAnyChanges()) {
            changesInFormComponents.add(elementChanges);
        }

        resolveBindingAndUpdateView(changesInFormComponents);

        doActionForEverySubcomponent(formElement -> {
            if(formElement instanceof Includeable) {
                ((Includeable) formElement).deactivateBindings();
            }
        });
        deactivateBindings();
        alreadyRefreshed = true;

        return changesInFormComponents;
    }

    public Set<ElementChanges> updateFormComponentsAvailabilityOnly() {
        activateBindings();

        Set<ElementChanges> changesInFormComponents = new LinkedHashSet<>();

        // calculate form's own availability
        calculateAvailability();

        doActionForEveryActiveSubcomponent(component -> {
            try {
                ElementChanges changedElement = new ElementChanges();
                component.refreshAvailability(changedElement);

                if (changedElement.containsAnyChanges()) {
                    changedElement.setFormId(getForm().getId());
                    changedElement.setFormElementId(component.getId());
                    changesInFormComponents.add(changedElement);
                }
            } catch (FhAuthorizationException exc) {
                processComponentsExceptions.add(exc);
                FhLogger.error("{} in form component '{}'", exc.getMessage(), component.getId());
            } catch (Exception exc) {
                processComponentsExceptions.add(exc);
                FhLogger.error("Error during update of form components '{}'!", component.getId(), exc);
            }
        });

        deactivateBindings();

        return changesInFormComponents;
    }

    /**
     * Updates form state attribute to client
     * @param changes all changes
     * @param addToChanges if true, attribute change may be added to passed changes collection, otherwise only form attribute is updated
     */
    public void updateClientKnownFormState(Set<ElementChanges> changes, boolean addToChanges) {
        if (state != clientKnownState) {
            if (addToChanges) {
                ElementChanges changedElement = new ElementChanges();
                changedElement.setFormId(this.getId());
                changedElement.setFormElementId(this.getId());
                changedElement.getChangedAttributes().put(STATE_ATTRIBUTE, state);
                changes.add(changedElement);
            }
            clientKnownState = state;
        }
    }

    private void resolveBindingAndUpdateView(Set<ElementChanges> changesInFormComponents) {
        doActionForEveryActiveSubcomponent(component -> {
            try {
                if (component instanceof FormElement) {
                    component.refreshView(changesInFormComponents);
                }
            } catch (FhAuthorizationException exc) {
                processComponentsExceptions.add(exc);
                FhLogger.error("{} in form component '{}'", exc.getMessage(), component.getId());
            } catch (Exception exc) {
                processComponentsExceptions.add(exc);
                FhLogger.error("Error during update of form components '{}'!", component.getId(), exc);
            }
        });
    }

    public void updateModel(List<ValueChange> changedFields) {
        activateBindings();
        changedFields.forEach(changedValue -> {
            if (getId().equals(changedValue.getFormId())) {
                Component changedElement = elementIdToFormElement.get(changedValue.getFieldId());
                if (changedElement == null) {
                    refreshElementIdToFormElement();
                    changedElement = elementIdToFormElement.get(changedValue.getFieldId());
                }
                if (changedElement instanceof IChangeableByClient) {
                    ((IChangeableByClient) changedElement).updateModel(changedValue);
                }
            }
        });
        deactivateBindings();
    }

    public void activateBindings() {
        bindingMethods.activate(getModel(), this);
        included.forEach(Includeable::activateBindings);
    }

    public void deactivateBindings() {
        included.forEach(Includeable::deactivateBindings);
        bindingMethods.deactivate();
    }

    public void preConfigureClear() {
        doActionForEverySubcomponent(Component::preConfigureClear);
    }

    public void configure(IFormUseCaseContext abstractUseCase, T model) {
        configure(abstractUseCase, model, "");
    }

    /**
     * Checks if form is in design mode. Also sent in JSON.
     * @return true if in design mode
     */
    public boolean isDesignMode() {
        return viewMode == ViewMode.DESIGN;
    }

    public void configure(IFormUseCaseContext abstractUseCase, T model, String variantId) {
        this.model = model;
        this.abstractUseCase = abstractUseCase;
        this.variant = variantId;

        // call init on every subcomponent
        initWithSubcomponents(this);

        if (viewMode == ViewMode.NORMAL) {
            addAdHocField();
            refreshElementIdToFormElement();
        } else {
            refreshElementIdToFormElement();
        }
        if (viewMode != ViewMode.DESIGN) {
            setAvailabilityRules(variantId);
        }
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_MANUAL_MODAL_CLOSE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onManualModalClose);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public static void initWithSubcomponents(Component component) {
        if (!component.isInitDone()) {
            component.init();
        }
        if (component instanceof IGroupingComponent) {
            ((IGroupingComponent<?>) component).doActionForEverySubcomponent(child -> {
                if (!child.isInitDone()) {
                    child.init();
                }
            });
        }
    }

    void setAvailabilityRules(String variantId) {
        Set<AccessibilityRule> accessibilityRules = new LinkedHashSet<>(this.getAllAccessibilityRules());
        // keeps all general rules (no variant) and rules for selected variant
        accessibilityRules.removeIf(rule -> !StringUtils.isNullOrEmpty(rule.getFormVariant()) && !Objects.equals(variantId, rule.getFormVariant()));
        if (accessibilityRules != null && !accessibilityRules.isEmpty()) {
            Map<String, Component> componentMap = new HashMap<>();
            Map<String, List<Component>> rawComponentMap = new HashMap<>();

            fillCompleteElementIdToFormElementMap(componentMap, rawComponentMap);
            for (AccessibilityRule accessibilityRule : accessibilityRules) {
                Component formElement = componentMap.get(accessibilityRule.getId());
                if (formElement == null) {
                    unassignedAccessibilityRules.add(accessibilityRule.getId(), accessibilityRule);
                    rawComponentMap.getOrDefault(accessibilityRule.getId(), Collections.emptyList()).
                            forEach(component -> component.getAccessibilityRules().add(accessibilityRule));
                } else {
                    formElement.getAccessibilityRules().add(accessibilityRule);
                }
            }
        }
    }

    public void refreshElementView(FormElement elementToBeRefreshed) {
        elementsToBeRefreshed.add(elementToBeRefreshed);
    }

    private void buildProgrammingAvailabilityMethodMap() {
        Map<String, Method> variantToElementIdToMethod = new HashMap<>();
        formClassToVariantToElementIdToMethod.put(this.getClass(), variantToElementIdToMethod);
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AvailabilityRuleMethod.class)) {
                AvailabilityRuleMethod annotation = method.getDeclaredAnnotation(AvailabilityRuleMethod.class);
                buildProgrammingAvaiabilityMethodMapForVariant(method, variantToElementIdToMethod, annotation);
            } else if (method.isAnnotationPresent(MultipleAvailabilityRuleMethods.class)) {
                MultipleAvailabilityRuleMethods annotations = method.getDeclaredAnnotation(MultipleAvailabilityRuleMethods.class);
                for (AvailabilityRuleMethod annotation : annotations.value()) {
                    buildProgrammingAvaiabilityMethodMapForVariant(method, variantToElementIdToMethod, annotation);
                }
            }
        }
    }

    private void buildProgrammingAvaiabilityMethodMapForVariant(Method method, Map<String, Method> variantToElementIdToMethod, AvailabilityRuleMethod annotation) {
        method.setAccessible(true);
        String formElementId = annotation.value();
        variantToElementIdToMethod.put(formElementId, method);
    }

    public Method getAvailabilityRuleMethod(String elementId) {
        Map<String, Method> elementIdToMethod = formClassToVariantToElementIdToMethod.get(this.getClass());
        if (elementIdToMethod == null) {
            return null;
        }
        return elementIdToMethod.get(elementId);
    }

    public boolean noValue(Object object) {
        return object == null;
    }

    public boolean setValue(Object object) {
        return object != null;
    }

    public boolean expressionResult(String expression) {
        Object result = SpelUtils.evaluateExpression(expression, getModel());
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            return result != null;
        }
    }

    @Override
    public void init() {
        super.init();
        if (this.getId() == null) {
            this.setId(this.getClass().getSimpleName());
        }
    }

    //TODO:Mockupfunctionality for functionalities managment
    public boolean disabledFunctionality(String functionalityName) {

        return false;
    }

    public T getModel() {
        if (modelProvider != null) {
            return modelProvider.get();
        }
        return model;
    }

    protected void setModel(T model) {
        this.model = model;
    }

    public void setUseCase(IFormUseCaseContext useCase) {
        this.abstractUseCase = useCase;
    }

    protected void removeElement(FormElement formElement) {
        IGroupingComponent groupingComponent = formElement.getGroupingParentComponent();

        if (groupingComponent == null) {
            groupingComponent = getGroupingComponent(formElement);
        }

        if (groupingComponent instanceof IEditableGroupingComponent) {
            groupingComponent.removeSubcomponent(formElement);
        } else {
            throw new FhFormException("Can't remove element from non editable grouping component");
        }
        refreshContainerView(groupingComponent);
    }

    protected void addElement(IGroupingComponent container, FormElement formElement) {
        IGroupingComponent groupingComponent = getGroupingComponent(formElement);
        if (groupingComponent instanceof IEditableGroupingComponent) {
            groupingComponent.addSubcomponent(formElement);
        } else {
            throw new FhFormException("Can't add element to non editable grouping component");
        }
        refreshContainerView(container);
    }

    /**
     * Refresh entire form
     */
    public void refreshView() {
        refreshContainerView(null);
    }

    protected void refreshContainerView(IGroupingComponent container) {
        if (container != null) {
            //TODO: We should refresh only given container but now we refresh whole form
            getAbstractUseCase().getUserSession().getUseCaseContainer().getFormsContainer().closeForm(this);
            getAbstractUseCase().getUserSession().getUseCaseContainer().getFormsContainer().showForm(this);
        } else {
            //If there is no container we refresh whole form
            getAbstractUseCase().getUserSession().getUseCaseContainer().getFormsContainer().closeForm(this);
            getAbstractUseCase().getUserSession().getUseCaseContainer().getFormsContainer().showForm(this);
        }
    }

    public String convertBindingValueToString(BindingResult bindingResult) {
        return bindingMethods.convertBindingValueToString(bindingResult);
    }

    public String convertBindingValueToString(BindingResult bindingResult, Optional<String> converterName) {
        return bindingMethods.convertBindingValueToString(bindingResult, converterName);
    }

    public void addToElementIdToFormElement(FormElement component) {
        this.elementIdToFormElement.put(component.getId(), component);
    }

    public <T> T convertValue(Object value, Class<T> clazz) {
        return bindingMethods.convertValue(value, clazz);
    }

    public String convertValueToString(Object value) {
        return bindingMethods.convertValueToString(value);
    }

    public String convertValueToString(Object value, String converter) {
        return bindingMethods.convertValueToString(value, converter);
    }

    @Override
    public <T> AdHocModelBinding<T> createAdHocModelBinding(String binding) {
        return new AdHocModelBinding<T>(this, this, binding);
    }

    public ActionBinding createActionBindingForComponent(Component component, String attrValue, Field field) {
        return new AdHocActionBinding(attrValue, this, component);
    }

    public ModelBinding createModelBindingForComponent(Component component, String attrValue, Field field) {
        return new AdHocModelBinding<>(this, component, attrValue);
    }

    public IndexedModelBinding createIndexedModelBindingForComponent(IIndexedBindingOwner indexedBindingOwner, String attrValue, Field field) {
        return new AdHocIndexedModelBinding<>(attrValue, indexedBindingOwner, this);
    }

    public String getContainer() {
        if (overriddenContainer != null) {
            return overriddenContainer;
        } else if (getEffectiveFormType().isModal()) {
            return MODAL_VIRTUAL_CONTAINER;
        } else {
            return declaredContainer;
        }
    }

    public void setCommentText(String text) {
        if (text != null && comment == null) {
            comment = new Comment(this, text);
        } else if (text == null) {
            comment = null;
        } else {
            comment.setText(text);
        }
    }

    public String getCommentText() {
        return comment != null ? comment.getText() : null;
    }

    /**
     * Gets effective form type. In normal view mode it returns form type. In other view modes it always returns standard form type.
     * @return effective form type
     */
    public FormType getEffectiveFormType() {
        if (viewMode == ViewMode.NORMAL) {
            return formType;
        } else {
            return FormType.STANDARD;
        }
    }

    @ModelElement(type = ModelElementType.OTHER_PROPERTY)
    public String getVariant() {
        return variant;
    }

    @ModelElement(type = ModelElementType.OTHER)
    public AccessibilityEnum availabilityHideIf(boolean condition) {
        return condition ? AccessibilityEnum.HIDDEN : AccessibilityEnum.EDIT;
    }

    @ModelElement(type = ModelElementType.OTHER)
    public AccessibilityEnum availabilityHideIfElseView(boolean condition) {
        return condition ? AccessibilityEnum.HIDDEN : AccessibilityEnum.EDIT;
    }

    @ModelElement(type = ModelElementType.OTHER)
    public AccessibilityEnum availabilityViewIf(boolean condition) {
        return condition ? AccessibilityEnum.VIEW : AccessibilityEnum.EDIT;
    }

    @ModelElement(type = ModelElementType.OTHER)
    public AccessibilityEnum availabilitySum(AccessibilityEnum one, AccessibilityEnum two) {
        return one.sumAccessibility(two);
    }

    /**
     * Checks if current user has function
     * @param functionName function name
     * @return true, if user has given function
     */
    @ModelElement(type = ModelElementType.OTHER)
    public boolean userHasFunction(String functionName) {
        UserSession userSession = SessionManager.getUserSession();
        return userSession != null && authorizationManager.hasFunction(
                userSession.getSystemUser().getBusinessRoles(),
                functionName,
                abstractUseCase.getUseCase().getSubsystem().getProductUUID());
    }

    /**
     * Checks if current user has any function
     * @param functionsName function names
     * @return true, if user has any of given functions
     */
    @ModelElement(type = ModelElementType.OTHER)
    public boolean userHasAnyFunction(String... functionsName) {
        UserSession userSession = SessionManager.getUserSession();
        return userSession != null && authorizationManager.hasAnyFunction(
                userSession.getSystemUser().getBusinessRoles(),
                Arrays.asList(functionsName),
                abstractUseCase.getUseCase().getSubsystem().getProductUUID());
    }

    /**
     * Checks if current user has role
     * @param roleName role name
     * @return true, if user has given role
     */
    @ModelElement(type = ModelElementType.OTHER)
    public boolean userHasRole(String roleName) {
        UserSession userSession = SessionManager.getUserSession();
        return userSession != null && userSession.getSystemUser().getBusinessRoles().stream().anyMatch(
                userRole -> roleName.equalsIgnoreCase(userRole.getRoleName()));

    }

    /**
     * Checks if current user has role
     * @param roleNames role names
     * @return true, if user has given role
     */
    @ModelElement(type = ModelElementType.OTHER)
    public boolean userHasAnyRole(String... roleNames) {
        UserSession userSession = SessionManager.getUserSession();
        Set<String> roleNamesSet = Arrays.stream(roleNames).map(String::toUpperCase).collect(Collectors.toSet());
        return userSession != null && userSession.getSystemUser().getBusinessRoles().stream().anyMatch(
                userRole -> roleNamesSet.contains(userRole.getRoleName().toUpperCase()));

    }

    public void setOnManualModalClose(ActionBinding onManualModalClose) {
        this.onManualModalClose = onManualModalClose;
    }

    public IActionCallbackContext setOnManualModalClose(IActionCallback onManualModalClose) {
        return CallbackActionBinding.createAndSet(onManualModalClose, this::setOnManualModalClose);
    }

    public String resolveLabel() {
        if (getLabelModelBinding() != null) {
            return getLabelModelBinding().getBindingExpression();
        }
        return getLabel();
    }

    public interface IFormGenerationUtils extends Component.IGenerationUtils {
        Set<ActionSignature> getEvents();
    }
}
