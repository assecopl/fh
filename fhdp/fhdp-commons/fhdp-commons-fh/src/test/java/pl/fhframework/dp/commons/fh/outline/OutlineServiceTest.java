package pl.fhframework.dp.commons.fh.outline;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import javax.xml.bind.JAXBException;

import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 30/09/2021
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Slf4j
public class OutlineServiceTest {

    @Autowired
    OutlineService outlineService;

    @Test
    public void generateOutline() throws JAXBException {
        List<ElementCT> result = outlineService.generateOutline("TEST");
        log.info("Returned {} items", result.size());
        String pointer = "document.customers";
        ElementCT el = outlineService.findElementFromPointer(pointer);
        if(el != null) {
            log.info("Found {}", el.getId());
        } else {
            log.info("Not found!");
        }
    }


}