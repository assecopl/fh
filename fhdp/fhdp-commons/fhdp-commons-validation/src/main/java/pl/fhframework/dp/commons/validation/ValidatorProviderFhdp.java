package pl.fhframework.dp.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import pl.fhframework.dp.commons.utils.lng.LngDescription;
import pl.fhframework.dp.commons.utils.lng.LngDescriptionHolder;

import javax.validation.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ValidatorProviderFhdp {

    @Autowired
    private ValidatorFactory fhdpValidatorFactoryBean;
    @Autowired
    private MessageSource validationTranslationsMessageSource;

    public Validator getValidator(Locale locale) {
        Locale currentLocale = locale;
        log.debug("Selected locale for validator:{}", currentLocale);

        return fhdpValidatorFactoryBean.usingContext()
                .messageInterpolator(new MessageInterpolatorFhdp(locale, new MessageSourceResourceBundleLocator(validationTranslationsMessageSource)))
                .getValidator();
    }

    /**
     * Provides multi-language JSR validation result set for object.
     * The reason is, that evaluation of JSR validation message keys is impossible without validation context.
     * Key in result map contains pointer, value, LngDescriptionHolder with descriptions. It can be translated to Json
     * by LngDescriptionUtil.toJson() method.
     *
     * @param object validated object
     * @param languages list of languages for descriptions
     * @return
     */
    public Map<String, LngDescriptionHolder> validateJsr(Object object, List<String> languages) {
        Map<String, LngDescriptionHolder> ret = new ConcurrentHashMap<>();
        for(String lng: languages) {
            Set<ConstraintViolation<Object>> resultSet = getValidator(new Locale(lng))
                    .validate(object);
            registerDescriptions(resultSet, lng, ret);
        }
        return ret;
    }

    private void registerDescriptions(Set<ConstraintViolation<Object>> resultSet, String lng, Map<String, LngDescriptionHolder> errors) {
        for(ConstraintViolation<Object> v: resultSet ) {
            LngDescriptionHolder holder = errors.get(v.getPropertyPath().toString());
            if(holder == null) holder = new LngDescriptionHolder();
            LngDescription description = new LngDescription();
            description.setLng(lng);
            description.setText(v.getMessage());
            holder.getDescriptions().add(description);
            errors.put(v.getPropertyPath().toString(), holder);
        }
    }
}
