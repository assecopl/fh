package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.util.StringUtils;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.dto.ElementChanges;

@Control(parents = {TablePaged.class, ColumnPaged.class})
public class ColumnPaged extends Column {


    public ColumnPaged(Form form) {
        super(form);
    }

    public void init() {
        super.init();
    }
}
