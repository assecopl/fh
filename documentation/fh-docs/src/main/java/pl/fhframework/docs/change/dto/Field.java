package pl.fhframework.docs.change.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Created by mateusz.zaremba
 */
@Getter
@Setter
public class Field {

    private String summary;

    private List<Task> subtasks;

    private Date created;

    private String description;

    private Asignee assignee;

    private List<String> labels;

    private List<Component> components;
}
