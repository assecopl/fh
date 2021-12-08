package pl.fhframework.dp.commons.fh.operations.msg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeClientFactory;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeRestClient;
import pl.fhframework.event.EventRegistry;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 14/08/2020
 */
//@Configuration
//@PropertySource("classpath:application.properties")
public class TestConfig {

    @Bean
    GetMessageOperationHandler getMessageOperationHandler() {
        return new GetMessageOperationHandler();
    }

    @Bean
    FacadeClientFactory getFacadeClientFactory() {
        return new FacadeClientFactory();
    }

    @Bean
    FacadeRestClient getFacadeRestClient() {
        return new FacadeRestClient();
    }

    @Bean
    EventRegistry getEventRegistry() {
        return new EventRegistry();
    }
}
