package pl.fhframework.dp.commons.fh.adm.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author Tomasz Kozlowski (created on 23.02.2021)
 */
@Configuration
public class AdmMessageSourceConfig {

    public static final String SOURCE_NAME = "admTranslationsMessageSource";

    @Bean(name = SOURCE_NAME)
    public MessageSource messageSource () {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:adm_translations");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheMillis(1000);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

}
