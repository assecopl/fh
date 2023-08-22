package pl.fhframework.docs.converter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.format.FhConversionService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static java.util.Map.Entry;

/**
 * Created by Adam Zareba on 16.01.2017.
 */
@Service
public class EventService {
    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
    private static final String CURRENT_YEAR_MONTH = YEAR_MONTH_FORMAT.format(LocalDate.now());
    private static final Integer CURRENT_YEAR = Integer.valueOf(YEAR_FORMAT.format(LocalDate.now()));
    private static final Integer CURRENT_MONTH = Integer.valueOf(MONTH_FORMAT.format(LocalDate.now()));

    public static final String HOLIDAYS = "Holidays";
    public static final String SELECTED_DATES = "Selected dates";
    public static final String GAME_EVENTS = "Game events";
    public static final String SPORT_EVENTS = "Sport events";
    public static final String MUSIC_EVENTS = "Music events";

    private static final String YELLOW = "#ffd106";
    private static final String GREEN = "#85b616";
    private static final String RED = "#ef444d";
    private static final String BLUE = "#99bbff";
    private static final String GRAY = "#d1d1e0";

    @Autowired
    private FhConversionService conversionService;

    public MultiValueMap<Entry<String, String>, LocalDate> getScheduleOfGameEventsData() {
        MultiValueMap<Entry<String, String>, LocalDate> eventsCollection = new LinkedMultiValueMap<>();
        SimpleImmutableEntry<String, String> holidays = new SimpleImmutableEntry<>(HOLIDAYS, YELLOW);
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-23", LocalDate.class));

        SimpleImmutableEntry<String, String> selectedDates = new SimpleImmutableEntry<>(SELECTED_DATES, GREEN);
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-20", LocalDate.class));

        SimpleImmutableEntry<String, String> gameEvents = new SimpleImmutableEntry<>(GAME_EVENTS, RED);
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-24", LocalDate.class));

        return eventsCollection;
    }

    public List<LocalDate> getMonthWeekends() {
        Set<DayOfWeek> weekendDays = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        YearMonth yearMonth = YearMonth.of(CURRENT_YEAR, CURRENT_MONTH);
        int initialCapacity = ((yearMonth.lengthOfMonth() / 7) + 1) * weekendDays.size();  // Maximum possible weeks * number of days per week.

        List<LocalDate> mothWeekends = new ArrayList<>(initialCapacity);
        for (int dayOfMonth = 1; dayOfMonth <= yearMonth.lengthOfMonth(); dayOfMonth++) {
            LocalDate localDate = yearMonth.atDay(dayOfMonth);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            if (weekendDays.contains(dayOfWeek)) {
                // Is this date *is* one of the days we care about, collect it.
                mothWeekends.add(localDate);
            }
        }

        return mothWeekends;
    }

    public MultiValueMap<Entry<String, String>, LocalDate> getScheduleOfSportEventsData() {
        MultiValueMap<Entry<String, String>, LocalDate> eventsCollection = new LinkedMultiValueMap<>();
        SimpleImmutableEntry<String, String> holidays = new SimpleImmutableEntry<>(HOLIDAYS, YELLOW);
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-23", LocalDate.class));

        SimpleImmutableEntry<String, String> selectedDates = new SimpleImmutableEntry<>(SELECTED_DATES, BLUE);
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-10", LocalDate.class));
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-20", LocalDate.class));

        SimpleImmutableEntry<String, String> gameEvents = new SimpleImmutableEntry<>(SPORT_EVENTS, RED);
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-05", LocalDate.class));
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-24", LocalDate.class));

        return eventsCollection;
    }

    public MultiValueMap<Entry<String, String>, LocalDate> getScheduleOfMusicEventsData() {
        MultiValueMap<Entry<String, String>, LocalDate> eventsCollection = new LinkedMultiValueMap<>();
        SimpleImmutableEntry<String, String> holidays = new SimpleImmutableEntry<>(HOLIDAYS, YELLOW);
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
        eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-24", LocalDate.class));

        SimpleImmutableEntry<String, String> selectedDates = new SimpleImmutableEntry<>(SELECTED_DATES, GREEN);
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-10", LocalDate.class));
        eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-21", LocalDate.class));

        SimpleImmutableEntry<String, String> gameEvents = new SimpleImmutableEntry<>(MUSIC_EVENTS, GRAY);
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-06", LocalDate.class));
        eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-24", LocalDate.class));

        return eventsCollection;
    }

    public List<LocalDate> getScheduleOfSimpleCollection() {
        return Arrays.asList(conversionService.convert(CURRENT_YEAR_MONTH + "-03", LocalDate.class),
                conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class),
                conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
    }

    public List<LocalDate> getBlockedDates() {
        return Arrays.asList(conversionService.convert(CURRENT_YEAR_MONTH + "-03", LocalDate.class), conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class), conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
    }

    public LocalDate getDate(String date) {
        return conversionService.convert(CURRENT_YEAR_MONTH + "-" + date, LocalDate.class);
    }
}
