package pl.fhframework.dp.commons.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 28/03/2022
 */
@Configuration
public class TestConfig {
    @Bean
    ValidatorProviderFhdp validatorProviderFhdp() {
        return new ValidatorProviderFhdp();
    };
}
