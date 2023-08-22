package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import pl.fhframework.docs.converter.service.EventService;
import pl.fhframework.core.designer.ComponentElement;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static java.util.Map.Entry;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CalendarElement extends ComponentElement {

    private LocalDate selectedGameDate;
    private LocalDate selectedSportDate;
    private LocalDate selectedMusicDate;
    private LocalDate minDate;
    private LocalDate maxDate;
    private List<LocalDate> blockedDates;
    private List<LocalDate> scheduleOfSimpleCollection;
    private List<LocalDate> weekends;
    private MultiValueMap<Entry<String, String>, LocalDate> scheduleOfGameEventsData;
    private MultiValueMap<Entry<String, String>, LocalDate> scheduleOfSportEventsData;
    private MultiValueMap<Entry<String, String>, LocalDate> scheduleOfMusicEventsData;

    public CalendarElement(EventService eventService) {
        this.selectedGameDate = eventService.getDate("10");
        this.selectedSportDate = eventService.getDate("11");
        this.selectedMusicDate = eventService.getDate("12");
        this.minDate = eventService.getDate("12");
        this.maxDate = eventService.getDate("26");
        this.blockedDates = eventService.getBlockedDates();
        this.scheduleOfSimpleCollection = eventService.getScheduleOfSimpleCollection();
        this.scheduleOfGameEventsData = eventService.getScheduleOfGameEventsData();
        this.scheduleOfSportEventsData = eventService.getScheduleOfSportEventsData();
        this.scheduleOfMusicEventsData = eventService.getScheduleOfMusicEventsData();
        this.weekends = eventService.getMonthWeekends();
    }
}
