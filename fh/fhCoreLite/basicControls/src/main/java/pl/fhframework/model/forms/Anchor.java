package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Objects;

/**
 * Anchor component allows to scroll to place where it was placed
 * Example: {@code <ScrollHere/>}
 */


@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Form.class, Repeater.class, Group.class, SplitContainer.class}, invalidParents = {Anchor.class, Table.class}, canBeDesigned = false)
@DocumentedComponent(category = DocumentedComponent.Category.OTHERS, value = "Anchor component is responsible for the scrolling whole page or scorllable container to specific invisiable point.", icon = "fa fa-anchor")
public class Anchor extends FormElement implements Boundable {
    private static final String SCROLL_ATTR = "scroll";
    private static final String ANIMATE_DURATION_ATTR = "animateDuration";

    public Anchor(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (scrollBinding != null) {
            BindingResult scrollBidingResult = scrollBinding.getBindingResult();
            if (scrollBidingResult != null) {
                if (scrollBidingResult.getValue() != null) {
                    this.scroll = convertValue(scrollBidingResult.getValue(), Boolean.class);
                }
            }
        }
        if (animateDurationModelBinding != null) {
            BindingResult animateDurationResult = animateDurationModelBinding.getBindingResult();
            if (animateDurationResult != null) {
                if (animateDurationResult.getValue() != null) {
                    this.animateDuration = convertValue(animateDurationResult.getValue(), Integer.class);
                }
            }
        }
    }

    /**
     * Horizontal-align of the child components
     */
    @Getter
    @Setter
    @XMLProperty
    private Boolean scrollOnStart;

    @Getter
    private Integer animateDuration = 0; //miliseconds

    @Getter
    private Boolean scroll = false;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = SCROLL_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from model of Form, used inside of '{}', like {model}.")
    private ModelBinding<Boolean> scrollBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ANIMATE_DURATION_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from model of Form, used inside of '{}', like {model}.")
    private ModelBinding<Integer> animateDurationModelBinding;


    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (scrollBinding != null) {
            BindingResult scrollBindingResult = scrollBinding.getBindingResult();
            if (scrollBindingResult != null) {
                boolean newScroll = this.scroll;
                Object value = scrollBindingResult.getValue();
                if (value instanceof Boolean) {
                    newScroll = (boolean) value;
                }
                if (!Objects.equals(newScroll, this.scroll)) {
                    //Never change base value off scroll attribiute. We need to always react on "true" value from scrollbinding.
                    elementChanges.addChange(SCROLL_ATTR, newScroll);
                    scrollBinding.setValue(false);
                }
            }
        }
        return elementChanges;
    }

}
