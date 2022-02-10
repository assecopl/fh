package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.Expression;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.util.SpelUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

@Control(parents = {TreeFhDP.class}, canBeDesigned = true)
@CompilationNotSupportedIterable
public class TreeElementFhDP extends GroupingComponent<TreeElementFhDP> implements IHasBoundableLabel, IChangeableByClient {

    private static final String ATTR_ICON = "icon";
    private static final String ATTR_URL = "url";
    private static final String ATTR_ON_LABEL_CLICK = "onLabelClick";
    private static final String ATTR_ON_ICON_CLICK = "onIconClick";
    private static final String ON_LAZY_LOAD = "onLazyLoad";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_SELECTED = "selected";
    private static final String ATTR_COLLAPSED = "collapsed";
    private static final String ATTR_NEXT_LEVEL_EXPANDABLE = "nextLevelExpandable";
    private static final String ATTR_EXPANDED_EXCEPTION = "expandedException";
    private static final String ATTR_IS_HIGHLIGHT = "isHighlight";
    private static final String ATTR_HIGHLIGHT_COLOR = "highlightColor";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_EXPANDED_EXCEPTION)
    private ModelBinding<String> expandedExceptionBinding;

    @Getter
    private String expandedException;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    private ModelBinding labelModelBinding;

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ICON)
    private ModelBinding iconBinding;

    @Getter
    private String icon;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_URL)
    private ModelBinding urlBinding;  // not supported in js. make it work or remove this

    @Getter
    private String url; // not supported in js. make it work or remove this

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_HIGHLIGHT_COLOR)
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    private ModelBinding<String> highlightColorBinding;

    @Getter
    private String highlightColor;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_IS_HIGHLIGHT)
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    private ModelBinding<Boolean> isHighlightBinding;

    @Getter
    private Boolean isHighlight;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(value = ATTR_ON_LABEL_CLICK)
    private ActionBinding onLabelClick;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(value = ATTR_ON_ICON_CLICK)
    private ActionBinding onIconClick;

    @Getter
    private boolean collapsed = true;

    private boolean wasSelected = false;
    private boolean wasCollapsed = true;

    @Getter
    @Setter
    @JsonIgnore
    private int level;

    @Getter
    @Setter
    @JsonIgnore
    private int[] indices;

    @JsonIgnore
    @Setter
    @Getter
    private String collectionBinding;

    @JsonIgnore
    @Setter
    @Getter
    private String elementBinding;

    @JsonIgnore
    @Setter
    @Getter
    @XMLMetadataSubelementParent
    private TreeFhDP tree;

    @JsonIgnore
    @Getter
    private boolean childrenLoaded;

    @JsonIgnore
    private boolean shouldIgnoreLazy = false;

    @Getter
    @Setter
    private boolean nextLevelExpandable;

    @Getter
    @Setter
    private boolean selected;

    @JsonIgnore
    private Expression nextLevelLazyExpandableExpression;

    public TreeElementFhDP(Form form) {
        super(form);
    }

    public void init() {
        super.init();
        if(expandedExceptionBinding != null){
            BindingResult<String> bindingResultExpandedException = expandedExceptionBinding.getBindingResult();
            if(bindingResultExpandedException != null){
                if(bindingResultExpandedException.getValue() != null){
                    this.expandedException = bindingResultExpandedException.getValue();
                }
            }
        }
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ATTR_ON_LABEL_CLICK.equals(eventData.getEventType())) {
            if (this.selected) {
                tree.onSelectionClick(getBoundObject());
            }
            return Optional.ofNullable(onLabelClick);
        } else if (ATTR_ON_ICON_CLICK.equals(eventData.getEventType())) {
            return Optional.ofNullable(onIconClick);
        } else if (ON_LAZY_LOAD.equals(eventData.getEventType())) {
            shouldIgnoreLazy = true;
            return Optional.empty();
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void processComponents() {
        // already processed and not dynamic - not processing again
        if (childrenLoaded && !tree.isDynamic()) {
            return;
        }

        // not processed yet and is lazy - not processing until expanded (shouldIgnoreLazy means already expanded)
        if (!childrenLoaded && tree.isLazy() && !shouldIgnoreLazy) {
            return;
        }

        tree.processComponentsForLevel(indices, level + 1, this);
        childrenLoaded = true;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (valueChange.hasAttributeChanged(ATTR_SELECTED)) {
            this.wasSelected = this.selected;
            boolean newSelected = valueChange.getBooleanAttribute(ATTR_SELECTED);
            if (newSelected) {
                this.tree.deselectAll();
            }
            this.selected = newSelected;
        }
        if (valueChange.hasAttributeChanged(ATTR_COLLAPSED)) {
            this.wasCollapsed = this.collapsed;
            this.collapsed = valueChange.getBooleanAttribute(ATTR_COLLAPSED);
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (expandedExceptionBinding != null) {
            BindingResult<String> bindingResult = expandedExceptionBinding.getBindingResult();
            if (bindingResult != null) {
                String newExpandedException = bindingResult.getValue();
                if (!areValuesTheSame(newExpandedException, expandedException)) {
                    this.expandedException = newExpandedException;
                    elementChanges.addChange(ATTR_EXPANDED_EXCEPTION, expandedException);
                }
            }
        }
        if (iconBinding != null) {
            BindingResult<String> bindingResult = iconBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = bindingResult.getValue();
                if (!areValuesTheSame(newLabelValue, icon)) {
                    this.icon = newLabelValue;
                    elementChanges.addChange(ATTR_ICON, icon);
                }
            }
        }
        if (urlBinding != null) {
            BindingResult<String> bindingResult = urlBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = bindingResult.getValue();
                if (!areValuesTheSame(newLabelValue, url)) {
                    this.url = newLabelValue;
                    elementChanges.addChange(ATTR_URL, url);
                }
            }
        }
        if (labelModelBinding != null) {
            BindingResult<String> bindingResult = labelModelBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = bindingResult.getValue();
                if (!areValuesTheSame(newLabelValue, label)) {
                    this.label = newLabelValue;
                    elementChanges.addChange(ATTR_LABEL, label);
                }
            }
        }

        if(isHighlightBinding != null){
            BindingResult<Boolean> bindingResult = isHighlightBinding.getBindingResult();
            if (bindingResult != null) {
                Boolean newIsHighlightValue = bindingResult.getValue();
                if (!areValuesTheSame(newIsHighlightValue, this.isHighlight)) {
                    this.isHighlight = newIsHighlightValue;
                    elementChanges.addChange(ATTR_IS_HIGHLIGHT, this.isHighlight);
                }
            }
        }

        if(highlightColorBinding != null){
            BindingResult<String> bindingResult = highlightColorBinding.getBindingResult();
            if (bindingResult != null) {
                String newhighlightColorValue = bindingResult.getValue();
                if (!areValuesTheSame(newhighlightColorValue, this.highlightColor)) {
                    this.highlightColor = newhighlightColorValue;
                    elementChanges.addChange(ATTR_HIGHLIGHT_COLOR, this.highlightColor);
                }
            }
        }

        boolean isSelected = tree.isSelected(getBoundObject());
        if (wasSelected != isSelected) {
            this.selected = isSelected;
            wasSelected = isSelected;
            elementChanges.addChange(ATTR_SELECTED, this.selected);
        }

        if (wasCollapsed != collapsed) {
            wasCollapsed = collapsed;
            elementChanges.addChange(ATTR_COLLAPSED, this.collapsed);
        }

        Boolean newNextLevelExpandable;
        if (tree.isLazy() && !childrenLoaded) {
            // simple expression cache
            if (nextLevelLazyExpandableExpression == null) {
                nextLevelLazyExpandableExpression = SpelUtils.parseExpression(StringUtils.removeSurroundingBraces(tree.getNextLevelExpandableExpression()));
            }

            try {
                newNextLevelExpandable = (Boolean) SpelUtils.evaluateExpression(nextLevelLazyExpandableExpression, getBoundObject());
                if (newNextLevelExpandable == null) {
                    newNextLevelExpandable = false;
                }
            } catch (ClassCastException e) {
                throw new FhBindingException(tree.getNextLevelExpandableExpression() + " is not a boolean expression");
            }

        } else {
            newNextLevelExpandable = !getSubcomponents().isEmpty();
        }

        if (nextLevelExpandable != newNextLevelExpandable.booleanValue()) {
            nextLevelExpandable = newNextLevelExpandable;
            elementChanges.addChange(ATTR_NEXT_LEVEL_EXPANDABLE, nextLevelExpandable);
        }

        return elementChanges;
    }

    private Object getBoundObject() {
        if (this.collectionBinding != null) {
            BindingResult bindingResult = getForm().getBindingResult(this.elementBinding, this);
            if (bindingResult != null) {
                return bindingResult.getValue();
            }
        }
        return null;
    }

    public void setOnLabelClick(ActionBinding onLabelClick) {
        this.onLabelClick = onLabelClick;
    }

    public IActionCallbackContext setOnLabelClick(IActionCallback onLabelClick) {
        return CallbackActionBinding.createAndSet(onLabelClick, this::setOnLabelClick);
    }

    public void setOnIconClick(ActionBinding onIconClick) {
        this.onIconClick = onIconClick;
    }

    public IActionCallbackContext setOnIconClick(IActionCallback onIconClick) {
        return CallbackActionBinding.createAndSet(onIconClick, this::setOnIconClick);
    }
}

