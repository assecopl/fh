package pl.fhframework.dp.commons.fh.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class CommonsMessageSourceConfig {
    public static final String SOURCE_NAME = "commonsTranslationsMessageSource";
    @Bean(name = SOURCE_NAME)
    public MessageSource messageSource () {
        ReloadableResourceBundleMessageSource messageSource = new
                ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:fhdp", "classpath:ui", "classpath:translations", "classpath:fhdp-enums" , "classpath:enums");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}