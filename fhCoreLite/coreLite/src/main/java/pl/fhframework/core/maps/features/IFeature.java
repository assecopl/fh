package pl.fhframework.core.maps.features;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.model.Model;

import java.util.List;
import java.util.Map;

@Model
public interface IFeature extends IGeographical {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "Feature";

    @ModelElement(type = ModelElementType.HIDDEN)
    String getClientId();

    @ModelElement(type = ModelElementType.HIDDEN)
    IGeometry getGeometry();

    @JsonIgnore
    @ModelElement(type = ModelElementType.HIDDEN)
    default List<?> getCoordinates() {
        return getGeometry().getCoordinates();
    }

    @ModelElement(type = ModelElementType.HIDDEN)
    default void setCoordinates(List<?> coordinates) {
        getGeometry().setCoordinates(coordinates);
    }

    default String getType() {
        return TYPE;
    }

    @JsonIgnore
    List<Class<? extends IGeometry>> allowedGeometries();

    @ModelElement(type = ModelElementType.HIDDEN)
    Map<String, Object> getProperties();
}
