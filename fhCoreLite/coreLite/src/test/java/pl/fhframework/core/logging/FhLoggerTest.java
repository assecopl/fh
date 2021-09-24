package pl.fhframework.core.logging;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Piotr on 2017-01-11.
 */
public class FhLoggerTest {

    private class FhLoggerTestInner {

        public void test() {
            Assert.assertTrue(FhLogger.getCallerClassName(FhLogger.class).startsWith(FhLoggerTest.class.getName()));
            FhLogger.info(this.getClass(), "testCallerClassNameInnerClass");
        }
    }

    @Test
    public void testCallerClassNameSimple() {
        Assert.assertEquals(FhLoggerTest.class.getName(), FhLogger.getCallerClassName(FhLogger.class));
        FhLogger.info(this.getClass(), "testCallerClassNameSimple");
    }

    @Test
    public void testCallerClassNameInnerClass() {
        new FhLoggerTestInner().test();
    }
}
