package pl.fhframework.dp.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;

@Service
@Slf4j
public class ValidatorProviderFhdp {
    private static ValidatorFactory validatorFactory;

    public Validator getValidator(Locale locale) {
//        Locale springContextLocale = LocaleContextHolder.getLocale();
//        Locale defaultLocale = new Locale("en");
//        Locale currentLocale = ObjectUtils.firstNonNull(locale, springContextLocale, defaultLocale);
        Locale currentLocale = locale;
//        log.info("UserSessionLocale:{}, springContextLocale:{}, defaultLocale:{}", locale, springContextLocale, defaultLocale);
        log.info("Wybrano locale dla walidatora:{}", currentLocale);
        if (validatorFactory == null) {
            log.info("Initiation of validatorFactory");
            validatorFactory = Validation.byDefaultProvider()
                    .configure()
                    .messageInterpolator(new ParameterMessageInterpolator())
                    .buildValidatorFactory();
        }
        return validatorFactory.usingContext()
                .messageInterpolator(new ParameterMessageInterpolatorFhdp(currentLocale))
                .getValidator();
    }
}
