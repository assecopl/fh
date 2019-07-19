package pl.fhframework.core.maps.features;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.List;

public interface IFeatureCollection extends IGeographical {
    @ModelElement(type = ModelElementType.HIDDEN)
    String TYPE = "FeatureCollection";

    @ModelElement(type = ModelElementType.HIDDEN)
    String DiscriminatorField = "featureclass";

    List<IFeature> allFeatures();

    void addFeature(IFeature feature);

    void removeFeature(IFeature feature);
}
