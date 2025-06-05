package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.fhPersistence.maps.features.*;
import pl.fhframework.fhPersistence.maps.features.geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IMapLiteElement extends ComponentElement {

    private String address;
    private List<String> addresses = Arrays.asList("Warszawa, ul. Branickiego 13", "Wrocław, Rynek 10");
    private IFeature newFeature = new PointFeature();
    private IFeature feature = null;
    private List<IFeature> features = new ArrayList<>();
    private IFeature selectedNewFeature = null;
    private List<IFeature> newFeatures = new ArrayList<>();

    public IMapLiteElement() {
        super();
        features.add(new PointFeature("Punkt", 1, -1));
        features.add(new PointFeature("Punkt2", 52, 21));
        features.add(new LineStringFeature("Linia", new LineString(Arrays.asList(
                new Point(15, -15),
                new Point(18, 18)
        ))));
        features.add(new PolygonFeature("Wielokąt", new Polygon(Arrays.asList(
                new Point(34.595947265625, -20.1708984375),
                new Point(38.814697265625, -35.6396484375),
                new Point(13.502197265625, -39.1552734375),
                new Point(10.689697265625, -25.0927734375),
                new Point(34.595947265625, -20.1708984375)
        ))));

        List<Point> multiPointPoints = new ArrayList<>();
        multiPointPoints.add(new Point(10, 10));
        multiPointPoints.add(new Point(11, 11));
        multiPointPoints.add(new Point(12, 12));
        MultiPoint multiPoint = new MultiPoint(multiPointPoints);
        MultiPointFeature multiPointFeature1 = new MultiPointFeature("Zbiór punktów", multiPoint);
        features.add(multiPointFeature1);

        List<Point> lineStringPoints1 = new ArrayList<>();
        lineStringPoints1.add(new Point(0, -10));
        lineStringPoints1.add(new Point(1, -12));
        lineStringPoints1.add(new Point(13, -14));
        LineString lineString1 = new LineString(lineStringPoints1);

        List<Point> lineStringPoints2 = new ArrayList<>();
        lineStringPoints2.add(new Point(3, -17));
        lineStringPoints2.add(new Point(8, -18));
        lineStringPoints2.add(new Point(9, -19));
        LineString lineString2 = new LineString(lineStringPoints2);

        List<LineString> lineStrings = new ArrayList<>();
        lineStrings.add(lineString1);
        lineStrings.add(lineString2);

        MultiLineString multiLineString = new MultiLineString(lineStrings);
        MultiLineStringFeature multiLineStringFeature = new MultiLineStringFeature("Zbiór linii", multiLineString);
        features.add(multiLineStringFeature);

        List<Point> polygonPoints1 = new ArrayList<>();
        polygonPoints1.add(new Point(-26.600646972656254, -1.0106897682409084));
        polygonPoints1.add(new Point(-8.319396972656255, -0.21972602392081342));
        polygonPoints1.add(new Point(-20.97564697265625, -11.824341483849054));
        polygonPoints1.add(new Point(-31.170959472656254, -4.872047700241922));
        polygonPoints1.add(new Point(-26.600646972656254, -1.0106897682409084));
        Polygon polygon1 = new Polygon(polygonPoints1);

        List<Point> polygonPoints2 = new ArrayList<>();
        polygonPoints2.add(new Point(-37.49633789062499, 24.36711356265127));
        polygonPoints2.add(new Point(-54.722900390624986, 11.695272733029398));
        polygonPoints2.add(new Point(-30.816650390624986, 15.453680224345831));
        polygonPoints2.add(new Point(-37.49633789062499, 24.36711356265127));
        Polygon polygon2 = new Polygon(polygonPoints2);

        List<Point> polygonPoints3 = new ArrayList<>();
        polygonPoints3.add(new Point(-38.4356689453125, 6.4026484059638875));
        polygonPoints3.add(new Point(-48.4552001953125, 3.7765593098768733));
        polygonPoints3.add(new Point(-46.17004394531249, 0.08789059053081871));
        polygonPoints3.add(new Point(-39.49035644531251, -1.6696855009865743));
        polygonPoints3.add(new Point(-35.2716064453125, -0.4394488164139716));
        polygonPoints3.add(new Point(-33.3380126953125, 2.1967272417616783));
        polygonPoints3.add(new Point(-38.4356689453125, 6.4026484059638875));
        Polygon polygon3 = new Polygon(polygonPoints3);

        List<Polygon> polygons = new ArrayList<>();
        polygons.add(polygon1);
        polygons.add(polygon2);
        polygons.add(polygon3);
        MultiPolygon multiPolygon = new MultiPolygon(polygons);
        MultiPolygonFeature multiPolygonFeature = new MultiPolygonFeature("Zbiór wielokątów", multiPolygon);
        features.add(multiPolygonFeature);

    }
}
