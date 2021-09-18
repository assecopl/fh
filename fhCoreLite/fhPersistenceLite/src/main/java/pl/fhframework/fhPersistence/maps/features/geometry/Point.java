package pl.fhframework.fhPersistence.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import lombok.Builder;
import lombok.Data;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.IPoint;
import pl.fhframework.core.model.Model;

import javax.persistence.*;
import java.util.List;

@Model
@Getter
@Setter
@Entity
@DiscriminatorValue(IPoint.TYPE)
public class Point extends PersistentGeometry implements IPoint {

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

    public void simpleSetCoordinates(Double x, Double y){
        geometry = geometryFactory.createPoint(new Coordinate(x, y));
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
    public void setLongitude(double longitude) {
        geometry = geometryFactory.createPoint(new Coordinate(longitude, getLatitude()));
    }

    @Override
    public double getLatitude() {
        return toPoint().getY();
    }

    @Override
    public void setLatitude(double latitude) {
        geometry = geometryFactory.createPoint(new Coordinate(getLongitude(), latitude));
    }

    protected org.locationtech.jts.geom.Point toPoint() {
        return ((org.locationtech.jts.geom.Point)geometry);
    }
}
