package pl.fhframework.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
@Slf4j
@Order(0)
public class WebConfig extends DelegatingWebMvcConfiguration {

    @Value("${fhframework.language.default:pl}")
    private String defaultLang;

    @Bean
    @Override
    @Primary
    public LocaleResolver localeResolver() {
        log.info("************* Start cookie locale resolver...");
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("USERLANG");
        resolver.setDefaultLocale(Locale.forLanguageTag(defaultLang));
        return resolver;
    }
}
