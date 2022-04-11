package pl.fhframework.dp.commons.fh.document.handling;


import pl.fhframework.dp.commons.fh.outline.ElementCT;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.*;


import java.util.HashMap;
import java.util.List;

public abstract class BaseDocumentHandlingOutlineForm<T extends BaseDocumentHandlingFormModel> extends Form<T> {
    String outlineMainName = "outlineMain";
    String outlineMessagesName = "messages";

    public abstract boolean isHasTabs();

    /**
     * Previous implementation:
     * getModel().getOperationQuery().getPerformerType().equals(PerformerEnum.USER)
     */
    public abstract boolean isPerformerUser();

    @Override
    public void init() {
        removeNotUsedElements();
        super.init();
    }

    protected void removeNotUsedElements() {
        if(isHasTabs()) {
            outlineMainName = "outlineMainTab";
            outlineMessagesName = "messagesTab";
        }
        Row mainContainer = (Row) getFormElement(outlineMainName);
        Row messagesContainer = (Row) getFormElement(outlineMessagesName);
        getForm().removeSubcomponent(mainContainer);
        getForm().removeSubcomponent(messagesContainer);
    }

    public void selectLeftMenuElementByLabel(String elementLabel){
        TreeFhDP tree = (TreeFhDP) getFormElement("leftMenuTreeRoot");
        TreeElementFhDP element = findTreeElementByLabel(tree, elementLabel);
        element.setSelected(true);
        expandTreeElementAncestors(element);
    }

    public String getFormElementIdByGroupId(
            pl.fhframework.dp.commons.fh.outline.TreeElement<ElementCT> treeElement, List<pl.fhframework.dp.commons.fh.outline.TreeElement<ElementCT>> menu, String treeElementsId){

        String numbersId = this.getFormElementIdByGroupId(treeElement, menu);
        return String.format("%s%s", treeElementsId, numbersId);
    }

    private String getFormElementIdByGroupId(
            pl.fhframework.dp.commons.fh.outline.TreeElement<ElementCT> treeElement, List<pl.fhframework.dp.commons.fh.outline.TreeElement<ElementCT>> menu){

        String childId;
        String parentId = "";
        int position = 0;

        for (TreeElement<ElementCT> el : menu) {
            if(el==treeElement) {
                parentId = String.format("[%s]", position);
            } else {
                childId = getFormElementIdByGroupId(treeElement, el.getChildren());
                if(childId != null) {
                    parentId = String.format("[%s]%s", position, childId);
                }
            }
            if (!parentId.equals("")){
                return parentId;
            }
            position++;
        }
        return null;
    }

    public String getFormElementId(String parentTreeId, String elementLabel, String parenLabel) {
        TreeFhDP tree = (TreeFhDP) getFormElement(parentTreeId);

        TreeElementFhDP elementParent = findTreeElementByLabel(tree, parenLabel);
        if(elementParent != null) {
            TreeElementFhDP element = findTreeElementByLabel(elementParent, elementLabel);
            return element != null ? element.getId() : null;
        }
        return null;
    }

    public String getFormElementId(String parentTreeId, String elementLabel) {
        TreeFhDP tree = (TreeFhDP) getFormElement(parentTreeId);
        TreeElementFhDP element = findTreeElementByLabel(tree, elementLabel);

        return element != null ? element.getId() : null;
    }

    public String getFormElementId(String parentTreeId, String elementLabel, Integer maxNext) {
        TreeFhDP tree = (TreeFhDP) getFormElement(parentTreeId);
        TreeElementFhDP element = findTreeElementByLabel(tree, elementLabel, maxNext);

        return element != null ? element.getId() : null;
    }

    private TreeElementFhDP findTreeElementByLabel(GroupingComponent<TreeElementFhDP> tree, String label, Integer maxNext) {
        TreeElementFhDP parent;

        if(maxNext > 0) {
            for (TreeElementFhDP el : tree.getSubcomponents()) {
                if (el.getLabel().equals(label)) {
                    parent = el;
                } else {
                    parent = findTreeElementByLabel(el, label, maxNext-1);
                }
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

    private static TreeElementFhDP findTreeElementByLabel(GroupingComponent<TreeElementFhDP> tree, String label) {
        TreeElementFhDP parent;

        for (TreeElementFhDP el : tree.getSubcomponents()) {
            if (el.getLabel().equals(label)) {
                parent = el;
            } else {
                parent = findTreeElementByLabel(el, label);
            }
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }

    private static void expandTreeElementAncestors(TreeElementFhDP element) {
        ValueChange valueChange = new ValueChange();
        HashMap<String, Object> changes = new HashMap<>();
        changes.put("collapsed", false);
        valueChange.setChangedAttributes(changes);
        element.updateModel(valueChange);

        IGroupingComponent<? extends Component> parent = element.getGroupingParentComponent();
        while (parent instanceof TreeElementFhDP) {
            ((TreeElementFhDP) parent).updateModel(valueChange);
            parent = ((TreeElementFhDP) parent).getGroupingParentComponent();
        }
    }

    public AccessibilityEnum isPerformerFieldReadOnly() {
        if(isPerformerUser()) {
            return AccessibilityEnum.EDIT;
        } else {
            return AccessibilityEnum.VIEW;
        }
    }
}
