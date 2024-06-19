package pl.fhframework.docs.availability.service;

import org.springframework.stereotype.Service;
import pl.fhframework.docs.availability.model.PropertyElement;

import java.util.Arrays;
import java.util.List;

/**
 * Created by k.czajkowski on 15.03.2017.
 */
@Service
public class PropertyElementServiceImpl implements PropertyElementService {

    @Override
    public List<PropertyElement> findAll() {
        return propertyElements;
    }

    private static List<PropertyElement> propertyElements = Arrays.asList(
            new PropertyElement(true),
            new PropertyElement(true),
            new PropertyElement(true),
            new PropertyElement(true),
            new PropertyElement(true),
            new PropertyElement(true)
    );

    public static List<PropertyElement> findAllProperties() {
        return propertyElements;
    }
}
