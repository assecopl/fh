package pl.fhframework.dp.commons.utils.conversion;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@EnableConfigurationProperties
@Slf4j
public class PropertiesToXmlConverterTest {

  @Test
  public void fakeTest() {

  }

//  @Test
  public void test() throws IOException {
    Properties props = new Properties();
    try (InputStream input =
             new FileInputStream("/Users/Owner/asseco/icdts/icdts-lt/icdts-lt-portal/icdts-lt-portal-module/src/main/resources/ui_en.properties")) {
      // loads a properties file
      props.load(input);
    }

    try (OutputStream output =
             new FileOutputStream("/Users/Owner/asseco/icdts/icdts-lt/icdts-lt-portal/icdts-lt-portal-module/src/main/resources/ui1_en.xml")) {

      // convert the properties to an XML file
      props.storeToXML(output, null, String.valueOf(StandardCharsets.UTF_8));

    }
  }
}
