package pl.fhframework.core.maps.features.geometry;

import org.locationtech.jts.geom.Coordinate;
import lombok.Builder;
import lombok.Data;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.List;

@Data
public class Point extends MapGeometry implements IPoint {
    public Point() {
        super();
    }

    public Point(Coordinate coordinate) {
        super();

        geometry = geometryFactory.createPoint(coordinate);
    }

    @Builder(toBuilder = true)
    public Point(@Builder.ObtainVia(method = "getLongitude") double longitude, @Builder.ObtainVia(method = "getLatitude") double latitude) {
        this(new Coordinate(longitude, latitude));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List getCoordinates() {
        return convertCoordinateToList(geometry.getCoordinate());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        geometry = geometryFactory.createPoint(convertListToCoordinate(coordinates));
    }

    @Override
    public String toString() {
        return "Point{" + getLongitude() + ", " + getLatitude() + '}';
    }

    @Override
    public double getLongitude() {
        return toPoint().getX();
    }

    @Override
    public double getLatitude() {
        return toPoint().getY();
    }

    protected org.locationtech.jts.geom.Point toPoint() {
        return ((org.locationtech.jts.geom.Point)geometry);
    }
}
