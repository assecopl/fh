package pl.fhframework.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.Expression;

/**
 * Tests of SpelUtils
 */
public class SpelUtilsTests {

    @Data
    @AllArgsConstructor
    private static class Child {
        private String name;
        private Parent parent;

        public String getChildClass() {
            return "Child";
        }
    }

    @Data
    @AllArgsConstructor
    private static class OtherChild {
        private String name;
        private Parent parent;

        public String getChildClass() {
            return "OtherChild";
        }
    }

    @Data
    @AllArgsConstructor
    private static class Parent {
        private String name;
    }

    @Test
    public void testNormal() {
        Child child = new Child("childName", new Parent("parentName"));
        Assert.assertEquals("childName", SpelUtils.evaluateExpression("name", child));
        Assert.assertEquals("parentName", SpelUtils.evaluateExpression("parent.name", child));
    }

    @Test
    public void testNullSafety() {
        Child child = new Child("childName", null);
        Assert.assertEquals(null, SpelUtils.evaluateExpression("parent", null));
        Assert.assertEquals(null, SpelUtils.evaluateExpression("parent.name", null));
        Assert.assertEquals(null, SpelUtils.evaluateExpression("parent", child));
        Assert.assertEquals(null, SpelUtils.evaluateExpression("parent.name", child));
    }

    @Test
    public void testCachedUsage() {
        Child child = new Child("childName1", new Parent("parentName1"));
        OtherChild otherChild = new OtherChild("childName2", new Parent("parentName2"));
        Expression expName = SpelUtils.parseExpression("parent.name");
        Expression expClass = SpelUtils.parseExpression("childClass");

        Assert.assertEquals(null, SpelUtils.evaluateExpression(expName, null));
        Assert.assertEquals("parentName1", SpelUtils.evaluateExpression(expName, child));
        Assert.assertEquals("parentName2", SpelUtils.evaluateExpression(expName, otherChild));
        Assert.assertEquals(null, SpelUtils.evaluateExpression(expClass, null));
        Assert.assertEquals("Child", SpelUtils.evaluateExpression(expClass, child));
        Assert.assertEquals("OtherChild", SpelUtils.evaluateExpression(expClass, otherChild));

        expName = SpelUtils.parseExpression("parent.name");
        expClass = SpelUtils.parseExpression("childClass");

        Assert.assertEquals("parentName1", SpelUtils.evaluateExpression(expName, child));
        Assert.assertEquals(null, SpelUtils.evaluateExpression(expName, null));
        Assert.assertEquals("parentName2", SpelUtils.evaluateExpression(expName, otherChild));
        Assert.assertEquals("Child", SpelUtils.evaluateExpression(expClass, child));
        Assert.assertEquals(null, SpelUtils.evaluateExpression(expClass, null));
        Assert.assertEquals("OtherChild", SpelUtils.evaluateExpression(expClass, otherChild));

    }
}
