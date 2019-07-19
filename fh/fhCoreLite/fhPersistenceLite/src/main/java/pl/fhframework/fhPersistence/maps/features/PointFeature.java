package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.IPoint;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.Point;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@FeatureClassDiscriminator(featureClass = IPoint.TYPE)
public class PointFeature extends GenericMapFeature {

    protected Point geometry;

    public PointFeature() {
        super();
        geometry = new Point();
    }

    public PointFeature(String id) {
        super(id);
        geometry = new Point();
    }

    @Builder(toBuilder = true)
    public PointFeature(String id, Point geometry) {
        super(id);
        this.geometry = geometry;
    }

    public PointFeature(String id, double longitude, double latitude) {
        this(id, new Point(longitude, latitude));
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(IPoint.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
