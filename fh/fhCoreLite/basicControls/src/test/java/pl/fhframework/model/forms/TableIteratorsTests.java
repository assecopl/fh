package pl.fhframework.model.forms;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import pl.fhframework.core.FhBindingException;
import pl.fhframework.BindingResult;
import pl.fhframework.binding.AdHocIndexedModelBinding;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.table.LowLevelRowMetadata;
import pl.fhframework.model.forms.table.RowIteratorMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;

/**
 * Created by Piotr on 2017-01-23.
 */
public class TableIteratorsTests {

    @AllArgsConstructor
    public static class A {
        private String name;
        private List<B> colB;

        @Override
        public String toString() {
            return name;
        }
    }

    @AllArgsConstructor
    public static class B {
        private String name;
        private List<C> colC;

        @Override
        public String toString() {
            return name;
        }
    }

    @AllArgsConstructor
    public static class C {
        private String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private List<A> lists = new ArrayList<>();

    private Form form;

    // to run this test comment bindingMethods = new Binding() in Form
    @Before
    public void prepare() {
        AutowireHelper.disable();
        lists.clear();

        form = new Form() {
            @Override
            public BindingResult getBindingResult(String binding, Component component) {
                if (binding.startsWith("{") && binding.endsWith("}")) {
                    binding = binding.substring(1, binding.length() - 1);
                }
                switch(binding) {
                    case "c1":
                        return new BindingResult(null, null, lists);
                    case "c1[0].colB":
                        return new BindingResult(null, null, lists.get(0).colB);
                    case "c1[1].colB":
                        return new BindingResult(null, null, lists.get(1).colB);
                    case "c1[2].colB":
                        return new BindingResult(null, null, lists.get(2).colB);
                    case "c1[3].colB":
                        return new BindingResult(null, null, lists.get(3).colB);
                    case "c1[0].colB[0].colC":
                        return new BindingResult(null, null, lists.get(0).colB.get(0).colC);
                    case "c1[0].colB[1].colC":
                        return new BindingResult(null, null, lists.get(0).colB.get(1).colC);
                    case "c1[1].colB[0].colC":
                        return new BindingResult(null, null, lists.get(1).colB.get(0).colC);
                    case "c1[1].colB[1].colC":
                        return new BindingResult(null, null, lists.get(1).colB.get(1).colC);
                    case "c1[2].colB[0].colC":
                        return new BindingResult(null, null, lists.get(2).colB.get(0).colC);
                    case "c1[2].colB[1].colC":
                        return new BindingResult(null, null, lists.get(2).colB.get(1).colC);
                    default:
                        throw new FhBindingException("Bad binding: " + binding);
                }
            }
        };
    }

    @Test
    public void testExampleData() {
        lists.add(new A("A1", Arrays.asList(
                        new B("A1B1",
                            Arrays.asList(
                                    new C("aaa1"),
                                    new C("bbb1"),
                                    new C("ccc1")
                            )
                        ),
                        new B("A1B2",
                                Arrays.asList(
                                    new C("xxx1"),
                                    new C("yyy1")
                            )
                        )
                )));
        lists.add(new A("A2", Arrays.asList(
                        new B("A2B1",
                                Arrays.asList(
                                        new C("aaa2"),
                                        new C("bbb2"),
                                        new C("ccc2")
                                )
                        ),
                        new B("A2B2",
                                Arrays.asList(
                                        new C("xxx2"),
                                        new C("yyy2")
                                )
                        )
                )));
        lists.add(new A("A3", Arrays.asList(
                        new B("A3B1",
                                Arrays.asList()
                        ),
                        new B("A3B2",
                                Arrays.asList(
                                        new C("xxx3"),
                                        new C("yyy3")
                                )
                        )
                )));
        lists.add(new A("A4", Arrays.asList()));

        Table table = new Table(form);
        table.setIterator("iter1");
        table.setCollection(new AdHocModelBinding(null, null, "{c1}") {
            @Override
            public BindingResult getBindingResult() {
                return form.getBindingResult(getBindingExpression(), table);
            }
        });

        table.getIterators().add(createIterator("iter2", "{iter1.colB}", form, table));
        table.getIterators().add(createIterator("iter3", "{iter2.colC}", form, table));

        List<LowLevelRowMetadata> rows = new ArrayList<>();
        table.init();
        table.createIteratorStructureRows(rows,
                table.getAllIterators().subList(1, table.getAllIterators().size()),
                new LinkedHashMap<>(),
                new int[0],
                table.getAllIterators().get(0));

        Assert.assertEquals(14, rows.size());

        Assert.assertEquals(5, rows.get(0).getIteratorData().get("iter1").getRowSpan().get());
        Assert.assertTrue(rows.get(0).getIteratorData().get("iter1").isFirstOccurrence());
        Assert.assertEquals(3, rows.get(0).getIteratorData().get("iter2").getRowSpan().get());
        Assert.assertTrue(rows.get(0).getIteratorData().get("iter2").isFirstOccurrence());
        Assert.assertEquals(1, rows.get(0).getIteratorData().get("iter3").getRowSpan().get());
        Assert.assertTrue(rows.get(0).getIteratorData().get("iter3").isFirstOccurrence());

        Assert.assertEquals(5, rows.get(3).getIteratorData().get("iter1").getRowSpan().get());
        Assert.assertFalse(rows.get(3).getIteratorData().get("iter1").isFirstOccurrence());
        Assert.assertEquals(2, rows.get(3).getIteratorData().get("iter2").getRowSpan().get());
        Assert.assertTrue(rows.get(3).getIteratorData().get("iter2").isFirstOccurrence());
        Assert.assertEquals(1, rows.get(3).getIteratorData().get("iter3").getRowSpan().get());
        Assert.assertTrue(rows.get(3).getIteratorData().get("iter3").isFirstOccurrence());

        System.out.printf("\n\nResult:\n");
        for (LowLevelRowMetadata row : rows) {
            System.out.println(row.toString() + "\n");
        }
    }

