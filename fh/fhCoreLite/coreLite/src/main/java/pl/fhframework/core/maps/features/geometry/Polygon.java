package pl.fhframework.core.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Polygon extends MapGeometry implements IPolygon {
    public Polygon() {
        super();
    }

    public Polygon(Coordinate[] coordinates) {
        super();

        geometry = geometryFactory.createPolygon(coordinates);
    }

    Polygon(org.locationtech.jts.geom.Polygon polygon) {
        this.geometry = polygon;
    }

    @Builder(toBuilder = true)
    public Polygon(@Builder.ObtainVia(method = "toImplPoints") @Singular List<Point> points) {
        this(points.stream().map(Point::toPoint).map(org.locationtech.jts.geom.Point::getCoordinate).toArray(Coordinate[]::new));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List<?> getCoordinates() {
        return Collections.singletonList(convertCoordinatesToList(geometry.getCoordinates()));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        // currently we only take care of first polygon for OpenLayers
        geometry = geometryFactory.createPolygon(convertListToCoordinates((List) coordinates.get(0)));
    }

    @Override
    public List<IPoint> getPoints() {
        return Arrays.asList(geometry.getCoordinates()).stream().map(Point::new).collect(Collectors.toList());
    }

    protected org.locationtech.jts.geom.Polygon toPolygon() {
        return ((org.locationtech.jts.geom.Polygon)geometry);
    }

    @Override
    public String toString() {
        return "Polygon{" + getPoints() + "}";
    }

    @JsonIgnore
    protected List<Point> toImplPoints() {
        return (List) getPoints();
    }
}
