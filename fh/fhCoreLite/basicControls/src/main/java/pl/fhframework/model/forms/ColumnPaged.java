package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.util.StringUtils;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;

import lombok.Getter;
import lombok.Setter;

@Control(parents = {TablePaged.class, ColumnPaged.class})
public class ColumnPaged extends Column {

    @Getter
    @Setter
    private boolean sortable = false;

    @JsonIgnore
    @Getter
    @Setter
    @DocumentedComponentAttribute(value = "Property name passed in the Pageable object to be interpreted in a data source (eg. DAO)")
    @XMLProperty
    @DesignerXMLProperty(commonUse = true)
    private String sortBy;

    public ColumnPaged(Form form) {
        super(form);
    }

    public void init() {
        super.init();
        sortable = !StringUtils.isNullOrEmpty(sortBy) && !isSubColumnsExists();
    }
}
