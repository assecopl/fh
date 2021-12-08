package pl.fhframework.dp.commons.utils.conversion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class TestConfig {

  @Bean
  MockObject mockObject() {
    MockObject mockObject = new MockObject();
    mockObject.setStringTest("test");
    mockObject.getNestedObjects().add(createNestedObject());
    mockObject.setNestedObject(createNestedObject());
    mockObject.setEmptyNestedObject(new NestedObject());
    byte[] bytes = new byte[100];
    Arrays.fill( bytes, (byte) 1);
    mockObject.setContent(bytes);
    return mockObject;
  }

  private NestedObject createNestedObject() {
    NestedObject nestedObject = new NestedObject();
    nestedObject.setStringTest("test");
    nestedObject.getItemMap().put("test", "test");
    nestedObject.getItemArray().add(1);
    nestedObject.getItemList().add("test");
    nestedObject.getItemCollection().add("test");

    byte[] bytes = new byte[100];
    Arrays.fill( bytes, (byte) 1);
    nestedObject.setContent(bytes);

    return nestedObject;
  }

}
