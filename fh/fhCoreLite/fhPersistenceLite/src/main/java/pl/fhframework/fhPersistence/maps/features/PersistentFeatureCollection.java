package pl.fhframework.fhPersistence.maps.features;

import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.IFeatureCollection;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2019-01-08.
 */
public abstract class PersistentFeatureCollection extends BasePersistentObject implements IFeatureCollection {

    @Override
    public String getType() {
        return IFeatureCollection.TYPE;
    }

    protected List<IFeature> joinLists(List<? extends IFeature>... features) {
        return Arrays.stream(features).flatMap(List::stream).collect(Collectors.toList());
    }

    protected <T> List<T> filterList(List<? extends IFeature> features, Class<? extends T> clazz) {
        return features.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
    }
}
