package pl.fhframework.core.maps.features;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericMapFeature extends MapFeature {
    @Getter
    protected String id;

    @Getter
    @Singular
    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    public GenericMapFeature() {
        super();
    }

    public GenericMapFeature(String id) {
        this.id = id;
    }
}
