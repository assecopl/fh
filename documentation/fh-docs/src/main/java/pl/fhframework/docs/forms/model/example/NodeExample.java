package pl.fhframework.docs.forms.model.example;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Przykladowa klasa, ktora reprezentuje node w drzewku rozwijalnym.
 */
@Getter
@Setter
public class NodeExample {

    private long id;
    private String name;
    private String icon;
    private String buttonName;

    private List<NodeExample> children = new ArrayList<>();

    public NodeExample(long id) {
        this.id = id;
    }
}