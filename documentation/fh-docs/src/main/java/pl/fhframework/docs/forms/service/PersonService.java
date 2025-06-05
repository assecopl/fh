package pl.fhframework.docs.forms.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import pl.fhframework.core.paging.ComparatorFunction;
import pl.fhframework.docs.forms.model.example.Address;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.model.forms.PageModel;

import java.util.*;
import java.util.function.Function;

@Service
public class PersonService {

    private static final String GREEN = "#B0EC78";

    private static final String GRAY = "#CCCCCC";

    private static List<Person> people = new ArrayList<Person>(Arrays.asList(
            new Person(1l, "Bill", "Gates", "New York", "Male", "Active", "Poland", "C", new Date(1220227200l), null),
            new Person(2l, "Larry", "Ellison", "Los Angeles", "Male", "Inactive", "USA", "B", new Date(1220227200l), null),
            new Person(3l, "Sara", "Larson", "San Francisco", "Female", "Active", "France", "A", new Date(1220227200l), null),
            new Person(4l, "Jane ", "Goodall", "Sao Paulo", "Female", "Active", "Brazil", "A", new Date(1220227200l), null),
            new Person(5l, "Jocelyn ", "Bell Burnell", "Sydney", "Female", "Active", "Australia", "A", new Date(1240227200l), null),
            new Person(6l, "Johannes", "Kepler", "Sydney", "Male", "Active", "Australia", "A", new Date(1250227200l), null),
            new Person(7l, "Lise", "Meitner", "Sydney", "Female", "Active", "Australia", "A", new Date(1280227200l), null),
            new Person(8l, "Maria", "Mitchell", "Munich", "Female", "Active", "Germany", "A", new Date(1210227200l), null),
            new Person(9l, "Max", "Born", "Munich", "Male", "Active", "Germany", "A", new Date(1220227200l), null),
            new Person(10l, "Michael", "Faraday", "Rome", "Male", "Inactive", "Italy", "A", new Date(1220227200l), null),
            new Person(11l, "Nicolaus", "Copernicus", "Rome", "Male", "Active", "Italy", "A", new Date(1220227200l), null),
            new Person(12l, "Polly", "Matzinger", "Rome", "Female", "Active", "Italy", "A", new Date(1220227200l), null)
            ));

    private static List<Person> restrictedPeople = new ArrayList<Person>(Arrays.asList(
            new Person(1l, "Bill", "Gates", "New York", "Male", "Active", "Poland", "C", new Date(1220227200l), Arrays.asList(new Address("Warsaw"), new Address("Berlin"))),
            new Person(2l, "Larry", "Ellison", "Los Angeles", "Male", "Inactive", "USA", "B", new Date(1240227200l), Arrays.asList(new Address("Warsaw"), new Address("Monachium"))),
            new Person(3l, "Sara", "Larson", "San Francisco", "Female", "Active", "France", "A", new Date(1260227200l), Arrays.asList(new Address("Warsaw")))));

    private static Map<Person, String> coloredRestrictedPeople = null;

    private static Map<Person, String> coloredPagedPeople = null;

    private static List<Person> peopleForEmpty = new ArrayList<>(Arrays.asList(new Person(1l, "Bill", "Gates", "New York", "Male", "Active", "Poland", "C", new Date(1220227200l), null),
            new Person(2l, "Larry", "Ellison", "Los Angeles", "Male", "Inactive", "USA", "B", new Date(1220227200l), null),
            new Person(3l, "Sara", "Larson", "San Francisco", "Female", "Active", "France", "A", new Date(1220227200l), null),
            new Person(4l, "Jane ", "Goodall", "Sao Paulo", "Female", "Active", "Brazil", "A", new Date(1220227200l), null),
            new Person(5l, "Jocelyn ", "Bell Burnell", "Sydney", "Female", "Active", "Australia", "A", new Date(1240227200l), null),
            new Person(6l, "Johannes", "Kepler", "Sydney", "Male", "Active", "Australia", "A", new Date(1250227200l), null),
            new Person(7l, "Lise", "Meitner", "Sydney", "Female", "Active", "Australia", "A", new Date(1280227200l), null),
            new Person(8l, "Maria", "Mitchell", "Munich", "Female", "Active", "Germany", "A", new Date(1210227200l), null)));

    private static List<Person> activePeople = new ArrayList<>(Arrays.asList(new Person(1l, "Bill", "Gates", "New York", "Male", "Active", "Poland", "C", new Date(1220227200l), null),
            new Person(11l, "Garry", "Ellison", "Los Angeles", "Male", "Active", "USA", "B", new Date(1220227200l), null),
            new Person(13l, "Lara", "Larson", "San Francisco", "Female", "Active", "France", "A", new Date(1220227200l), null),
            new Person(14l, "Jane ", "Doogall", "Sao Paulo", "Female", "Active", "Brazil", "A", new Date(1220227200l), null),
            new Person(15l, "Jocelyn ", "Burnell", "Sydney", "Female", "Active", "Australia", "A", new Date(1240227200l), null),
            new Person(16l, "Johan", "Kepler", "Sydney", "Male", "Active", "Australia", "A", new Date(1250227200l), null),
            new Person(17l, "Lisa", "Mei", "Sydney", "Female", "Active", "Australia", "A", new Date(1280227200l), null),
            new Person(18l, "David", "Mitchell", "Munich", "Male", "Active", "Germany", "A", new Date(1210227200l), null)));

