package pl.fhframework.core.designer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Amadeusz Szkiladz on 22.11.2016.
 */
@Getter
@Setter
public class DocumentedAttribute {
    private String name;
    private String type;
    private String description;
    private String defaultValue;
    private boolean isBoundable;

    private List<DocumentedAttribute> nestedAttributes = new ArrayList<>();
}
