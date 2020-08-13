package pl.fhframework.fhPersistence.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.model.Model;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Model
@Entity
@Table(name = "SPATIAL_GEOMETRY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="GEOMETRY_TYPE")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SPATIAL_GEO_ID_SEQ", allocationSize = 50)
public abstract class PersistentGeometry extends BasePersistentObject implements IGeometry {
    static protected GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), CoordinateReferenceSystems.WGS84.getCrsId().getCode());

    @JsonIgnore
    @Column(name = "geometry")
    protected Geometry geometry;

    @Transient
    protected String clientId;

    private static AtomicInteger counter = new AtomicInteger(0);

    PersistentGeometry() {
        this.clientId = String.valueOf(counter.incrementAndGet());
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
