package pl.fhframework.model.forms;

import org.junit.Assert;
import org.junit.Test;

import pl.fhframework.binding.AdHocModelBinding;

public class TreeTest {

    @Test
    public void test() {
        Tree tree = new Tree(null);
        tree.setBindingForNodes(new AdHocModelBinding<>(null, null, "{nodes}"));
        tree.setIterator("node");
        tree.setRelation("children");

        Assert.assertEquals("{nodes[3].label}", tree.replaceBinding("{node.label}", new int[]{3}, 0));
        Assert.assertEquals("{nodes[3].children[7].label}", tree.replaceBinding("{node.label}", new int[]{3, 7}, 1));
        Assert.assertEquals("{nodes[3].children[7].children[5].label}", tree.replaceBinding("{node.label}", new int[]{3, 7, 5}, 2));

        Assert.assertEquals("nodes", tree.buildCollectionBinding(new int[]{}, 0));
        Assert.assertEquals("nodes[3].children", tree.buildCollectionBinding(new int[]{3}, 1));
        Assert.assertEquals("nodes[3].children[7].children", tree.buildCollectionBinding(new int[]{3, 7}, 2));
        Assert.assertEquals("nodes[3].children[7].children[5].children", tree.buildCollectionBinding(new int[]{3, 7, 5}, 3));
    }
}