package pl.fhframework.compiler.binding;

import lombok.Data;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.compiler.core.generator.ExpressionJavaCodeGenerator;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.generator.GenerationContext;

/**
 * Test of java code generation for assignments.
 */
public class AssignmentCompilerTests {

    private static ExpressionContext expressionContext;

    @Data
    private static class One {

        private String propOne;
    }

    @Data
    private static class Two extends One {

        private String propTwo;
    }

    @BeforeClass
    public static void init() {
        expressionContext = new ExpressionContext();
        expressionContext.addTwoWayBindingRoot("one", "one", One.class);
        expressionContext.addTwoWayBindingRoot("two", "two", Two.class);
        expressionContext.addTwoWayBindingRoot("text", "text", String.class);
    }

    @Test
    public void testRootAssignment() {
        Assert.assertEquals("one = two", createAssignment("one", "two"));
        Assert.assertEquals("one = one", createAssignment("one", "one"));
        try {
            System.out.println(createAssignment("two", "one"));
            Assert.fail();
        } catch (FhBindingException e) {
            System.out.printf(e.getMessage());
        }
    }

    @Test
    public void testPropertyAssignment() {
        Assert.assertEquals("one.setPropOne(two.getPropTwo())", createAssignment("one.propOne", "two.propTwo"));
        Assert.assertEquals("one.setPropOne(two.getPropOne())", createAssignment("one.propOne", "two.propOne"));
        Assert.assertEquals("text = two.getPropOne()", createAssignment("text", "two.propOne"));
        Assert.assertEquals("one.setPropOne(text)", createAssignment("one.propOne", "text"));
    }

    private String createAssignment(String target, String source) {
        ExpressionJavaCodeGenerator generator = new ExpressionJavaCodeGenerator(new GenerationContext(), expressionContext);
        String result = generator.createAssignment(target, source, expressionContext);
        System.out.println(result);
        return result;
    }
}
