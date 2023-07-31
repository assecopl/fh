package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.forms.utils.WCAGService;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Created by k.czajkowski on 03.01.2017.
 */
@TemplateControl(tagName = "fh-high-contrast-buttons")
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.BUTTONS_AND_OTHER, documentationExample = true, value = "PanelGroup component responsible for the grouping of buttons.", icon = "fa fa-square")
public class HighContrastButtons extends GroupingComponent<Component> implements Boundable, IChangeableByClient, CompactLayout, IDesignerEventListener, IHasBoundableLabel {

    private static final String ATTR_ACTIVE_BUTTON = "activeButton";
    private static final String ATTR_CSS_CLASS = "cssClass";
//    public static final String ATTR_ON_BUTTON_CHANGE = "onContrastChange";
//    private static final String DEFAULT_ACTION_NAME = "-";

    public static final String LABEL_ATTR = "label";

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = LABEL_ATTR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - FH Markup Language.")
    private ModelBinding<String> labelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ACTIVE_BUTTON)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(boundable = true, value = "Index of active Button. Default value of active button is 0 - high contrast is not activated.")
    private ModelBinding activeButtonBinding;

//    @Getter
//    @DesignerXMLProperty(functionalArea = BEHAVIOR)
//    @XMLProperty(defaultValue = DEFAULT_ACTION_NAME)
//    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on clicking on button, that method will be executed. This method fires, when button is clicked.")
//    private ActionBinding onContrastChange;

    @Autowired
    @JsonIgnore
    WCAGService WCAGService;

    @Autowired
    @JsonIgnore
    MessageService messageService;

    @Getter
    private int activeButton = 0;

    @Getter
    private String cssClass = "";

    private boolean buttonAdded = false;

    public HighContrastButtons(Form form) {
        super(form);
        AutowireHelper.autowire(this, WCAGService);
        AutowireHelper.autowire(this, messageService);

        if (WCAGService != null) {
            Boolean isHighContrast = WCAGService.isHighContrast();
            if (isHighContrast) {
                this.activeButton = 1;
            } else {
                this.activeButton = 0;
            }
        }


    }

    @Override
    public void updateModel(ValueChange valueChange) {
        String newValue = valueChange.getMainValue();
        int newActiveButton = Integer.parseInt(newValue);
        Boolean isHighContrast = WCAGService.isHighContrast();
        if (isHighContrast) {
            this.activeButton = 1;
        } else {
            this.activeButton = 0;
        }
        if (newActiveButton != this.activeButton) {
            this.activeButton = newActiveButton;
            this.updateBindingForValue(newActiveButton, activeButtonBinding, activeButton);
            if (newActiveButton == 1) {
                WCAGService.setHighContrast();
            } else {
                WCAGService.setNormalContrast();
            }
        }
    }

    @Override
    protected ElementChanges updateView() {
        if (this.getSubcomponents().size() == 0 && !buttonAdded && this.isInitDone()) {
            addSubcomponent(createNormalButton());
            addSubcomponent(createHighContrastButton());
            buttonAdded = true;
        }
        ElementChanges elementChanges = super.updateView();
        resolveBindingForLabel(elementChanges);

        Boolean isHighContrast = WCAGService.isHighContrast();
        if (isHighContrast) {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 1);
        } else {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 0);
        }

        if (!this.cssClass.equals(WCAGService.getCssClassForContrast())) {
            this.cssClass = WCAGService.getCssClassForContrast();
            elementChanges.addChange(ATTR_CSS_CLASS, this.cssClass);
        }

        return elementChanges;
    }

//    @Override
//    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
//        if (ATTR_ON_BUTTON_CHANGE.equals(eventData.getEventType())) {
//            return Optional.ofNullable(onContrastChange);
//        } else {
//            return super.getEventHandler(eventData);
//        }
//    }

//    @Override
//    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
//        addSubcomponent(createExampleButton(getSubcomponents().size() + 1));
//    }

//    @Override
//    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
//        addSubcomponent(createExampleButton(1));
//        addSubcomponent(createExampleButton(2));
//    }

//    public void setOnContrastChange(ActionBinding onContrastChange) {
//        this.onContrastChange = onContrastChange;
//    }

//    public IActionCallbackContext setOnContrastChange(IActionCallback onButtonChange) {
//        return CallbackActionBinding.createAndSet(onButtonChange, this::setOnContrastChange);
//    }

    private void resolveBindingForLabel(ElementChanges elementChanges) {
        if (labelModelBinding != null) {
            label = labelModelBinding.resolveValueAndAddChanges(this, elementChanges, label, LABEL_ATTR);
        }
    }

    private Button createNormalButton() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.highContrastButtons.normal");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.highContrastButtons.normal.ariaLabel");
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>(msg));
        button.setAriaLabelBinding(new StaticBinding<>(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-high-contrast-btn-normal");
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }

    private Button createHighContrastButton() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.highContrastButtons.hightContrast");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.highContrastButtons.hightContrast.ariaLabel");
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>(msg));
        button.setAriaLabelBinding(new StaticBinding<>(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DARK.toValue()));
        button.setStyleClasses("mr-2, fh-high-contrast-btn-high");
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }
}
