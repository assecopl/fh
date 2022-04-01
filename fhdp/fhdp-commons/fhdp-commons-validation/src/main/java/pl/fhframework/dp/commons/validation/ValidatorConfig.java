package pl.fhframework.dp.commons.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 29/03/2022
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public MessageSource validationTranslationsMessageSource () {
        ReloadableResourceBundleMessageSource messageSource = new
                ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:jsr");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

//    @Bean
//    public LocalValidatorFactoryBean fhdpValidatorFactoryBean() {
//        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
//        factory.setValidationMessageSource(validationTranslationsMessageSource());
//        return factory;
//    }
}
