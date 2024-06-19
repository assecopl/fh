package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CommissionService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WidgetElement extends ComponentElement {

    private List<Person> restrictedPeople = PersonService.findAllRestricted();
    private List<ChartSeries> provisionMockData;
    private List<ChartSeries> dollarExchangeMockData;
    private List<ChartSeries> populationMockData;

    public WidgetElement(CommissionService commissionService) {
        provisionMockData = commissionService.getProvisionsMockData();
        dollarExchangeMockData = commissionService.getDollarExchangeRateMockData();
        populationMockData = commissionService.getPopulationRateMockData();
    }
}