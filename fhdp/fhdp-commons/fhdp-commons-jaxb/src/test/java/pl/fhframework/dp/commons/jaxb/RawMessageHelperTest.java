package pl.fhframework.dp.commons.jaxb;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class RawMessageHelperTest {

    @Test
    public void extractXMLMessageTypeFromNamespace() {
        InputStream is = RawMessageHelperTest.class.getResourceAsStream("/xml/test.xml");
        String name = RawMessageHelper.extractXMLMessageTypeFromNamespace(is);
        assertEquals("unknown", name);
        InputStream is2 = RawMessageHelperTest.class.getResourceAsStream("/xml/test2.xml");
        name = RawMessageHelper.extractXMLMessageTypeFromNamespace(is2);
        assertEquals("testXML", name);
    }
}