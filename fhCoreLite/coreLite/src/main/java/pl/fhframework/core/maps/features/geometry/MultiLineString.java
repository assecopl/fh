package pl.fhframework.core.maps.features.geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2019-01-23.
 */
@Data
public class MultiLineString extends MapGeometry implements IMultiLineString {

    public MultiLineString() {
        super();
    }

    @Builder(toBuilder = true)
    public MultiLineString(@Builder.ObtainVia(method = "toImplLines") @Singular List<LineString> lineStrings) {
        geometry = geometryFactory.createMultiLineString(lineStrings.stream().map(LineString::toLineString).toArray(org.locationtech.jts.geom.LineString[]::new));
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public List getCoordinates() {
        return getLines().stream().map(ILineString::getCoordinates).collect(Collectors.toList());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public void setCoordinates(List coordinates) {
        geometry = geometryFactory.createMultiLineString((org.locationtech.jts.geom.LineString[]) coordinates.stream().map(List.class::cast).
                map(cords -> geometryFactory.createLineString(convertListToCoordinates((List<List<Number>>) cords))).
                toArray(org.locationtech.jts.geom.LineString[]::new));
    }

    @Override
    public List<ILineString> getLines() {
        List<ILineString> lines = new ArrayList<>(geometry.getNumGeometries());
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            lines.add(new LineString((org.locationtech.jts.geom.LineString) geometry.getGeometryN(i)));
        }
        return lines;
    }

    @JsonIgnore
    protected List<LineString> toImplLines() {
        return (List) getLines();
    }
}
