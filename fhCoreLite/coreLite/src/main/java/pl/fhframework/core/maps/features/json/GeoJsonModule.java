package pl.fhframework.core.maps.features.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import pl.fhframework.core.maps.features.ArcGisFeature;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.IFeatureCollection;
import pl.fhframework.core.maps.features.IGeographical;

/**
 * Created by pawel.ruta on 2019-01-14.
 */
public class GeoJsonModule extends SimpleModule {
    public GeoJsonModule() {
        setDeserializers(new InheritanceDeserializer());

        addSerializer(IGeographical.class, new FeatureSerializer());
        addDeserializer(IFeatureCollection.class, new FeatureCollectionDeserializer());
        addDeserializer(IFeature.class, new FeatureDeserializer());
        addSerializer(ArcGisFeature.class, new ArcGisSerializer());
    }
}
