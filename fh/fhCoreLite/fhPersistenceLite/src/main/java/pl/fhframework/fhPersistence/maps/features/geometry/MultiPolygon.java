package pl.fhframework.fhPersistence.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.IMultiPolygon;
import pl.fhframework.core.maps.features.geometry.IPolygon;
import pl.fhframework.core.model.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Model
@Getter
@Setter
@Entity
@DiscriminatorValue(IMultiPolygon.TYPE)
public class MultiPolygon extends PersistentGeometry implements IMultiPolygon {

    public MultiPolygon() {
        super();
    }

    @Builder(toBuilder = true)
    public MultiPolygon(@Builder.ObtainVia(method = "toImplPolygons") @Singular List<Polygon> polygons) {
        geometry = geometryFactory.createMultiPolygon(polygons.stream().map(Polygon::toPolygon).toArray(org.locationtech.jts.geom.Polygon[]::new));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List getCoordinates() {
        return getPolygons().stream().map(IPolygon::getCoordinates).collect(Collectors.toList());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        geometry = geometryFactory.createMultiPolygon((org.locationtech.jts.geom.Polygon[]) coordinates.stream().map(List.class::cast).
                map(cords -> geometryFactory.createPolygon(convertListToCoordinates((List<List<Number>>) ((List)cords).get(0)))).
                toArray(org.locationtech.jts.geom.Polygon[]::new));
    }

    @Override
    public List<IPolygon> getPolygons() {
        List<IPolygon> polygons = new ArrayList<>(geometry.getNumGeometries());
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            polygons.add(new Polygon((org.locationtech.jts.geom.Polygon) geometry.getGeometryN(i)));
        }
        return polygons;
    }

    @JsonIgnore
    protected List<Polygon> toImplPolygons() {
        return (List) getPolygons();
   }
}
