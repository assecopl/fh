package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.IPolygon;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.Polygon;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@FeatureClassDiscriminator(featureClass = IPolygon.TYPE)
public class PolygonFeature extends GenericMapFeature {

    protected Polygon geometry;

    public PolygonFeature() {
        super();
        geometry = new Polygon();
    }

    public PolygonFeature(String id) {
        super(id);
        geometry = new Polygon();
    }

    @Builder(toBuilder = true)
    public PolygonFeature(String id, Polygon geometry) {
        super(id);
        this.geometry = geometry;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(IPolygon.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
