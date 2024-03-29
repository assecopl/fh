package pl.fhframework.fhdp.example.lookup.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class CountryApiResponse {
    private String status;
    @JsonProperty("status-code")
    private Integer statusCode;
    private String version;
    private String access;
    private Map<String, CountryEntity> data = new HashMap<>();
}
