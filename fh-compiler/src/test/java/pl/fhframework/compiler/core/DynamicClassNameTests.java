package pl.fhframework.compiler.core;

import org.junit.Assert;
import org.junit.Test;
import pl.fhframework.core.dynamic.DynamicClassName;

/**
 * Created by Piotr on 2017-10-19.
 */
public class DynamicClassNameTests {

    @Test
    public void testRemoveVersion() {
        Assert.assertEquals("pl.fhframework.demo.TestClass", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass"));
        Assert.assertEquals("pl.fhframework.demo.TestClass", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_V1"));
        Assert.assertEquals("pl.fhframework.demo.TestClass", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_V123"));
        Assert.assertEquals("pl.fhframework.demo.TestClass", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_Precompiled"));
        Assert.assertEquals("pl.fhframework.demo.TestClass$InternalModel", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass$InternalModel"));
        Assert.assertEquals("pl.fhframework.demo.TestClass$InternalModel", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_V1$InternalModel"));
        Assert.assertEquals("pl.fhframework.demo.TestClass$InternalModel", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_V123$InternalModel"));
        Assert.assertEquals("pl.fhframework.demo.TestClass$InternalModel", DynamicClassName.removeVersion("pl.fhframework.demo.TestClass_Precompiled$InternalModel"));
        Assert.assertEquals("TestClass", DynamicClassName.removeVersion("TestClass"));
        Assert.assertEquals("TestClass", DynamicClassName.removeVersion("TestClass_V1"));
        Assert.assertEquals("TestClass", DynamicClassName.removeVersion("TestClass_V123"));
        Assert.assertEquals("TestClass", DynamicClassName.removeVersion("TestClass_Precompiled"));
        Assert.assertEquals("TestClass$InternalModel", DynamicClassName.removeVersion("TestClass$InternalModel"));
        Assert.assertEquals("TestClass$InternalModel", DynamicClassName.removeVersion("TestClass_V1$InternalModel"));
        Assert.assertEquals("TestClass$InternalModel", DynamicClassName.removeVersion("TestClass_V123$InternalModel"));
        Assert.assertEquals("TestClass$InternalModel", DynamicClassName.removeVersion("TestClass_Precompiled$InternalModel"));    }
}
