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

@TemplateControl(tagName = "fh-images-switch-buttons")
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class ImagesSwitchButtons extends  GroupingComponent<Component> implements Boundable, IChangeableByClient, CompactLayout, IDesignerEventListener, IHasBoundableLabel {

    public static final String LABEL_ATTR = "label";

    private static final String ATTR_CSS_CLASS = "cssClass";

    private static final String ATTR_ACTIVE_BUTTON = "activeButton";

    @Autowired
    @JsonIgnore
    pl.fhframework.model.forms.utils.WCAGService WCAGService;

    @Autowired
    @JsonIgnore
    MessageService messageService;
    private boolean buttonAdded = false;

    @Getter
    private String label;

    @Getter
    private int activeButton = 0;

    @Getter
    private String cssClass = "";

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

    public ImagesSwitchButtons(Form form) {
        super(form);

        AutowireHelper.autowire(this, WCAGService);
        AutowireHelper.autowire(this, messageService);

        if (WCAGService != null) {
            Boolean isImagesHidden = WCAGService.isImagesHidden();
            if (isImagesHidden) {
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
        Boolean isImagesHidden = WCAGService.isImagesHidden();

        if (isImagesHidden) {
            this.activeButton = 1;
        } else {
            this.activeButton = 0;
        }

        if (newActiveButton != this.activeButton) {
            this.activeButton = newActiveButton;
            this.updateBindingForValue(newActiveButton, activeButtonBinding, activeButton);
            if (newActiveButton == 1) {
                WCAGService.setImagesHidden();
            } else {
                WCAGService.setImagesShow();
            }
        }
    }

    @Override()
    protected ElementChanges updateView () {
        if (this.getSubcomponents().size() == 0 && !buttonAdded && this.isInitDone()) {
            addSubcomponent(createShowButton());
            addSubcomponent(createHideButton());

            buttonAdded = true;
        }

        ElementChanges elementChanges = super.updateView();
        resolveBindingForLabel(elementChanges);

        Boolean isImagesHidden = WCAGService.isImagesHidden();

        if (isImagesHidden) {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 1);
        } else {
            elementChanges.addChange(ATTR_ACTIVE_BUTTON, 0);
        }

        if (!this.cssClass.equals(WCAGService.getCssClassForImagesHidden())) {
            this.cssClass = WCAGService.getCssClassForImagesHidden();

            elementChanges.addChange(ATTR_CSS_CLASS, this.cssClass);
        }

        return elementChanges;
    }

    private void resolveBindingForLabel(ElementChanges elementChanges) {
        if (labelModelBinding != null) {
            label = labelModelBinding.resolveValueAndAddChanges(this, elementChanges, label, LABEL_ATTR);
        }
    }

    private Button createShowButton() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.hiddenImageButtons.show");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.hiddenImageButtons.show.ariaLabel");
        Button button = new Button(getForm());

        button.setLabelModelBinding(new StaticBinding(msg));
        button.setAriaLabelBinding(new StaticBinding(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-images-switch-btn-normal");
        button.setGroupingParentComponent(this);
        button.init();

        return button;
    }

    private Button createHideButton() {
        String msg = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.hiddenImageButtons.hide");
        String msgAria = messageService.getAllBundles().getMessage("pl.fhframework.model.forms.hiddenImageButtons.hide.ariaLabel");
        Button button = new Button(getForm());

        button.setLabelModelBinding(new StaticBinding(msg));
        button.setAriaLabelBinding(new StaticBinding(msgAria));
        button.setStyleModelBinding(new StaticBinding<>(Styleable.Style.DEFAULT.toValue()));
        button.setStyleClasses("border, mr-2, fh-images-switch-btn-hidden");
        button.setGroupingParentComponent(this);
        button.init();

        return button;
    }
}
