package pl.fhframework.dp.commons.utils.image;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.fhframework.dp.commons.utils.conversion.MockObject;
import pl.fhframework.dp.commons.utils.conversion.NestedObject;
import pl.fhframework.dp.commons.utils.conversion.TestEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class TestConfig {

    @Bean
    ImageUploadService imageUploadService() {
        return new ImageUploadService();
    }

}
