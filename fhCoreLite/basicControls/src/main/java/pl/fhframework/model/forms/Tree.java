package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.AdHocActionBinding;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.ElementChanges;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This component can be used in XML Form. Most logic is based on component - Repeater, for dynamic
 * binding resolving and cloning components.
 */
@DocumentedComponent(category = DocumentedComponent.Category.TABLE_AND_TREE, documentationExample = true, ignoreFields = {"width"},
        value = "Tree component is responsible for display certain relation between parent and childs. "
                + "This component is similar too Repeater, but relation is visible.", icon = "fa fa-tree")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@CompilationNotSupportedIterable
public class Tree extends GroupingComponent<TreeElement> implements Boundable, CompactLayout, IRepeatableComponentsHolder {

    private static final String ATTR_LAZY = "lazy";
    private static final String ATTR_NEXT_LEVEL_ACTIVE = "nextLevelExpandableExpression";

    @JsonIgnore
    @Getter
    @XMLProperty(value = "collection")
    @DesignerXMLProperty(allowedTypes = Collection.class, commonUse = true)
    @DocumentedComponentAttribute(boundable = true, value = "Collection of data to be displayed")
    private ModelBinding bindingForNodes;

    @JsonIgnore
    @Getter
    @Setter
    @DocumentedComponentAttribute(boundable = true, value = "Name of the property in object, that should process nested relation")
    @XMLProperty
    @DesignerXMLProperty(commonUse = true)
    private String relation;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_NEXT_LEVEL_ACTIVE, defaultValue = "true")
    @DocumentedComponentAttribute(boundable = true, value = "Current object based expression which checks if next level should be expandable, which means that state / type of the object suggests that it has / may have child nodes. Returned value may be false-positive which results in disapearing of expand icon after clicking on it. Used only when lazy=true.", defaultValue = "true")
    private String nextLevelExpandableExpression;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true)
    @DocumentedComponentAttribute(value = "Name of the iterator variable used to refer each data")
    private String iterator;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty("selected")
    @DocumentedComponentAttribute(boundable = true, value = "Selected object")
    private ModelBinding selectedBinding;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "fas fa-caret-down")
    @DocumentedComponentAttribute(defaultValue = "fas fa-caret-down", value = "Collapse icon")
    private String nodeIcon = "fas fa-caret-down";

    @Getter
    @Setter
    @XMLProperty(defaultValue = "fas fa-caret-right")
    @DocumentedComponentAttribute(defaultValue = "fas fa-caret-right", value = "Expand icon")
    private String collapsedNodeIcon = "fas fa-caret-right";

    @Getter
    @Setter
    @XMLProperty(defaultValue = "hidden")
    @DocumentedComponentAttribute(value = "Leaf icon, displayed when node has no children. Default is 'hidden'")
    private String leafIcon = "hidden";

    @Getter
    @Setter
    @XMLProperty(defaultValue = "true")
    @DocumentedComponentAttribute(value = "Draw lines between parent and children. Default is 'true'")
    private boolean lines = true;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "true")
    @DocumentedComponentAttribute(value = "Is tree processing itself, once it was displayed.", defaultValue = "true")
    private boolean dynamic = true;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    @DocumentedComponentAttribute(value = "Are all levels already opened by default. This option is ignored when lazy = true.", defaultValue = "false")
    private boolean expanded = false;

    @Getter
    @Setter
    @XMLProperty(value = ATTR_LAZY, defaultValue = "false")
    @DocumentedComponentAttribute(value = "Are tree node lazy loaded while user expands them.", defaultValue = "false")
    private boolean lazy = false;

    @JsonIgnore
    @Getter
    private boolean childrenLoaded;

    @Getter
    @Setter
    @JsonIgnore
    @XMLMetadataSubelement
    private TreeElement templateTreeElement;

    public Tree(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();

        // template element must be not null, this will be also handy in the form designer
        if (templateTreeElement == null) {
            templateTreeElement = new TreeElement(getForm());
            templateTreeElement.setTree(this);
        }

        templateTreeElement.init();
    }

    public void setBindingForNodes(ModelBinding bindingForNodes) {
        this.bindingForNodes = bindingForNodes;
    }

    @Override
    protected ElementChanges updateView() {
        return super.updateView();
    }

    @Override
    public void processComponents() {
        // already processed and not dynamic - not processing again
        if (this.childrenLoaded && !dynamic) {
            return;
        }

        processComponentsForLevel(new int[0], 0, this);
        childrenLoaded = true;
    }

    void processComponentsForLevel(int[] parentIndices, int level, IGroupingComponent<TreeElement> groupingComponent) {
        // will expose template components instead of processing template to create new components
        if (getForm().getViewMode() != Form.ViewMode.NORMAL) {
            return;
        }

        String collectionBinding = buildCollectionBinding(parentIndices, level);
        BindingResult bindingResult = getForm().getBindingResult(collectionBinding, (Component) groupingComponent); // todo: save to TreeElement and add binding to processComponentsForLevel parameters.
        if (bindingResult != null && bindingResult.getValue() != null) {
            Collection bindedCollection = (Collection) bindingResult.getValue();
            if (bindedCollection.size() > groupingComponent.getSubcomponents().size()) {
                for (int i = groupingComponent.getSubcomponents().size(); i < bindedCollection.size(); i++) {
                    TreeElement clonedTreeElement = (TreeElement) templateTreeElement.clone().get();

                    int[] indices = Arrays.copyOf(parentIndices, parentIndices.length + 1);
                    indices[level] = i;
                    clonedTreeElement.setId(generateId(clonedTreeElement.getId(), indices));
                    clonedTreeElement.setIndices(indices);
                    clonedTreeElement.setLevel(level);
                    clonedTreeElement.setIconBinding(replaceModelBinding(clonedTreeElement.getIconBinding(), indices, level, clonedTreeElement));
                    clonedTreeElement.setLabelModelBinding(replaceModelBinding(clonedTreeElement.getLabelModelBinding(), indices, level, clonedTreeElement));
                    clonedTreeElement.setCollapsedModelBinding(replaceModelBinding(clonedTreeElement.getCollapsedModelBinding(), indices, level, clonedTreeElement));
                    clonedTreeElement.setUrlBinding(replaceModelBinding(clonedTreeElement.getUrlBinding(), indices, level, clonedTreeElement));
                    clonedTreeElement.setAvailabilityModelBinding(replaceModelBinding(clonedTreeElement.getAvailabilityModelBinding(), indices, level, clonedTreeElement));
                    if (clonedTreeElement.getOnLabelClick() != null) {
                        String onClickIndexed = replaceBinding(clonedTreeElement.getOnLabelClick().getActionBindingExpression(), indices, level);
                        clonedTreeElement.setOnLabelClick(new AdHocActionBinding(onClickIndexed, getForm(), clonedTreeElement));
                    }
                    if (clonedTreeElement.getOnIconClick() != null) {
                        String onClickIndexed = replaceBinding(clonedTreeElement.getOnIconClick().getActionBindingExpression(), indices, level);
                        clonedTreeElement.setOnIconClick(new AdHocActionBinding(onClickIndexed, getForm(), clonedTreeElement));
                    }
                    if (clonedTreeElement.getOnDesignerToolboxDrop() != null) {
                        String onDesignerToolboxDropIndexed = replaceBinding(clonedTreeElement.getOnDesignerToolboxDrop().getActionBindingExpression(), indices, level);
                        clonedTreeElement.setOnDesignerToolboxDrop(new AdHocActionBinding(onDesignerToolboxDropIndexed, getForm(), clonedTreeElement));
                    }

                    clonedTreeElement.setCollectionBinding(collectionBinding);
                    clonedTreeElement.setElementBinding(buildElementBinding(indices, level));

                    groupingComponent.getSubcomponents().add(clonedTreeElement);
                    clonedTreeElement.setGroupingParentComponent(groupingComponent);
                    clonedTreeElement.getBindingContext().getIteratorContext().putAll(((Component) groupingComponent).getBindingContext().getIteratorContext());
                    clonedTreeElement.getBindingContext().setCachePrefix(((Component) groupingComponent).getBindingContext().getCachePrefix());
                    getForm().addToElementIdToFormElement(clonedTreeElement);
                }
            } else if (bindedCollection.size() < groupingComponent.getSubcomponents().size()) {
                while (bindedCollection.size() < groupingComponent.getSubcomponents().size()) {
                    groupingComponent.getSubcomponents().remove(groupingComponent.getSubcomponents().size() - 1);
                }
            }
        }
        // dont comment this out if u don't have to
//        for (TreeElement treeElement : groupingComponent.getSubcomponents()) {
//            treeElement.processComponents();
//        }
    }

    ModelBinding replaceModelBinding(ModelBinding binding, int[] indices, int level, TreeElement clonedTreeElement) {
        if (binding == null || !StringUtils.hasSurroundingBraces(binding.getBindingExpression())) {
            return binding;
        } else {
            return new AdHocModelBinding<>(this.getForm(), clonedTreeElement, replaceBinding(binding.getBindingExpression(), indices, level));
        }
    }

    String replaceBinding(String binding, int[] indices, int level) {
        if (binding != null) {
            Pattern pattern = Pattern.compile("^(.*[\\(\\{\\s\\,])?(" + Matcher.quoteReplacement(iterator) + ")([\\,\\s\\}\\)\\.\\[].*)$");
            String elementBinding = buildElementBinding(indices, level);
            int counter = 0;
            do {
                Matcher bindingMatcher = pattern.matcher(binding);
                if (bindingMatcher.find()) {
                    String prefix = bindingMatcher.group(1) != null ? bindingMatcher.group(1) : "";
                    String suffix = bindingMatcher.group(3) != null ? bindingMatcher.group(3) : "";

                    binding = prefix + elementBinding + suffix;
                } else {
                    break; // no more occurrences of iterator
                }
            } while (counter++ < 20); // infinite loop protection
        }
        return binding;
    }

    private String buildElementBinding(int[] indices, int level) {
        return buildCollectionBinding(indices, level) + "[" + indices[level] + "]";
    }

    String buildCollectionBinding(int[] indices, int level) {
        String collectionBinding = StringUtils.removeSurroundingBraces(bindingForNodes.getBindingExpression());
        for (int i = 0; i < level; i++) {
            collectionBinding += "[" + indices[i] + "]." + StringUtils.removeSurroundingBraces(relation);
        }
        return collectionBinding;
    }

    private String generateId(String originalId, int[] indices) {
        for (int i : indices) {
            originalId += "[" + i + "]";
        }
        return originalId;
    }

    @Override
    public void refreshView(Set<ElementChanges> changeSet) {
        super.refreshView(changeSet);
    }


    public void onSelectionClick(Object anObject) {
        if (selectedBinding == null) {
            return;
        }
        if (selectedBinding.getBindingResult() != null && selectedBinding.getBindingResult().getValue() instanceof Collection) {
            Collection col = (Collection) selectedBinding.getBindingResult().getValue();
            if (col.contains(anObject)) {
                col.remove(anObject);
            } else {
                col.add(anObject);
            }
        } else {
            getForm().activateBindings();
            updateBindingForValue(anObject, selectedBinding, selectedBinding.getBindingExpression(), Optional.empty());
            getForm().deactivateBindings();
        }
    }

    @JsonIgnore
    public boolean isSelected(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (selectedBinding == null || selectedBinding.getBindingResult() == null) {
            return false;
        }
        if (selectedBinding.getBindingResult().getValue() instanceof Collection) {
            return ((Collection) selectedBinding.getBindingResult()).contains(anObject);
        } else {
            return anObject.equals(selectedBinding.getBindingResult().getValue());
        }
    }

    void deselectAll() {
        this.getSubcomponents().forEach(this::deselectAllChild);
    }

    private void deselectAllChild(TreeElement treeElement) {
        treeElement.setSelected(false);
        treeElement.getSubcomponents().forEach(this::deselectAllChild);
    }

    public boolean isSelectionOn() {
        return selectedBinding != null;
    }

    @Override
    public List<TreeElement> getSubcomponents() {
        // in design mode expose template tree element
        if (getForm().getViewMode() == Form.ViewMode.NORMAL) {
            return super.getSubcomponents();
        } else {
            return Arrays.asList(templateTreeElement);
        }
    }

    @Override
    public List<Component> getRepeatedComponents() {
        return Arrays.asList(templateTreeElement);
    }
}
