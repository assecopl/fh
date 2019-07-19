package pl.fhframework.core.maps.features.geometry;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

public interface IPoint extends IGeometry {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "Point";

    double getLongitude();
    double getLatitude();

    @Override
    default String getType() {
        return TYPE;
    }
}