    private enum SortedProperty {
        PersonNameAndSurname((firstPerson, secondPerson) -> (firstPerson.getName() + firstPerson.getSurname()).compareTo(secondPerson.getName() + secondPerson.getSurname())),
        PersonName((firstPerson, secondPerson) -> firstPerson.getName().compareTo(secondPerson.getName())),
        PersonSurname((firstPerson, secondPerson) -> firstPerson.getSurname().compareTo(secondPerson.getSurname())),
        PersonCity((firstPerson, secondPerson) -> firstPerson.getCity().compareTo(secondPerson.getCity())),
        PersonGender((firstPerson, secondPerson) -> firstPerson.getGender().compareTo(secondPerson.getGender())),
        PersonStatus((firstPerson, secondPerson) -> firstPerson.getStatus().compareTo(secondPerson.getStatus())),
        PersonCitizenship((firstPerson, secondPerson) -> firstPerson.getCitizenship().compareTo(secondPerson.getCitizenship()));

        private ComparatorFunction<Person> comparator;

        SortedProperty(ComparatorFunction<Person> comparator) {
            this.comparator = comparator;
        }
    }

    public static List<Person> findAll() {
        return people;
    }

    public static List<Person> findPeopleForEmpty() {
        return peopleForEmpty;
    }

    public static List<Person> findAllRestricted() {
        return restrictedPeople;
    }

    public static List<Person> findActivePeople() {
        return activePeople;
    }

    public static Map<Person, String> findAllColoredRestricted() {
        coloredRestrictedPeople = new HashMap<>();
        if (restrictedPeople != null) {
            for (Person person : restrictedPeople) {
                String colorValue = null;
                if ("Active".equals(person.getStatus())) {
                    colorValue = GREEN;
                } else if ("Inactive".equals(person.getStatus())) {
                    colorValue = GRAY;
                }

                coloredRestrictedPeople.put(person, colorValue);
            }
        }

        return coloredRestrictedPeople;
    }

    public static Page<Person> findAll(Pageable pageable) {
        int startingPosition = pageable.getPageNumber() * pageable.getPageSize();
        List<Person> peopleCopy = new ArrayList<>(people);
        List<Person> filteredPeople = new LinkedList<>();

        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            Iterator<Sort.Order> orderIterator = pageable.getSort().iterator();
            Sort.Order order = orderIterator.next();
            if (order != null) {
                SortedProperty sortedProperty = SortedProperty.valueOf(order.getProperty());
                Collections.sort(peopleCopy, (first, second) -> sortedProperty.comparator.compare(first, second, order.getDirection()));
            }
        }

        for (int i = startingPosition; i < Integer.min(startingPosition + pageable.getPageSize(), peopleCopy.size()); i++) {
            filteredPeople.add(peopleCopy.get(i));
        }

        coloredPagedPeople = new HashMap<>();
        for (int i = 0; i < filteredPeople.size(); i++) {
            String colorValue = null;
            Person person = filteredPeople.get(i);
            if ("Active".equals(person.getStatus())) {
                colorValue = GREEN;
            } else if ("Inactive".equals(person.getStatus())) {
                colorValue = GRAY;
            }

            coloredPagedPeople.put(person, colorValue);
        }

        return new PageImpl<>(filteredPeople, pageable, peopleCopy.size());
    }

    public Page<Person> findAllPeople(Pageable pageable) {
        return PersonService.findAll(pageable);
    }

    public static PageModel<Person> findPageModel(PageRequest pageable) {
        PageModel<Person> pageModel = new PageModel<>(new Function<Pageable, Page<Person>>() {
            @Override
            public Page<Person> apply(Pageable pageable) {

                return (Page<Person>) PersonService.findAll((PageRequest) pageable);
            }
        });
        return pageModel;
    }


    public static Map<Person, String> findColoredPagedPeople() {
        return coloredPagedPeople;
    }

    public void save(Person person) {
        if (person.getCitizenship() == null) {
            person.setCitizenship("Poland");
        }

        if (person.getDrivingLicenseCategory() == null) {
            person.setDrivingLicenseCategory("B");
        }

        person.setId(new Random().nextLong());
        restrictedPeople.add(person);
    }

    public void update(Person person) {
        Person personToBeUpdated = restrictedPeople.stream().filter(personFromCollection -> person.getId().equals(personFromCollection.getId())).findFirst().get();
        personToBeUpdated = person;
    }

    public void delete(Person person) {
        restrictedPeople.remove(person);
    }
}
