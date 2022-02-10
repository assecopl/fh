package pl.fhframework.dp.commons.utils.xml;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 16/10/2020
 */
@Slf4j
public class TextUtilsTest {

    @Test
    public void camelCase() {
        String test = "ale w kOło JEST wesoło";
        String result = TextUtils.camelCase(test);
        log.info("Result: {}", result);
    }
}