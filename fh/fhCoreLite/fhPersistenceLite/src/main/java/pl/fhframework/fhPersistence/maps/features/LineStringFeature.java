package pl.fhframework.fhPersistence.maps.features;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.geometry.ILineString;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.maps.features.geometry.LineString;

import java.util.Collections;
import java.util.List;

@Model
@Getter
@Setter
@Data
@FeatureClassDiscriminator(featureClass = ILineString.TYPE)
public class LineStringFeature extends GenericMapFeature {

    protected LineString geometry;

    public LineStringFeature() {
        super();
        geometry = new LineString();
    }

    public LineStringFeature(String id) {
        super(id);
        geometry = new LineString();
    }

    @Builder(toBuilder = true)
    public LineStringFeature(String id, LineString geometry) {
        super(id);
        this.geometry = geometry;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Collections.singletonList(ILineString.class);
    }

    @Override
    public String toString() {
        return geometry != null ? geometry.toString() : super.toString();
    }
}
