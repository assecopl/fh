package pl.fhframework.dp.commons.services.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ServicesMessageSourceConfig {
    public static final String SOURCE_NAME = "servicesTranslationsMessageSource";
    @Bean(name = SOURCE_NAME)
    public MessageSource messageSource () {
        ReloadableResourceBundleMessageSource messageSource = new
                ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:jsr");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}