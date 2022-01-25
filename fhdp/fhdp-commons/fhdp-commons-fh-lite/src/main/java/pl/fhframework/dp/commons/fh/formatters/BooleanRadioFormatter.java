package pl.fhframework.dp.commons.fh.formatters;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

@FhFormatter("booleanRadioFormatter")
public class BooleanRadioFormatter implements Formatter<String> {

    @Autowired
    private MessageService messageService;

    @Override
    public String parse(String s, Locale locale) throws ParseException {
        return null;
    }

    @SneakyThrows
    @Override
    public String print(String s, Locale locale) {
        return messageService.getAllBundles().getMessage(s);
        /*
        switch (s) {
            case "true": {
                return "Tak";
            }
            case "false": {
                return "Nie";
            }
            case "": {
                return "Nie wybrano";
            }
        }
        throw new ValidationException("Key out of scope");

         */
    }
}
