package pl.fhframework.dp.commons.utils.date;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonGeneratorImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 13/05/2022
 */
@Slf4j
public class LocalDateTimeSerializerTest {

    @Before
    public void setUp() {
    }

    @Test
    public void serialize() {
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer();
        LocalDateTime ldt = LocalDateTime.of(2022, 01, 01, 12, 12, 00);
        String ret = serializer.convert(ldt);
        log.info("Ret: {}", ret);
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer();
        LocalDateTime date = deserializer.convert(ret);
        log.info("Date: {}", date);
    }

    @Test
    public void testDuration() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusNanos(250000000);
        long diff = ChronoUnit.MILLIS.between(startTime, endTime);
        float duration = (float) diff / 1000;
        log.info("Diff: {}", diff);
    }
}