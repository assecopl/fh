package pl.fhframework.dp.commons.jaxb;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/07/2021
 */
@Slf4j
public class XSLocalDateTimeAdapterTest {

    @Test
    public void unmarshal() {
        XSLocalDateTimeWithOffsetAdapter adapter = new XSLocalDateTimeWithOffsetAdapter();
        String [] dates = {"2021-07-01T14:43:24.712+03:00",
                            "2021-07-01T14:00:16.000Z",
                            "2021-07-12T10:07:27.812800",
                            "2021-07-12T10:07:27.813",
                            "2021-07-12T10:07:27",
                            "2021-07-12T10:07:27+03:00"};
//        String dateStr = "2021-07-01T14:43:24.712+03:00";
//        String dateStr = "2021-07-01T14:00:16.000Z";
//        String dateStr = "2021-07-12T10:07:27.812800";
//        String dateStr = "2021-07-12T10:07:27.813";
        for (String dateStr:dates) {
            LocalDateTime result = adapter.unmarshal(dateStr);
            log.info("Result: {}", result);
            String strResult = adapter.marshal(result);
            log.info("Result of marshall: {}", strResult);
        }
    }
}