package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.model.LabelPosition;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.widgets.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Class representing xml component of Image. Every field represents xml attribute of image tag. <p>
 * Example <Image src="src_1" zr="bindSource_1"/>. <p> Every field is parsed as json for javascript.
 * If field should be ingored by JSON, use <code>@JsonIgnore</code>. There can be used any
 * annotations for json generator.
 */
@Getter
@Setter
@DesignerControl(defaultWidth = 6)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class, Widget.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(value = "Image component for displaying image and marking points on it (optionally)", icon = "fa fa-image")
public class Image extends FormElement implements TableComponent<Image> {

    public static final String ATTR_SRC = "src";
    public static final String ATTR_ON_AREA_CLICK = "onAreaClick";
    public static final String ATTR_ON_CLICK = "onClick";
    public static final String ATTR_IMAGE_AREAS = "imageAreas";
    public static final String ATTR_LABEL = "label";

    @Getter
    private String src;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SRC)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source")
    private ModelBinding<String> srcModelBinding;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "If the image is clicked that method will be executed. Action is fired, while component is active.")
    private ActionBinding onClick;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "If the image area is clicked that method will be executed. Action is fired, while component is active.")
    private ActionBinding onAreaClick;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_IMAGE_AREAS)
    @DocumentedComponentAttribute(value = "Component area parameter containing id and coordinates")
    private String imageAreasAttribute;

    private List<ImageArea> imageAreas;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 95)
    @DocumentedComponentAttribute(value = "Defines position of a label. Position is one of: up, down, left, right.")
    private LabelPosition labelPosition;

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - FH Markup Language.")
    private ModelBinding labelModelBinding;

    public Image(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.imageAreas = parseAreas(imageAreasAttribute);
    }

    private List<ImageArea> parseAreas(String map) {
        if (map == null) {
            return null;
        }
        List<ImageArea> result = new ArrayList<>();
        //map="(#1,0,0,20,20); (#2, 0, 20, 20, 40);(#3, 20,0, 40,20); (#4, 20,20, 40, 40)"
        String[] areasString = map.replace(" ", "").split(";");
        for (String areaString : areasString) {
            try {
                areaString = areaString.substring(1, areaString.length() - 1);
                String[] parameters = areaString.split(",");
                String id = parameters[0].substring(1);
                int xl = Integer.parseInt(parameters[1]);
                int yl = Integer.parseInt(parameters[2]);
                int xp = Integer.parseInt(parameters[3]);
                int yp = Integer.parseInt(parameters[4]);
                result.add(new ImageArea(id, xl, yl, xp, yp));

            } catch (Exception exc) {
                FhLogger.error("Error in parsing of area: '{}' image '{}'!", areaString, this.getId(), exc);
            }
        }

        return result;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().startsWith(ATTR_ON_AREA_CLICK)) {
            String[] splitEvent = eventData.getEventType().split("#");
            eventData.setEventType(splitEvent[0]);
            if (splitEvent.length == 2) {
                eventData.setOptionalValue(splitEvent[1]);
            }

            return Optional.ofNullable(onAreaClick);
        } else if (ATTR_ON_CLICK.equals(eventData.getEventType())) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (srcModelBinding != null) {
            src = srcModelBinding.resolveValueAndAddChanges(this, elementChange, src, ATTR_SRC);
        }
        if(labelModelBinding != null) {
            label = labelModelBinding.resolveValueAndAddChanges(this, elementChange, label, ATTR_LABEL);
        }

        return elementChange;
    }

    @Override
    public Image createNewSameComponent() {
        return new Image(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, Image clone) {
        TableComponent.super.doCopy(table, iteratorReplacements, clone);
        clone.setSrc(this.getSrc());
        clone.setOnClick(table.getRowBinding(this.getOnClick(), clone, iteratorReplacements));
        clone.setOnAreaClick(table.getRowBinding(this.getOnAreaClick(), clone, iteratorReplacements));
        clone.setLabelModelBinding(table.getRowBinding(this.getLabelModelBinding(), clone, iteratorReplacements));
        clone.setImageAreasAttribute(getImageAreasAttribute());
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }

    public IActionCallbackContext setOnClick(IActionCallback onClick) {
        return CallbackActionBinding.createAndSet(onClick, this::setOnClick);
    }

    public void setOnAreaClick(ActionBinding onAreaClick) {
        this.onAreaClick = onAreaClick;
    }

    public IActionCallbackContext setOnAreaClick(IActionCallback onAreaClick) {
        return CallbackActionBinding.createAndSet(onAreaClick, this::setOnAreaClick);
    }
}
