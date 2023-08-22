package pl.fhframework.docs.change.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by mateusz.zaremba
 */
@Getter
@Setter
public class Issue {

    private Field fields;

    private String self;
}
