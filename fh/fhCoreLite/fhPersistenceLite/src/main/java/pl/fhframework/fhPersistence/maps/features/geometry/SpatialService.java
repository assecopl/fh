package pl.fhframework.fhPersistence.maps.features.geometry;

import org.locationtech.jts.geom.Geometry;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.services.FhService;
import pl.fhframework.fhPersistence.maps.features.LineStringFeature;
import pl.fhframework.fhPersistence.maps.features.PointFeature;
import pl.fhframework.fhPersistence.maps.features.PolygonFeature;
import pl.fhframework.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Created by pawel.ruta on 2019-01-08.
 */
@FhService(groupName = "spatial", categories = "gis")
public class SpatialService {
    private static Map<String, MathTransform> crsTransforms = new ConcurrentHashMap<>();

    public Point asPoint(IGeometry geometry) {
        return cast(Point.class, geometry);
    }

    public LineString asLineString(IGeometry geometry) {
        return cast(LineString.class, geometry);
    }

    public Polygon asPolygon(IGeometry geometry) {
        return cast(Polygon.class, geometry);
    }

    public boolean isPoint(IGeometry geometry) {
        return isInstance(PointFeature.class, geometry);
    }

    public boolean isLineString(IGeometry geometry) {
        return isInstance(LineStringFeature.class, geometry);
    }

    public boolean isPolygon(IGeometry geometry) {
        return isInstance(PolygonFeature.class, geometry);
    }

    public Point.PointBuilder newPoint() {
        return Point.builder();
    }

    public LineString.LineStringBuilder newLineString() {
        return LineString.builder();
    }

    public Polygon.PolygonBuilder newPolygon() {
        return Polygon.builder();
    }

    public boolean intersects(IGeometry a, IGeometry b) {
        return applyGeometryRelation(a, b, Geometry::intersects);
    }

    public boolean touches(IGeometry a, IGeometry b) {
        return applyGeometryRelation(a, b, Geometry::touches);
    }

    public boolean crosses(IGeometry a, IGeometry b) {
        return applyGeometryRelation(a, b, Geometry::crosses);
    }

    public boolean contains(IGeometry a, IGeometry b) {
        return applyGeometryRelation(a, b, Geometry::contains);
    }

    public boolean disjoint(IGeometry a, IGeometry b) {
        return applyGeometryRelation(a, b, Geometry::disjoint);
    }

    public IGeometry intersection(IGeometry a, IGeometry b) {
        return applyGeometryFunction(a, b, Geometry::intersection);
    }

    public IGeometry union(IGeometry a, IGeometry b) {
        return applyGeometryFunction(a, b, Geometry::union);
    }

    public IGeometry difference(IGeometry a, IGeometry b) {
        return applyGeometryFunction(a, b, Geometry::difference);
    }

    public Double distance(IGeometry a, IGeometry b) {
        if (isNullGeometry(a, b)) {
            return null;
        }
        return transformToCrs(a.getGeometry(), 3035).distance(transformToCrs(b.getGeometry(), 3035));
    }

    private boolean applyGeometryRelation(IGeometry a, IGeometry b, BiFunction<Geometry, Geometry, Boolean> function) {
        if (isNullGeometry(a, b)) {
            return false;
        }
        return function.apply(a.getGeometry(), b.getGeometry());
    }

    private IGeometry applyGeometryFunction(IGeometry a, IGeometry b, BiFunction<Geometry, Geometry, Geometry> function) {
        if (isNullGeometry(a, b)) {
            return null;
        }
        Geometry geometry = function.apply(a.getGeometry(), b.getGeometry());
        IGeometry mapGeometry = (IGeometry) ReflectionUtils.newInstance(ReflectionUtils.getClassForName(GeometryType.valueOfType(geometry.getGeometryType()).getImplClass()));
        mapGeometry.setGeometry(geometry);

        return mapGeometry;
    }

    private <T> T cast(Class<? extends T> clazz, Object obj) {
        return clazz.cast(obj);
    }

    private boolean isInstance(Class<?> clazz, Object obj) {
        return clazz.isInstance(obj);
    }

    private boolean isNullGeometry(IGeometry... geometries) {
        for (IGeometry geometry : geometries) {
            if (geometry == null || geometry.getGeometry() == null) {
                return true;
            }
        }
        return false;
    }

    public static Geometry transformToCrs(Geometry geometry, int toCrsId) {
        if (geometry == null) {
            return null;
        }

        MathTransform transform = crsTransforms.computeIfAbsent(geometry.getSRID() + ";" + toCrsId, key -> {
            CoordinateReferenceSystem sourceCRS = findCRS(geometry.getSRID());
            CoordinateReferenceSystem targetCRS = findCRS(toCrsId);
            try {
                return CRS.findMathTransform( sourceCRS, targetCRS);
            } catch (FactoryException e) {
                throw new FhException(e);
            }
        });

        try {
            return JTS.transform(geometry, transform);
        } catch (TransformException e) {
            throw new FhException(e);
        }
    }

    /**
     * Return a Coordinate Reference System for the given spatial reference identifier
     *
     * @param srid The spatial reference identifier
     * @return The coordinate reference system that corresponds with the given srid.
     */
    private static CoordinateReferenceSystem findCRS(int srid) {
        try {
            return CRS.decode("EPSG:" + srid);
        } catch (Exception e) {
            // Can probably not happen since we specified the authority code. Nevertheless...
            throw new FhException(e);
        }
    }
}
