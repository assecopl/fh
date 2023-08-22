package pl.fhframework.docs.converter.model;

import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;
import pl.fhframework.model.forms.Styleable;

import java.text.ParseException;
import java.util.Locale;

@FhFormatter("styleFormatter")
public class StyleFormatter extends AutoRegisteredFormatter<Styleable.Style> {

    @Override
    public Styleable.Style parse(String s, Locale locale) throws ParseException {
        return Styleable.Style.valueOf(s);
    }

    @Override
    public String print(Styleable.Style style, Locale locale) {

        return style.toValue();
    }
}
