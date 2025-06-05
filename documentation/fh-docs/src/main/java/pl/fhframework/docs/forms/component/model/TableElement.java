package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.model.tv.TvSeries;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.docs.forms.service.TvSeriesService;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableElement extends ComponentElement {

    @Autowired
    private TvSeriesService tvSeriesService;

    private List<Person> people = PersonService.findAll();
    private List<Person> restrictedPeople = PersonService.findAllRestricted();
    private Map<Person, String> coloredRestrictedPeople = PersonService.findAllColoredRestricted();
    private List<TvSeries> tvSeries;
    private Map<?, String> tvSeriesColored;
    private Person selectedPerson;
    private Person selectedReadOnlyPerson;
    private List<Person> multiSelectPeople = PersonService.findActivePeople();
    private List<Person> selectedActivePeople;
    private TvSeries selectedSerial;

    private String companyEmployeesEditableTableName = "Name";
    private String companyEmployeesEditableTableSurname = "Surname";
    private String companyEmployeesEditableTableCity = "City";
    private String companyEmployeesEditableTableGender = "Gender";
    private String companyEmployeesEditableTableStatus = "Status";
    private String companyEmployeesEditableTableCitizenship = "Citizenship";
    private String companyEmployeesEditableTableDrivingLicense = "Driving license";
    private String companyEmployeesEditableTableBirthDate = "Birth date";

    // grouping table headers binding
    private String ordinalNumber = "No.";
    private String tvSeriesNames = "TV Series name";
    private String season = "Season";
    private String seasonNumber = "Number";
    private String episode = "Episode";
    private String episodeTitle = "Title";
    private String episodeDetails = "Details";
    private String episodeActors = "Actors";
    private String episodeDuration = "Duration";
    private String seasonActions = "Actions";
    private String removeSeason = "Remove";
    private String episodeActions = "Episode actions";
    private String episodeRemove = "Remove";
    private String episodeDescription = "Description";
    private String countryName = "Country";

    public TableElement(TvSeriesService tvSeriesService) {
        tvSeries = tvSeriesService.findAll();
        tvSeriesColored = tvSeriesService.findAllColored();
    }

}
