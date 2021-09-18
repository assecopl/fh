package pl.fhframework.core.maps.features.geometry;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.List;

public interface IMultiPoint extends IGeometry {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "MultiPoint";

    List<IPoint> getPoints();

    @Override
    default String getType() {
        return TYPE;
    }
}
