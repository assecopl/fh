package pl.fhframework.core.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2019-01-23.
 */
@Data
public class MultiPoint extends MapGeometry implements IMultiPoint {

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
