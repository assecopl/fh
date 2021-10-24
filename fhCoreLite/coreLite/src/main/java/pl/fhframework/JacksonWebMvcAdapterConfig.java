package pl.fhframework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.AutoRegisteredFormatter;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-04-18.
 */
@Component
public class JacksonWebMvcAdapterConfig implements WebMvcConfigurer {
    @Autowired
    private List<AutoRegisteredConverter> converters;

    @Autowired
    private List<AutoRegisteredFormatter> formatters;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        formatters.forEach(registry::addFormatter);
        converters.forEach(registry::addConverter);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
    }
}
