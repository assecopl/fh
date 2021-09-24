package pl.fhframework.fhPersistence.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.IMultiPoint;
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
@DiscriminatorValue(IMultiPoint.TYPE)
public class MultiPoint extends PersistentGeometry implements IMultiPoint {
    public MultiPoint() {
        super();
    }

    public MultiPoint(Coordinate[] coordinates) {
        super();

        geometry = geometryFactory.createMultiPoint(coordinates);
    }

    @Builder(toBuilder = true)
    public MultiPoint(@Builder.ObtainVia(method = "toImplPoints") @Singular List<Point> points) {
        this(points.stream().map(Point::toPoint).map(org.locationtech.jts.geom.Point::getCoordinate).toArray(Coordinate[]::new));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List getCoordinates() {
        return convertCoordinatesToList(geometry.getCoordinates());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        geometry = geometryFactory.createMultiPoint(convertListToCoordinates(coordinates));
    }

    @Override
    public List<IPoint> getPoints() {
        return Arrays.asList(geometry.getCoordinates()).stream().map(Point::new).collect(Collectors.toList());
    }

    @JsonIgnore
    protected List<Point> toImplPoints() {
        return (List) getPoints();
    }

    @Override
    public String toString() {
        return "MultiPoint{" + getPoints() + '}';
    }
}
