package pl.fhframework.app;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceFactory {
    public static final String SOURCE_NAME = "menuMessageSource";

    @Bean(name = SOURCE_NAME)
    public MessageSource translationsMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:defaultMenu");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
