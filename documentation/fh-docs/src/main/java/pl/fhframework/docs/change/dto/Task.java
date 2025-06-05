package pl.fhframework.docs.change.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by mateusz.zaremba
 */
@Getter
@Setter
public class Task {

    private String key;

    private Field fields;

    private String self;
}
