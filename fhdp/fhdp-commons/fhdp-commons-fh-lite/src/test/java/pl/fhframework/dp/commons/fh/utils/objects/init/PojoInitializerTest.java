package pl.fhframework.dp.commons.fh.utils.objects.init;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class PojoInitializerTest {

    @Test
    public void testInitializePojo() {
        PojoInitializerConfig config = new PojoInitializerConfig();
        TestPojo testPojo = new TestPojo();
        PojoInitializer.initializePojo(testPojo, config);
        log.info("Test pojo initialized");
    }

    @Data
    public static class TestPojo {
        String name;
        byte[] bytes;
        InnerPojo innerPojo;
    }

    @Data
    public static class InnerPojo {
        String xxx;
    }


}