package pl.fhframework.dp.commons.fh.print;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 16/10/2021
 */
//@Configuration
//@PropertySource("classpath:application.properties")
public class TestConfig {

    @Bean
    PrintRestClient printRestClient() {
        return new PrintRestClient();
    }
}
