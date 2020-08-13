package pl.fhframework.model.forms.attributes.formatter;

import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;
import pl.fhframework.model.forms.attributes.FloatingGroupStateAttribute;

import java.text.ParseException;
import java.util.Locale;

@FhFormatter("floatingStateFormatter")
public class FloatingStateFormatter extends AutoRegisteredFormatter<FloatingGroupStateAttribute.FloatingState> {
    @Override
	public String print(FloatingGroupStateAttribute.FloatingState aElement, Locale locale) {
   		return aElement.toString();
   	}

   	@Override
   	public FloatingGroupStateAttribute.FloatingState parse(String s, Locale locale) throws ParseException {
   		return FloatingGroupStateAttribute.FloatingState.valueOf(s);
   	}
}