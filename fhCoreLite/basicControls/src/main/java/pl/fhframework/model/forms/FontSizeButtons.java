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
@TemplateControl(tagName = "fh-font-size-buttons")
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.BUTTONS_AND_OTHER, documentationExample = true, value = "PanelGroup component responsible for the grouping of buttons.", icon = "fa fa-square")
public class FontSizeButtons extends GroupingComponent<Component> implements Boundable, IChangeableByClient, CompactLayout, IDesignerEventListener, IHasBoundableLabel {

    private static final String ATTR_ACTIVE_BUTTON = "activeButton";
    private static final String ATTR_CSS_CLASS2 = "cssClass2";
    private static final String ATTR_CSS_CLASS4 = "cssClass4";
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
    private String cssClass2 = "";
    @Getter
    private String cssClass4 = "";

    private boolean buttonAdded = false;

    public FontSizeButtons(Form form) {
        super(form);
        AutowireHelper.autowire(this, WCAGService);
        AutowireHelper.autowire(this, messageService);

        if (WCAGService != null) {
            Boolean isFontSize2 = WCAGService.isFontSize2();
            Boolean isFontSize4 = WCAGService.isFontSize4();
            if (isFontSize2) {
                this.activeButton = 1;
            } else if (isFontSize4) {
                this.activeButton = 2;
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
        if (WCAGService != null) {
            Boolean isFontSize2 = WCAGService.isFontSize2();
            Boolean isFontSize4 = WCAGService.isFontSize4();
            if (isFontSize2) {
                this.activeButton = 1;
            } else if (isFontSize4) {
                this.activeButton = 2;
            } else {
                this.activeButton = 0;
            }
        }
        if (newActiveButton != this.activeButton) {
            this.activeButton = newActiveButton;
            this.updateBindingForValue(newActiveButton, activeButtonBinding, activeButton);
            if (newActiveButton == 1) {
                WCAGService.setFontSize2();
            } else if (newActiveButton == 2) {
                WCAGService.setFontSize4();
            } else {
                WCAGService.setNormalFontSize();
            }
        }
    }

    @Override
    protected ElementChanges updateView() {
        if (this.getSubcomponents().size() == 0 && !buttonAdded && this.isInitDone()) {
            addSubcomponent(createNormalButton());
            addSubcomponent(createFontSize2Button());
            addSubcomponent(createFontSize4Button());
            buttonAdded = true;
        }
        ElementChanges elementChanges = super.updateView();
        resolveBindingForLabel(elementChanges);

        Boolean isFontSize2 = WCAGService.isFontSize2();
        Boolean isFontSize4 = WCAGService.isFontSize4();
        if (isFontSize2) {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 1);
        } else if (isFontSize4) {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 2);
        } else {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 0);
        }

        if (!this.cssClass2.equals(WCAGService.getCssClassForSize2())) {
            this.cssClass2 = WCAGService.getCssClassForSize2();
            elementChanges.addChange(ATTR_CSS_CLASS2, this.cssClass2);
        }

        if (!this.cssClass4.equals(WCAGService.getCssClassForSize4())) {
            this.cssClass4 = WCAGService.getCssClassForSize4();
            elementChanges.addChange(ATTR_CSS_CLASS4, this.cssClass4);
        }


        return elementChanges;
    }

    private void resolveBindingForLabel(ElementChanges elementChanges) {
        if (labelModelBinding != null) {
            label = labelModelBinding.resolveValueAndAddChanges(this, elementChanges, label, LABEL_ATTR);
        }
    }

    private Button createNormalButton() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.normal");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.normal.ariaLabel");
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>(msg));
        button.setAriaLabelBinding(new StaticBinding<>(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-font-size-btn-normal");
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }

    private Button createFontSize2Button() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.size2");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.size2.ariaLabel");
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>(msg));
        button.setAriaLabelBinding(new StaticBinding<>(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-font-size-btn-2");
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }

    private Button createFontSize4Button() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.size4");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.fontSizeButtons.size4.ariaLabel");
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>(msg));
        button.setAriaLabelBinding(new StaticBinding<>(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-font-size-btn-4");
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }
}
