package pl.fhframework.dp.commons.utils.date;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;


public final class LocalDatesModule extends SimpleModule {
    public final static Version VERSION = VersionUtil.parseVersion(
            "1.0", "pl.fhframework.dp", "jackson-datatype-jsr310"
    );
    private static final long serialVersionUID = 1L;

    public LocalDatesModule() {


        super(VERSION);

        // first deserializers
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
//        addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        // then serializers:
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
//        addSerializer(LocalTime.class, new LocalTimeSerializer());
    }
}
