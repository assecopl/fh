package pl.fhframework.dp.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.fhframework.dp.commons.utils.lng.LngDescriptionHolder;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 28/03/2022
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ValidatorConfig.class, TestConfig.class})
@Slf4j
public class ValidatorProviderFhdpTest {
    @Autowired
    ValidatorProviderFhdp validatorProviderFhdp;

    @Test
    public void testValidateJSR() {
        TestDto dto = new TestDto();
        dto.setTestRegexp("");
        dto.setNetMass(BigDecimal.ZERO);
        List<String> languages = Arrays.asList("lt", "en");
        Map<String, LngDescriptionHolder> result2 = validatorProviderFhdp.validateJsr(dto, languages);
        log.info("Found {} errors", result2.size());
    }

}