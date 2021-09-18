package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.IMultiLineString;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.MultiLineString;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@FeatureClassDiscriminator(featureClass = IMultiLineString.TYPE)
public class MultiLineStringFeature extends GenericMapFeature {
    protected MultiLineString geometry;

    public MultiLineStringFeature() {
        super();
        geometry = new MultiLineString();
    }

    public MultiLineStringFeature(String id) {
        super(id);
        geometry = new MultiLineString();
    }

    @Builder(toBuilder = true)
    public MultiLineStringFeature(String id, MultiLineString geometry) {
        super(id);
        this.geometry = geometry;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(IMultiLineString.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
