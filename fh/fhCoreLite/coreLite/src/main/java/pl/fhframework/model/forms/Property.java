package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.model.forms.attribute.PropertyMultiplicityAttrConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines element of form's internal model.
 */
@Control(parents = Model.class)
public class Property extends Component {

    /**
     * Defines model's element multiplicity.
     */
    public enum PropertyMultiplicity {

        SINGLE("Single"),
        MULTIPLE("Multiple"),
        MULTIPLE_PAGEABLE("Multiple-Pageable");

        @Getter
        private String displayName;

        PropertyMultiplicity(String displayName) {
            this.displayName = displayName;
        }
    }

    public static Map<String, String> TYPE_DISPLAYED_NAMES_MAPPING = new HashMap<>();
    static {
        TYPE_DISPLAYED_NAMES_MAPPING.put(String.class.getName(), "String");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Boolean.class.getName(), "Boolean");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Integer.class.getName(), "Integer");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Long.class.getName(), "Long");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Float.class.getName(), "Float");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Double.class.getName(), "Double");
        TYPE_DISPLAYED_NAMES_MAPPING.put(BigDecimal.class.getName(), "BigDecimal");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Date.class.getName(), "Timestamp");
        TYPE_DISPLAYED_NAMES_MAPPING.put(LocalDate.class.getName(), "Date");
        TYPE_DISPLAYED_NAMES_MAPPING.put(Resource.class.getName(), "Resource");
        TYPE_DISPLAYED_NAMES_MAPPING.put(LocalDateTime.class.getName(), "LocalDateTime");
    }

    public Property(Form form) {
        super(form);
    }

    @Getter
    @Setter
    @XMLProperty(required = true)
    private String name;

    @Getter
    @Setter
    @XMLProperty(required = true)
    private String type;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "single", converter = PropertyMultiplicityAttrConverter.class, aliases = "multiple")
    private PropertyMultiplicity multiplicity;

    @Getter
    @Setter
    @XMLProperty(value = "private", defaultValue = "false")
    private boolean privateProperty;

    public void setTypeAsDisplayed(String newType) {
        if (TYPE_DISPLAYED_NAMES_MAPPING.containsValue(newType)) {
            newType = CollectionsUtils.getKeyWithValue(TYPE_DISPLAYED_NAMES_MAPPING, newType);
        }
        setType(newType);
    }

    public String getTypeAsDisplayed() {
        return toTypeAsDisplayed(type);
    }

    public static String toTypeAsDisplayed(String type) {
        if (TYPE_DISPLAYED_NAMES_MAPPING.containsKey(type)) {
            return TYPE_DISPLAYED_NAMES_MAPPING.get(type);
        } else {
            return type;
        }
    }
}
