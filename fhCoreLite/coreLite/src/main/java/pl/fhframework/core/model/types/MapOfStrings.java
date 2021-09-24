package pl.fhframework.core.model.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Model;

import java.util.HashMap;
import java.util.Map;

@Model
@Builder
@Getter
@Setter
public class MapOfStrings {
    private Map<String, String> value = new HashMap<>();
}
