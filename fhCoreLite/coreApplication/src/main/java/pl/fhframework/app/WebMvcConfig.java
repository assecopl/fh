package pl.fhframework.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

@Configuration
@Slf4j
public class WebMvcConfig extends DelegatingWebMvcConfiguration {
    @Value("${fh.web.cors.origins:}")
    private List<String> corsOrigins;
    @Value("${fh.web.cors.methods:}")
    private List<String> corsMethods;
    @Value("${fh.web.cors.headers:}")
    private List<String> corsHeaders;
    @Value("${fh.web.cors.allowCredentials:false}")
    private Boolean corsAllowCredentials;

    @Autowired
    LocaleChangeInterceptor yourInjectedInterceptor;

    @Value("${fhframework.language.default:pl}")
    private String defaultLang;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(yourInjectedInterceptor);
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


//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.clear();
//        converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        for(HttpMessageConverter converter: converters) {
//            log.info("************** Added converter {}", converter.getClass().getName());
//        }
//    }

    @Bean
    @Override
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("USERLANG");
        resolver.setDefaultLocale(Locale.forLanguageTag(defaultLang));
        return resolver;
    }

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/"};

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
}