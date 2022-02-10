package pl.fhframework.dp.commons.fh.outline;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import pl.fhframework.core.i18n.MessageService;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 14/08/2020
 */
@Configuration
@PropertySource("classpath:application.properties")
public class TestConfig {

    @Bean
    OutlineService outlineService() {
        return new OutlineService();
    }

    @Bean
    MessageService messageService() {
        return new MessageService();
    }
}
