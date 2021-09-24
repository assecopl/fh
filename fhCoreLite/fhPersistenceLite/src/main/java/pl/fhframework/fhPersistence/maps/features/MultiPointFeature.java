package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.IMultiPoint;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.MultiPoint;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@FeatureClassDiscriminator(featureClass = IMultiPoint.TYPE)
public class MultiPointFeature extends GenericMapFeature {

    protected MultiPoint geometry;

    public MultiPointFeature() {
        super();
        geometry = new MultiPoint();
    }

    public MultiPointFeature(String id) {
        super(id);
        geometry = new MultiPoint();
    }

    @Builder(toBuilder = true)
    public MultiPointFeature(String id, MultiPoint geometry) {
        super(id);
        this.geometry = geometry;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(IMultiPoint.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
