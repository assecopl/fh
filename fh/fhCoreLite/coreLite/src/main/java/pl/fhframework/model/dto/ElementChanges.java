package pl.fhframework.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.FormElement;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class ElementChanges {

    @JsonIgnore
    private static AtomicInteger sid = new AtomicInteger();
	@JsonIgnore
    private String change = "change_"+sid.getAndIncrement();

    private String formId;

    private String formElementId;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Map<String, Object> changedAttributes = new LinkedHashMap<>();

    //private Set<ElementChanges> customElementsChanges;

    private Map<String, LinkedHashSet<FormElement>> addedComponents = new LinkedHashMap<>();

    private Set<String> removedComponents = new LinkedHashSet<>();

    /**
     * Adds a new change to be sent to client.
     * @param changedAttributeName attribute name
     * @param newValue new value
     */
    public void addChange(String changedAttributeName, Object newValue) {
        changedAttributes.put(changedAttributeName, newValue);
    }

    public boolean containsAnyChanges() {
        return !getChangedAttributes().isEmpty()
                || !getRemovedComponents().isEmpty()
                || !getAddedComponents().isEmpty();
    }
}
