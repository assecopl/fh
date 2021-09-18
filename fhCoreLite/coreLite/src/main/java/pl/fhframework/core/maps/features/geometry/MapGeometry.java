package pl.fhframework.core.maps.features.geometry;

import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class MapGeometry implements ISnapshotEnabled, IGeometry {
    static protected GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), /*WGS84*/4326);

    @Getter
    protected String id;

    protected String clientId;

    private static AtomicInteger counter = new AtomicInteger(0);

    protected Geometry geometry;

    MapGeometry() {
        this.clientId = String.valueOf(counter.incrementAndGet());
    }

    MapGeometry(String id) {
        this();
        this.id = id;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @ModelElement(type = ModelElementType.HIDDEN)
    public Geometry getGeometry() {
        return geometry;
    }

    @ModelElement(type = ModelElementType.HIDDEN)
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    protected List convertCoordinateToList(Coordinate coordinate) {
        return Arrays.asList(coordinate.x, coordinate.y);
    }

    protected List convertCoordinatesToList(Coordinate[] coordinates) {
        return Arrays.stream(coordinates).map(this::convertCoordinateToList).collect(Collectors.toList());
    }

    protected Coordinate convertListToCoordinate(List<Number> list) {
        return new Coordinate(list.get(0).doubleValue(), list.get(1).doubleValue());
    }

    protected Coordinate[] convertListToCoordinates(List<List<Number>> list) {
        return list.stream().map(this::convertListToCoordinate).toArray(Coordinate[]::new);
    }
}
