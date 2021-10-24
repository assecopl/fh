package pl.fhframework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

@Configuration
@Slf4j
public class MvcConfig {

    @Value("${fh.web.cors.origins:}")
    private List<String> corsOrigins;
    @Value("${fh.web.cors.methods:}")
    private List<String> corsMethods;
    @Value("${fh.web.cors.headers:}")
    private List<String> corsHeaders;
    @Value("${fh.web.cors.allowCredentials:false}")
    private Boolean corsAllowCredentials;


    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        log.info("****************** Adding cors origins: {} *********************************", String.join(",", corsOrigins));
        if (!corsOrigins.isEmpty()) {
            configuration.setAllowedOrigins(corsOrigins);
            configuration.setAllowedOriginPatterns(corsOrigins);
        }
        if (!corsMethods.isEmpty()) {
            configuration.setAllowedMethods(corsMethods);
        }
        if (!corsHeaders.isEmpty()) {
            configuration.setAllowedHeaders(corsHeaders);
        }
        configuration.setAllowCredentials(corsAllowCredentials);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
