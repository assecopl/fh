package pl.fhframework.docs.converter.model;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.docs.converter.service.UserService;
import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

@FhFormatter
public class UserFormatter extends AutoRegisteredFormatter<User> {

    @Autowired
    private UserService userService;

    @Override
    public User parse(String s, Locale locale) throws ParseException {
        return userService.findByLastName(s);
    }

    @Override
    public String print(User user, Locale locale) {
        return user.getLastName();
    }
}
