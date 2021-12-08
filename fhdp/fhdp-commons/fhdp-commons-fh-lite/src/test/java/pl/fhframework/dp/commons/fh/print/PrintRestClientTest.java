package pl.fhframework.dp.commons.fh.print;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import pl.fhframework.dp.transport.prints.FormatType;
import pl.fhframework.dp.transport.prints.PrintRequestType;
import pl.fhframework.dp.transport.prints.PrintResponseType;


import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 16/10/2021
 */
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {TestConfig.class, PrintRestTemplateConfig.class})
@Slf4j
public class PrintRestClientTest {

//    @Autowired
//    PrintRestClient printRestClient;

    @Test
    public void fakeTest() {

    }

    //@Test
//    public void print() throws IOException {
//        PrintRequestType request = new PrintRequestType();
//        //TODO: implement
//        byte[] template = IOUtils.resourceToByteArray("/H6.jrxml");
//        byte[] source = IOUtils.resourceToByteArray("/H6.json");
//        byte[] bundle = IOUtils.resourceToByteArray("/H6_lt.properties");
//        request.setLocaleCountry("LT");
//        request.setLocaleLanguage("lt");
//        request.setTemplate(template);
//        request.setSource(source);
//        request.setSourceType("JSON");
//        request.setBundleDefault(bundle);
//        request.setOutputFormat(Arrays.asList(FormatType.PDF));
//        PrintResponseType result = printRestClient.print(request);
//        log.info("Print test result: {}", result.getResult().getCode());
//    }
}