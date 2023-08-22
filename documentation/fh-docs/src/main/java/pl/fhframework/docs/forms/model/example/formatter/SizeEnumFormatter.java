package pl.fhframework.docs.forms.model.example.formatter;

import pl.fhframework.docs.forms.model.example.SizeEnum;
import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * SizeEnum formatter.
 */
@FhFormatter
public class SizeEnumFormatter extends AutoRegisteredFormatter<SizeEnum> {
    @Override
    public SizeEnum parse(String id, Locale locale) throws ParseException {
        return SizeEnum.findById(id);
    }

    @Override
    public String print(SizeEnum sizeEnum, Locale locale) {
        return sizeEnum.getId();
    }
}
