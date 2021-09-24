package pl.fhframework.core.maps.features.geometry;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.List;

public interface IMultiPolygon extends IGeometry {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "MultiPolygon";

    List<IPolygon> getPolygons();

    @Override
    default String getType() {
        return TYPE;
    }
}
