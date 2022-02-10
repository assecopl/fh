package pl.fhframework.dp.commons.utils.conversion;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@EnableConfigurationProperties
@Slf4j
public class BeanClearUtilTest {

  @Autowired
  MockObject mockObject;

  @Test
  public void test() throws IllegalAccessException {
    BeanClearUtil.clearObject(mockObject);
    /**
     * The below objects are set as empty collections,
     * so should be cleared up
     */
    Assert.assertNull(mockObject.getEmptyNestedObject());
    Assert.assertNull(mockObject.getNestedObject().getItemEmptyArray());
    Assert.assertNull(mockObject.getNestedObject().getItemEmptyCollection());
    Assert.assertNull(mockObject.getNestedObject().getItemEmptyList());
    Assert.assertNull(mockObject.getNestedObject().getItemEmptyMap());

    /**
     * The below objects are set with content,
     * so shouldn't be cleared up
     */
    Assert.assertNotNull(mockObject.getNestedObject());
    Assert.assertNotNull(mockObject.getNestedObjects());
    Assert.assertNotNull(mockObject.getContent());
    Assert.assertNotNull(mockObject.getNestedObjects().get(0).getContent());
  }
}
