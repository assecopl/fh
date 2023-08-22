package pl.fhframework.docs.converter.model;

import org.springframework.format.Formatter;

import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

@FhFormatter("userSecondFormatter")
public class UserSecondFormatter implements Formatter<User> {

    @Override
    public String print(User user, Locale locale) {
        return user.getFirstName();
    }

    @Override
    public User parse(String s, Locale locale) throws ParseException {
        return null;
    }

}