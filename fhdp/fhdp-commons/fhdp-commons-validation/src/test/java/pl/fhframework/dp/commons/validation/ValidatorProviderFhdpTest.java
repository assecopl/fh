package pl.fhframework.dp.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

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
        Locale locale = new Locale("en");
        Set<ConstraintViolation<Object>> result = validatorProviderFhdp.getValidator(locale).validate(dto, new Class[0]);
        log.info("Found {} errors", result.size());
        Locale locale2 = new Locale("lt");
        Set<ConstraintViolation<Object>> result2 = validatorProviderFhdp.getValidator(locale2).validate(dto, new Class[0]);
        log.info("Found {} errors", result2.size());
    }

}