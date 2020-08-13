package pl.fhframework.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.PostConstruct;

/**
 * Basic class for all Converters which does auto-self register into Spring container.
 * @param <S> Type of from we convert.
 * @param <T> Type to we convert.
 */
public abstract class AutoRegisteredConverter<S, T> implements Converter<S, T> {

    protected FhConversionService conversionService;

    public FhConversionService getConversionService() {
        return conversionService;
    }

    @Autowired
    public void setConversionService(FhConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostConstruct
    private void register() {
        conversionService.addConverter(this);
    }
}
