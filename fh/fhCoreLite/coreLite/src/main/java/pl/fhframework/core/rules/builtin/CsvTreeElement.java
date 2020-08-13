package pl.fhframework.core.rules.builtin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV-like tree element data. May be used for filling trees in fast prototyping.
 */
@Getter
@Setter
public class CsvTreeElement {

    private String label;

    private List<CsvTreeElement> subelements = new ArrayList<>();

    @Override
    public String toString() {
        return "CsvTreeElement{" +
                "label='" + label + '\'' +
                ", subelements=" + subelements +
                '}';
    }
}
