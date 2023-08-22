package pl.fhframework.docs.change.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JiraJsonObject {

    private String maxResults;

    private List<Issue> issues;

}