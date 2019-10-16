package pl.fhframework.core.maps.features.geometry;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

public interface IPoint extends IGeometry {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "Point";

    double getLongitude();
    void setLongitude(double longitude);
    double getLatitude();
    void setLatitude(double latitude);

    @Override
    default String getType() {
        return TYPE;
    }
}
