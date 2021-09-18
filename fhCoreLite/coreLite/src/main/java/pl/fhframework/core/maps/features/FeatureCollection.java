package pl.fhframework.core.maps.features;

import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel.ruta on 2019-01-08.
 */
@Model
@FeatureClassDiscriminator(classDiscriminatorKey = FeatureCollection.DiscriminatorField)
public class FeatureCollection implements IFeatureCollection {

    private List<IFeature> features = new ArrayList<>();

    @Override
    public String getType() {
        return IFeatureCollection.TYPE;
    }

    @Override
    public List<IFeature> allFeatures() {
        return features;
    }

    @Override
    public void addFeature(IFeature feature) {
        features.add(feature);
    }

    @Override
    public void removeFeature(IFeature feature) {
        features.remove(feature);
    }
}
