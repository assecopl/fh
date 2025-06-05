package pl.fhframework.docs.change.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Adam Zareba on 08.02.2017.
 */
@Getter
@Setter
public class Change {

    private String title;
    private String description;
    private Date version;
    private Type type;
    private String[] authors;
    private String ticketUrl;
    private String functionalArea;

    public Change(String title, String description, Date version, Type type, String ticketUrl, String[] authors, String functionalArea) {
        this.title = title;
        this.description = description;
        this.version = version;
        this.type = type;
        this.ticketUrl = ticketUrl;
        this.authors = authors;
        this.functionalArea = functionalArea;
    }

    public enum Type {
        BUG, IMPROVEMENT
    }
}
