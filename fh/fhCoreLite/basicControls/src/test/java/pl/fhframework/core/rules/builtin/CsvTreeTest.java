package pl.fhframework.core.rules.builtin;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests for CsvTree build-in rule.
 */
public class CsvTreeTest {

    private static final CsvTree RULE = new CsvTree();

    @Test
    public void testFlat() {
        List<CsvTreeElement> tree = RULE.csvTree("Aaa|Bbb|Ccc");
        Assert.assertEquals(3, tree.size());
        assertLabelAndSize(tree, "0", "Aaa", 0);
        assertLabelAndSize(tree, "1", "Bbb", 0);
        assertLabelAndSize(tree, "2", "Ccc", 0);
    }

    @Test
    public void testSimple() {
        List<CsvTreeElement> tree = RULE.csvTree("Aaa|Bbb(Bbb1|Bbb2)");
        Assert.assertEquals(2, tree.size());
        assertLabelAndSize(tree, "0", "Aaa", 0);
        assertLabelAndSize(tree, "1", "Bbb", 2);
        assertLabelAndSize(tree, "1|0", "Bbb1", 0);
        assertLabelAndSize(tree, "1|1", "Bbb2", 0);
    }

    @Test
    public void testSimple2() {
        List<CsvTreeElement> tree = RULE.csvTree("Aaa(Aaa1|Aaa2)|Bbb");
        Assert.assertEquals(2, tree.size());
        assertLabelAndSize(tree, "0", "Aaa", 2);
        assertLabelAndSize(tree, "0|0", "Aaa1", 0);
        assertLabelAndSize(tree, "0|1", "Aaa2", 0);
        assertLabelAndSize(tree, "1", "Bbb", 0);
    }

    @Test
    public void testComplex() {
        List<CsvTreeElement> tree = RULE.csvTree("Aaa(Aaa1(Aaa11)|Aaa2)|Bbb|Ccc(Ccc1|Ccc2|Ccc3)");
        Assert.assertEquals(3, tree.size());
        assertLabelAndSize(tree, "0", "Aaa", 2);
        assertLabelAndSize(tree, "0|0", "Aaa1", 1);
        assertLabelAndSize(tree, "0|0|0", "Aaa11", 0);
        assertLabelAndSize(tree, "0|1", "Aaa2", 0);
        assertLabelAndSize(tree, "1", "Bbb", 0);
        assertLabelAndSize(tree, "2", "Ccc", 3);
        assertLabelAndSize(tree, "2|0", "Ccc1", 0);
        assertLabelAndSize(tree, "2|1", "Ccc2", 0);
        assertLabelAndSize(tree, "2|2", "Ccc3", 0);
    }

    public void assertLabelAndSize(List<CsvTreeElement> tree, String indexes, String label, int size) {
        CsvTreeElement element = null;
        for (String index : indexes.split("\\|")) {
            element = tree.get(Integer.parseInt(index));
            tree = element.getSubelements();
        }
        Assert.assertEquals(label, element.getLabel());
        Assert.assertEquals(size, element.getSubelements().size());
    }
}
