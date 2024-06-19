package pl.fhframework.docs.uc;

import org.apache.commons.lang.math.RandomUtils;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.TreeForm;
import pl.fhframework.docs.forms.component.model.TreeElement;
import pl.fhframework.docs.forms.model.example.NodeExample;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;

import java.util.List;


/**
 * Use case supporting Tree documentation
 */
@UseCase
public class TreeUC implements IDocumentationUseCase<TreeElement> {
    private TreeElement model;

    @Override
    public void start(TreeElement model) {
        this.model = model;
        showForm(TreeForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeNodeDynamically(NodeExample selectedNode) {
        removeNodeDynamically(model.getNodes(), selectedNode);
        model.setSelectedNode(null);
    }

    private boolean removeNodeDynamically(List<NodeExample> nodes, NodeExample node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
            return true;
        } else {
            for (NodeExample otherNode : nodes) {
                if (removeNodeDynamically(otherNode.getChildren(), node)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addNodeDynamically(List<NodeExample> parentChildren) {
        int id = RandomUtils.nextInt();
        NodeExample addedNode = new NodeExample(id);
        addedNode.setName("node " + id);
        //addedNode.setIcon("fa fa-file");
        parentChildren.add(addedNode);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void updateTreeMessage() {
        TreeElement treeElement = model;
        treeElement.incrementCounter();
        treeElement.setOnClickedMessage("Component clicked " + treeElement.getCounter() + " times.");
    }



}

