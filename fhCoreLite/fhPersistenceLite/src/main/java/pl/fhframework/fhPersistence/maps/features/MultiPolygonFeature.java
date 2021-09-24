package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.IMultiPolygon;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.MultiPolygon;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@FeatureClassDiscriminator(featureClass = IMultiPolygon.TYPE)
public class MultiPolygonFeature extends GenericMapFeature {

    protected MultiPolygon geometry;

    public MultiPolygonFeature() {
        super();
        geometry = new MultiPolygon();
    }

    public MultiPolygonFeature(String id) {
        super(id);
        geometry = new MultiPolygon();
    }

    @Builder(toBuilder = true)
    public MultiPolygonFeature(String id, MultiPolygon geometry) {
        super(id);
        this.geometry = geometry;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(IMultiPolygon.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
