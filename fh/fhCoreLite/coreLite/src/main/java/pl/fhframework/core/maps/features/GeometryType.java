package pl.fhframework.core.maps.features;

import lombok.Getter;
import pl.fhframework.core.maps.features.geometry.*;

import java.util.Objects;

/**
 * Created by pawel.ruta on 2019-01-10.
 */
public enum GeometryType {
    Point(IPoint.class, "pl.fhframework.fhPersistence.maps.features.geometry.Point", IPoint.TYPE),
    LineString(ILineString.class, "pl.fhframework.fhPersistence.maps.features.geometry.LineString", ILineString.TYPE),
    Polygon(IPolygon.class, "pl.fhframework.fhPersistence.maps.features.geometry.Polygon", IPolygon.TYPE),
    MultiPoint(IMultiPoint.class, "pl.fhframework.fhPersistence.maps.features.geometry.MultiPoint", IMultiPoint.TYPE),
    MultiLineString(IMultiLineString.class, "pl.fhframework.fhPersistence.maps.features.geometry.MultiLineString", IMultiLineString.TYPE),
    MultiPolygon(IMultiPolygon.class, "pl.fhframework.fhPersistence.maps.features.geometry.MultiPolygon", IMultiPolygon.TYPE),
    FeatureCollection(IFeatureCollection.class, FeatureCollection.class.getName(), IFeatureCollection.TYPE);

    public static final String AnyGeometry = "pl.fhframework.fhPersistence.maps.features.geometry.PersistentGeometry";

    @Getter
    private final Class<?> intClass;
    @Getter
    private final String implClass;
    @Getter
    private final String type;

    GeometryType(Class<?> intClass, String implClass, String type) {
        this.intClass = intClass;
        this.implClass = implClass;
        this.type = type;
    }

    public static GeometryType valueOfType(String type) {
        for (GeometryType value : values()) {
            if (Objects.equals(value.getType(), type)) {
                return value;
            }
        }

        return null;
    }
}
