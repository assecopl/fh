package pl.fhframework.docs.converter.service;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import pl.fhframework.docs.converter.model.ForeignUser;
import pl.fhframework.docs.converter.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Amadeusz Szkiladz on 12.12.2016.
 */
@Service
public class UserService {

    public static final String ALL_USERS_CATEGORY = "All users";
    public static final String EMPTY_CATEGORY = "";
    public static final String ADMINS_CATEGORY = "Admins";
    public static final String OTHER_USERS_CATEGORY = "Other users";

    public User findByLastName(String s) {
        return findAll().stream().filter(u -> u.getLastName().equals(s)).findFirst().orElse(null);
    }

    public List<User> findAll() {
        return Stream.of(new User("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21)),
                new User("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5)),
                new User("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01))).collect(Collectors.toList());
    }

    public MultiValueMap<String, User> getComboData() {
        MultiValueMap<String, User> usersByCategory = new LinkedMultiValueMap<>();
        usersByCategory.add(ALL_USERS_CATEGORY, new User("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21)));
        usersByCategory.add(ALL_USERS_CATEGORY, new User("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5)));
        usersByCategory.add(ALL_USERS_CATEGORY, new User("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01)));

        usersByCategory.add(ADMINS_CATEGORY, new User("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21)));

        usersByCategory.add(OTHER_USERS_CATEGORY, new User("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5)));
        usersByCategory.add(OTHER_USERS_CATEGORY, new User("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01)));
        return usersByCategory;
    }

    public MultiValueMap<String, User> getSelect2ComboData() {
        MultiValueMap<String, User> usersByCategory = new LinkedMultiValueMap<>();
        usersByCategory.add(EMPTY_CATEGORY, new User("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21)));
        usersByCategory.add(EMPTY_CATEGORY, new User("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5)));
        usersByCategory.add(EMPTY_CATEGORY, new User("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01)));

        usersByCategory.add(EMPTY_CATEGORY, new User("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21)));

        usersByCategory.add(EMPTY_CATEGORY, new User("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5)));
        usersByCategory.add(EMPTY_CATEGORY, new User("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01)));
        return usersByCategory;
    }

    public List<ForeignUser> findAllForeignUsers() {
        return Stream.of(new ForeignUser("admin", "admin@myemail.com", "Jack", "Abell", 25, LocalDateTime.of(2012, 12, 12, 9, 21), "FBSYS1", "Foreign System 1"),
                new ForeignUser("approver", "mputz@myemail.com", "Marcelino", "Putz", 44, LocalDateTime.of(2014, 5, 5, 16, 5), "FBSYS2", "Foreign System 2"),
                new ForeignUser("analyst", "ethorp@myemail.com", "Erlinda", "Thorp", 65, LocalDateTime.of(2016, 10, 9, 10, 01), "FBSYS3", "Foreign System 3")).collect(Collectors.toList());
    }
}
