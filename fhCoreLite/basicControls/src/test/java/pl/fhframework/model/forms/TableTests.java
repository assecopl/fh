package pl.fhframework.model.forms;

import junit.framework.Assert;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Piotr on 2017-01-24.
 */
public class TableTests {

    private static Table table = new Table(null);

    private static Map<String, String> replacements = new LinkedHashMap<>();
    static {
        replacements.put("iterator", "collection[1]");
        replacements.put("iterator2", "collection[1].other[2]");
    }

    @Test
    public void testGetRowBindingProperties() {
        Assert.assertEquals("{collection[1]}", table.getRowBinding("{iterator}", replacements, true));
        Assert.assertEquals("{collection[1].prop}", table.getRowBinding("{iterator.prop}", replacements, true));

        Assert.assertEquals("{iterator3}", table.getRowBinding("{iterator3}", replacements, true));
        Assert.assertEquals("{iterator3.prop}", table.getRowBinding("{iterator3.prop}", replacements, true));

        Assert.assertEquals("{aniterator}", table.getRowBinding("{aniterator}", replacements, true));
        Assert.assertEquals("{aniterator.prop}", table.getRowBinding("{aniterator.prop}", replacements, true));
    }

    @Test
    public void testGetRowBindingMethodCalls() {
        Assert.assertEquals("{method(collection[1])}", table.getRowBinding("{method(iterator)}", replacements, true));
        Assert.assertEquals("{method(collection[1].prop)}", table.getRowBinding("{method(iterator.prop)}", replacements, true));
        Assert.assertEquals("{method(collection[1]).prop}", table.getRowBinding("{method(iterator).prop}", replacements, true));

        Assert.assertEquals("{method(collection[1], collection[1].other[2])}", table.getRowBinding("{method(iterator, iterator2)}", replacements, true));

        Assert.assertEquals("{method(iterator3)}", table.getRowBinding("{method(iterator3)}", replacements, true));

        Assert.assertEquals("{method(aniterator)}", table.getRowBinding("{method(aniterator)}", replacements, true));
    }

    @Test
    public void testGetRowBindingCollections() {
        Assert.assertEquals("{collection[1][5]}", table.getRowBinding("{iterator[5]}", replacements, true));
        Assert.assertEquals("{method(collection[1][5])}", table.getRowBinding("{method(iterator[5])}", replacements, true));
        Assert.assertEquals("{collection[1][5].prop}", table.getRowBinding("{iterator[5].prop}", replacements, true));
    }
}
