package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.forms.designer.SizePxDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.ARRANGEMENT, value = "Container component responsible for placing components on a single line with movable column", icon = "fa fa-columns")
public class SplitContainer extends GroupingComponent<FormElement> implements IDesignerEventListener, CompactLayout {

    public static final String SIZE_LEFT_ATTR = "sizeLeft";
    public static final String SIZE_RIGHT_ATTR = "sizeRight";

    @Getter
    private String sizeLeft;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(SIZE_LEFT_ATTR)
    @DocumentedComponentAttribute("Defines width in percent of the left panel of this SplitContainer")
    @DesignerXMLProperty(previewValueProvider = SizePxDesignerPreviewProvider.class)
    private ModelBinding sizeLeftBinding;

    @Getter
    private String sizeRight;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(SIZE_RIGHT_ATTR)
    @DocumentedComponentAttribute("Defines width in percent of the right panel of this SplitContainer")
    @DesignerXMLProperty(previewValueProvider = SizePxDesignerPreviewProvider.class)
    private ModelBinding sizeRightBinding;

    public SplitContainer(Form form) {
        super(form);
    }

    @Override
    public void addSubcomponent(FormElement formElement) {
        if (getSubcomponents().size() >= 2) {
            final String errorMessage = "SplitContainer can't have more than 2 subcomponents!! [id: " + getId();
            FhLogger.error(errorMessage);
            throw new FhException(errorMessage);
        }
        super.addSubcomponent(formElement);
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createGroup(spacerService));
        addSubcomponent(createGroup(spacerService));
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (sizeLeftBinding != null) {
            sizeLeft = sizeLeftBinding.resolveValueAndAddChanges(this, elementChanges, sizeLeft, SIZE_LEFT_ATTR);
        }
        if (sizeRightBinding != null) {
            sizeRight = sizeRightBinding.resolveValueAndAddChanges(this, elementChanges, sizeRight, SIZE_RIGHT_ATTR);
        }
        return elementChanges;
    }

    private FormElement createGroup(SpacerService spacerService) {
        Group group = new Group(getForm());
        group.setWidth("md-12");
        group.init();
        group.setGroupingParentComponent(this);
        return group;
    }
}
