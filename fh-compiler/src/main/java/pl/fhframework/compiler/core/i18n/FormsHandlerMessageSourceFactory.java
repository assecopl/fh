package pl.fhframework.compiler.core.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Created by pawel.ruta on 2018-09-17.
 */
@Configuration
public class FormsHandlerMessageSourceFactory {
    public static final String SOURCE_NAME = "formsHandlerMessageSource";

    @Bean(name = SOURCE_NAME)
    public MessageSource translationsMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:formsHandler");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
