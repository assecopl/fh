package pl.fhframework.fhdp.example.lookup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Country {
    private String code;
    private String name;
    private String region;
}
