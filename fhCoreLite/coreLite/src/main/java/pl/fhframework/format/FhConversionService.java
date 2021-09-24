package pl.fhframework.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.GenericTypeResolver;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.stereotype.Service;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.text.ParseException;

@Service
public class FhConversionService extends DefaultFormattingConversionService {

    @Autowired
    private ApplicationContext appContext;


    @PostConstruct
    void init() {
        DateFormatter formatter = new DateFormatter();
        formatter.setPattern("yyyy-MM-dd HH:mm:ss"); // Date is with time, LocalDate is without time
        addFormatter(formatter);
    }

    public boolean canConvertObjectToString(Class<?> sourceType, String converterName) {
        Printer printer = appContext.getBean(converterName, Printer.class);
        Class fieldType = GenericTypeResolver.resolveTypeArgument(printer.getClass(), Printer.class);
        if (fieldType == null && printer instanceof DecoratingProxy) {
            fieldType = GenericTypeResolver.resolveTypeArgument(((DecoratingProxy) printer).getDecoratedClass(), Printer.class);
        }
        if (fieldType == null || !fieldType.isAssignableFrom(sourceType)) {
            return false;
        }
        return true;
    }

    public boolean canConvertToObject(Class<?> sourceType, String converterName) {
        Parser parser = appContext.getBean(converterName, Parser.class);
        Class fieldType = GenericTypeResolver.resolveTypeArgument(parser.getClass(), Parser.class);
        if (fieldType == null && parser instanceof DecoratingProxy) {
            fieldType = GenericTypeResolver.resolveTypeArgument(((DecoratingProxy) parser).getDecoratedClass(), Parser.class);
        }
        if (fieldType == null || !ReflectionUtils.isAssignablFrom(fieldType, sourceType)) {
            return false;
        }
        return true;
    }

    public <T> String printUsingCustomFormatter(T source, String converterName) {
        Printer<T> printer = appContext.getBean(converterName, Printer.class);
        return printer.print(source, LocaleContextHolder.getLocale());
    }

    public <T> T parseUsingCustomFormatter(String text, String converterName) throws ParseException {
            Parser<T> parser = appContext.getBean(converterName, Parser.class);
            if (StringUtils.isNullOrEmpty(text) && parser instanceof DateFormatter) {
                return null;
            }
            return parser.parse(text, LocaleContextHolder.getLocale());
    }
}
