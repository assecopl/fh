package pl.fhframework.core.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Geometry;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.List;

/**
 * Created by pawel.ruta on 2019-01-23.
 */
public interface IGeometry {
    String getType();
    @ModelElement(type = ModelElementType.HIDDEN)
    String getClientId();
    @JsonIgnore
    @ModelElement(type = ModelElementType.HIDDEN)
    List getCoordinates();
    Geometry getGeometry();
    void setGeometry(Geometry geometry);
    @ModelElement(type = ModelElementType.HIDDEN)
    void setCoordinates(List coordinates);
}