    @Test
    public void testEmptyCollection() {
        // lists are empty

        Table table = new Table(form);
        table.setIterator("iter1");
        table.setCollection(new AdHocModelBinding(null, null, "{c1}") {
            @Override
            public BindingResult getBindingResult() {
                return form.getBindingResult(getBindingExpression(), table);
            }
        });

        table.getIterators().add(createIterator("iter2", "{iter1.colB}", form, table));
        table.getIterators().add(createIterator("iter3", "{iter2.colC}", form, table));

        List<LowLevelRowMetadata> rows = new ArrayList<>();
        table.init();
        table.createIteratorStructureRows(rows,
                table.getAllIterators().subList(1, table.getAllIterators().size()),
                new LinkedHashMap<>(),
                new int[0],
                table.getAllIterators().get(0));

        Assert.assertEquals(0, rows.size());
    }

    @Test
    public void testRowStructureEquals() {
        RowIteratorMetadata row1Iter1 = new RowIteratorMetadata(0, new Object(), new AtomicInteger(1), true);
        RowIteratorMetadata row1Iter2 = new RowIteratorMetadata(2, new Object(), new AtomicInteger(2), false);

        LowLevelRowMetadata row1 = new LowLevelRowMetadata();
        row1.getIteratorData().put("iter", row1Iter1);
        row1.getIteratorData().put("iterInner", row1Iter2);

        LowLevelRowMetadata row2 = new LowLevelRowMetadata();
        RowIteratorMetadata row2Iter1 = new RowIteratorMetadata(0, new Object(), new AtomicInteger(1), true);
        RowIteratorMetadata row2Iter2 = new RowIteratorMetadata(2, new Object(), new AtomicInteger(2), false);
        row2.getIteratorData().put("iter", row2Iter1);
        row2.getIteratorData().put("iterInner", row2Iter2);

        // look the same
        Assert.assertTrue(row1.equals(row2));

        row2Iter1.setFirstOccurrence(false);
        Assert.assertFalse(row1.equals(row2));

        row2.getIteratorData().remove("iter");
        Assert.assertFalse(row1.equals(row2));

        row1.getIteratorData().remove("iter");
        Assert.assertTrue(row1.equals(row2));

        List<LowLevelRowMetadata> rows1 = new ArrayList<>();
        rows1.add(row1);
        rows1.add(row1);
        List<LowLevelRowMetadata> rows2 = new ArrayList<>();
        rows2.add(row2);
        rows2.add(row2);
        Assert.assertTrue(rows1.equals(rows2));

        rows2.add(row2);
        Assert.assertFalse(rows1.equals(rows2));
    }

    private Iterator createIterator(String name, String bindingExpression, Form form, Table table) {
        Iterator iterator = new Iterator(form);
        iterator.setTable(table);
        iterator.setId(name);
        iterator.setCollection(new AdHocIndexedModelBinding<>(bindingExpression, iterator, form));
        return iterator;
    }

}
