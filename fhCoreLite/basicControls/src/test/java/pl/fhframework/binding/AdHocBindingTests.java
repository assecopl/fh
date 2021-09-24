package pl.fhframework.binding;

import org.junit.Assert;
import org.junit.Test;
import pl.fhframework.core.FhException;

import java.util.Arrays;

/**
 * Created by Piotr on 2017-04-04.
 */
public class AdHocBindingTests {

    private static final String LONG_ESCAPED_BINDING = "<TablePaged id=\"tablePage_ColoredCompanyEmployeesEditableNested\"\n" +
            "        iterator=\"person\" collection=\"\\{pagedPeople\\}\" onRowClick=\"-\"\n" +
            "        selected=\"\\{selectedPersonPage\\}\" onPageChange=\"onPageChangeUpdate(this)\" onSortChange=\"onPageChangeUpdate(this)\"\n" +
            "        onPageSizeChange=\"onPageChangeUpdate(this)\" rowStylesMap=\"\\{coloredPagedPeople\\}\"\n" +
            "        pageSize=\"5\">\n" +
            "<ColumnPaged label=\"No.\" value=\"\\{person$rowNo\\}\" />\n" +
            "<ColumnPaged label=\"\\{companyEmployeesEditableTablePersonalData\\}\">\n" +
            "    <ColumnPaged label=\"\\{companyEmployeesEditableTableNameAndSurname\\}\">\n" +
            "        <ColumnPaged label=\"\\{companyEmployeesEditableTableName\\}\" value=\"\\{person.name\\}\" sortBy=\"PersonName\"/>\n" +
            "        <ColumnPaged label=\"\\{companyEmployeesEditableTableSurname\\}\" value=\"\\{person.surname\\}\" sortBy=\"PersonSurname\"/>\n" +
            "    </ColumnPaged>\n" +
            "    <ColumnPaged label=\"\\{companyEmployeesEditableTableCountryAndGender\\}\">\n" +
            "        <ColumnPaged label=\"\\{companyEmployeesEditableTableCountry\\}\">\n" +
            "\n" +
            "            <ColumnPaged label=\"\\{companyEmployeesEditableTableCity\\}\" value=\"\\{person.city\\}\" sortBy=\"PersonCity\"/>\n" +
            "            <ColumnPaged label=\"\\{companyEmployeesEditableTableCitizenship\\}\" value=\"\\{person.citizenship\\}\" sortBy=\"PersonCitizenship\\}/>\n" +
            "        </ColumnPaged>\n" +
            "        <ColumnPaged label=\"\\{companyEmployeesEditableTableGender\\}\" value=\"\\{person.gender\\}\" sortBy=\"PersonGender\"/>\n" +
            "    </ColumnPaged>\n" +
            "</ColumnPaged>\n" +
            "<ColumnPaged label=\"\\{companyEmployeesEditableTableStatus\\}\" value=\"\\{person.status\\}\" sortBy=\"PersonStatus\"/>\n" +
            "<ColumnPaged label=\"\\{companyEmployeesEditableTableName\\} + \\{companyEmployeesEditableTableSurname\\}\">\n" +
            "    <OutputLabel value=\"\\{person.name\\}\"></OutputLabel>\n" +
            "    <OutputLabel value=\"\\{person.surname\\}\"></OutputLabel>\n" +
            "</ColumnPaged>\n" +
            "</TablePaged>";

    private static final String PARTIALLY_ESCAPED_BINDING = "Test {my} and \\{escaped\\} what?";

    private static final String FULLY_ESCAPED_BINDING = "Test \\{my\\} and \\{escaped\\} what?";

    @Test
    public void escapedBindingTest() {
        AdHocModelBinding escapedBinding = new AdHocModelBinding<>(null, null, LONG_ESCAPED_BINDING);
        Assert.assertEquals(LONG_ESCAPED_BINDING, escapedBinding.getBindingExpression());
        Assert.assertFalse(escapedBinding.isCombined());
        Assert.assertTrue(escapedBinding.isStaticValue());
    }

    @Test
    public void escapedBindingTest2() {
        AdHocModelBinding<?> escapedBinding = new AdHocModelBinding<>(null, null, PARTIALLY_ESCAPED_BINDING);
        Assert.assertEquals(PARTIALLY_ESCAPED_BINDING, escapedBinding.getBindingExpression());
        Assert.assertTrue(escapedBinding.isCombined());
        Assert.assertFalse(escapedBinding.isStaticValue());

        Assert.assertEquals(3, escapedBinding.getCombinedExpressions().size());

        Assert.assertEquals("Test ", escapedBinding.getCombinedExpressions().get(0).getValue());
        Assert.assertFalse(escapedBinding.getCombinedExpressions().get(0).isBinding());

        Assert.assertEquals("my", escapedBinding.getCombinedExpressions().get(1).getValue());
        Assert.assertTrue(escapedBinding.getCombinedExpressions().get(1).isBinding());

        Assert.assertEquals(" and {escaped} what?", escapedBinding.getCombinedExpressions().get(2).getValue());
        Assert.assertFalse(escapedBinding.getCombinedExpressions().get(2).isBinding());
    }

    @Test
    public void escapedBindingTest3() {
        AdHocModelBinding<?> escapedBinding = new AdHocModelBinding<>(null, null, FULLY_ESCAPED_BINDING);
        Assert.assertEquals(FULLY_ESCAPED_BINDING, escapedBinding.getBindingExpression());
        Assert.assertFalse(escapedBinding.isCombined());
        Assert.assertTrue(escapedBinding.isStaticValue());

        Assert.assertEquals("Test {my} and {escaped} what?", escapedBinding.getStaticValueText());
    }

    @Test
    public void testActionBinding() {
        assertActionBinding("-", "-");
        assertActionBinding("+", "+");

        assertActionBinding("simple", "simple");
        assertActionBinding("simple()", "simple");
        assertActionBinding("simple( )", "simple");
        assertActionBinding("simple(arg1)", "simple", "arg1");
        assertActionBinding("simple(arg1 - arg1)", "simple", "arg1-arg1");
        assertActionBinding("simple(arg1 - arg1, arg2)", "simple", "arg1-arg1", "arg2");
        assertActionBinding("simple(arg1(arg1a, arg1b), arg2)", "simple", "arg1(arg1a,arg1b)", "arg2");
        assertActionBinding("simple(arg1.arg1a.arg1b, arg2)", "simple", "arg1.arg1a.arg1b", "arg2");

        assertActionBinding("simple(arg1[0].arg2[2])", "simple", "arg1[0].arg2[2]");


        assertActionBindingError("simple(), error");
        assertActionBindingError("e r r o r()");
        assertActionBindingError("simple() - error");
    }

    private void assertActionBinding(String fullExpression, String actionName, String... arguments) {
        ActionBinding actionBinding = new AdHocActionBinding(fullExpression, null, null);
        Assert.assertEquals(fullExpression, actionBinding.getActionBindingExpression());
        Assert.assertEquals(actionName, actionBinding.getActionName());
        Assert.assertEquals(arguments, Arrays.asList(actionBinding.getArguments()).stream().map(a -> a.getBindingExpression().replace(" ", "")).toArray());
    }

    private void assertActionBindingError(String fullExpression) {
        try {
            new AdHocActionBinding(fullExpression, null, null).getActionName();
            Assert.fail();
        } catch (FhException expected) {
        }
    }
}
