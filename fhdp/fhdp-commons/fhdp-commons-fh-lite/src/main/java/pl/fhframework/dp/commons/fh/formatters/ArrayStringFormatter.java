package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.format.Formatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@FhFormatter("arrayStringFormatter")
public class ArrayStringFormatter implements Formatter<List<String>> {

    @Override
    public List<String> parse(String s, Locale locale) throws ParseException {
        return null;
    }

    @Override
    public String print(List<String> s, Locale locale) {
        return  s.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }
}
