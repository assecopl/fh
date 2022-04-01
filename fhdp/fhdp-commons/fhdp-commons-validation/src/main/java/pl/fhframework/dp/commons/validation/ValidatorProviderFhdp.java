package pl.fhframework.dp.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;

@Service
@Slf4j
public class ValidatorProviderFhdp {

    private static ValidatorFactory validatorFactory;
    @Autowired
    private MessageSource validationTranslationsMessageSource;

    public Validator getValidator(Locale locale) {
        Locale currentLocale = locale;
        log.debug("Selected locale for validator:{}", currentLocale);
        if(validatorFactory == null) {
            validatorFactory = Validation.byDefaultProvider()
                    .configure()
                    .messageInterpolator(new ParameterMessageInterpolator())
                    .buildValidatorFactory();
        }
        return validatorFactory
                .usingContext()
                .messageInterpolator(new MessageInterpolatorFhdp(locale, new MessageSourceResourceBundleLocator(validationTranslationsMessageSource)))
                .getValidator();
    }
}
