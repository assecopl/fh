package pl.fhframework.model.forms.rules.buildin;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.ArcGisFeature;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.rules.BusinessRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Changes collection of IFeatures - fhFramework model representation of GeoJSON feature into collection of ArcGis JSON objects. Remember that ArcGis JSON
 * is able to handle only one geometry type per layer.
 * Example:
 * List <IFeature> features;
 * List<JsonNode> = RULE.convertGeoJsonToEsriJson(features);
 */
@BusinessRule(categories = "esri")
public class FeatureToEsri {

    public List<ArcGisFeature> convertIFeatureToArcGisFeature(List<IFeature> features) {
        List<ArcGisFeature> convertedFeatures = new ArrayList<>();

        for (IFeature feature : features) {
            ArcGisFeature arcGisFeature = new ArcGisFeature();
            List<Geometry> geometry = this.determineGeometryType(feature);
            geometry = this.createEsriFeature(geometry, feature);
            for (int a = 0; a < geometry.size(); a++) {
                arcGisFeature.setGeometry(geometry.get(0));
                this.writeEsriAttributes(arcGisFeature, feature);
                convertedFeatures.add(arcGisFeature);
            }
        }

        return convertedFeatures;
    }

    private List<Geometry> determineGeometryType(IFeature feature) {
        switch (feature.getGeometry().getType()) {
            case "Point":
                return Collections.singletonList(new Point());
            case "LineString":
                return Collections.singletonList(new Polyline());
            case "Polygon":
                return Collections.singletonList(new Polygon());
            // TODO: Handle multi types
            case "MultiPolygon":
                return Collections.singletonList(new Polygon());
            default:
                throw new FhException("Passed geometry type is not handled");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Geometry> createEsriFeature(List<Geometry> geometry, IFeature feature) {
        // ESRI feature is able to handle only one geometry type per feature list
        switch (geometry.get(0).getType()) {
            case Polygon:
                List checkCoordinates = ((List<List>) feature.getCoordinates().get(0)).get(0);
                List<Double> startCoordinates;
                List<List<Double>> geometryCoordinates;

                if (checkCoordinates.size() > 2) {
                    List<Geometry> polygonList = new ArrayList<>();
                    startCoordinates = ((List<List<Double>>) checkCoordinates).get(0);
                    geometryCoordinates = checkCoordinates;
                    for (int i = 0; i < feature.getCoordinates().size(); i++) {
                        Polygon polygon = new Polygon();
                        drawPolygonPath(startCoordinates, geometryCoordinates, polygon);
                        polygonList.add(polygon);
                    }
                    return polygonList;
                } else {
                    Polygon polygon = new Polygon();
                    startCoordinates = checkCoordinates;
                    geometryCoordinates = (List<List<Double>>) feature.getCoordinates().get(0);
                    drawPolygonPath(startCoordinates, geometryCoordinates, polygon);
                    return Collections.singletonList(polygon);
                }
            case Polyline:
                Polyline polyline = new Polyline();
                List<Double> lineStartCoordinates = (List<Double>) feature.getCoordinates().get(0);
                Point startDrawLine = createPoint(lineStartCoordinates);
                polyline.startPath(startDrawLine);

                for (int i = 1; i < feature.getCoordinates().size(); i++) {
                    List<Double> lineCoordinates = (List<Double>) feature.getCoordinates().get(i);
                    Point nextLinePoint = createPoint(lineCoordinates);
                    polyline.lineTo(nextLinePoint);
                }
                return Collections.singletonList(polyline);
            case Point:
                List<Double> coordinates = (List<Double>) feature.getCoordinates();
                return Collections.singletonList(createPoint(coordinates));
            default:
                throw new FhException("Passed geometry type is not handled");
        }
    }

    private void drawPolygonPath(List<Double> startCoordinates, List<List<Double>> geometryCoordinates, Polygon polygon) {
        Point startPoint = createPoint(startCoordinates);
        polygon.startPath(startPoint);
        int geometriesSize = geometryCoordinates.size();

        for (int a = 1; a < geometriesSize; a++) {
            if (!(a + 1 > geometriesSize)) {
                List<Double> coordinates = geometryCoordinates.get(a);
                Point nextPoint = createPoint(coordinates);
                polygon.lineTo(nextPoint);
            } else {
                polygon.lineTo(startPoint);
            }
        }
    }

    private Point createPoint(List<Double> coordinates) {
        Point endPoint2D = new Point();
        endPoint2D.setXY(coordinates.get(0), coordinates.get(1));
        return endPoint2D;
    }

    private void writeEsriAttributes(ArcGisFeature arcGisFeature, IFeature feature) {
        Map<String, Object> attributes = feature.getProperties();
        arcGisFeature.setAttributes(attributes);

        arcGisFeature.getAttributes().put("shape_area", arcGisFeature.getGeometry().calculateArea2D());
        arcGisFeature.getAttributes().put("shape_length", arcGisFeature.getGeometry().calculateLength2D());
    }
}
