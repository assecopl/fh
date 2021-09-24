package pl.fhframework.fhPersistence.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.ILineString;
import pl.fhframework.core.maps.features.geometry.IPoint;
import pl.fhframework.core.model.Model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Model
@Getter
@Setter
@Entity
@DiscriminatorValue(ILineString.TYPE)
public class LineString extends PersistentGeometry implements ILineString {

    public LineString() {
        super();
    }

    public LineString(Coordinate[] coordinates) {
        super();

        geometry = geometryFactory.createLineString(coordinates);
    }

    @Builder(toBuilder = true)
    public LineString(@Builder.ObtainVia(method = "toImplPoints") @Singular List<Point> points) {
        this(points.stream().map(Point::toPoint).map(org.locationtech.jts.geom.Point::getCoordinate).toArray(Coordinate[]::new));
    }

    LineString(org.locationtech.jts.geom.LineString lineString) {
        this.geometry = lineString;
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List getCoordinates() {
        return convertCoordinatesToList(geometry.getCoordinates());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        geometry = geometryFactory.createLineString(convertListToCoordinates(coordinates));
    }

    @Override
    public List<IPoint> getPoints() {
        return Arrays.asList(geometry.getCoordinates()).stream().map(Point::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "LineString{" + getPoints() + '}';
    }

    protected org.locationtech.jts.geom.LineString toLineString() {
        return ((org.locationtech.jts.geom.LineString)geometry);
    }

    @JsonIgnore
    protected List<Point> toImplPoints() {
        return (List) getPoints();
    }
}
