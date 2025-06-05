package pl.fhframework.docs.availability.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.docs.availability.service.PropertyElementService;
import pl.fhframework.docs.availability.service.PropertyElementServiceImpl;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@Scope (ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AvailabilityConfigurationModel {

    @Autowired
    PropertyElementService propertyElementService;

    List<PropertyElement> propertyElements = PropertyElementServiceImpl.findAllProperties();
    AccessibilityEnum controlAvailability = AccessibilityEnum.EDIT;

    private boolean available = false;
}
