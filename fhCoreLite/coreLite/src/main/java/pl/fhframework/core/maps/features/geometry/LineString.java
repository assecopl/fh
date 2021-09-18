package pl.fhframework.core.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LineString extends MapGeometry implements ILineString {
    @Singular
    @Builder.Default
    private List<IPoint> points = new ArrayList<>();

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
