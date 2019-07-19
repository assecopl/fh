package pl.fhframework.model.forms.attribute;

import pl.fhframework.model.forms.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Piotr on 2017-02-09.
 */
public class CommaSeparatedStringListAttrConverter implements IComponentAttributeTypeConverter<List<String>> {

    public static final String SEPARATOR = ",";

    @Override
    public List<String> fromXML(Component owner, String value) {
        if (value == null) {
            return null;
        } else {
            return Arrays.asList(value.split(SEPARATOR)).stream()
                    .map(String::trim) // trim
                    .filter(s -> !s.isEmpty()) // ommit empty strings
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, List<String> value) {
        if (value == null) {
            return null;
        } else {
            return value.stream()
                    .map(String::trim) // trim
                    .filter(s -> !s.isEmpty()) // ommit empty strings
                    .collect(Collectors.joining(SEPARATOR)); // join using comma
        }
    }
}
