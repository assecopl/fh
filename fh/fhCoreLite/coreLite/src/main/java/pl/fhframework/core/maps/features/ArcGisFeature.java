package pl.fhframework.core.maps.features;

import com.esri.core.geometry.Geometry;
import lombok.Data;
import pl.fhframework.core.model.Model;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Model
public class ArcGisFeature {

    protected String id;
    protected String clientId;
    private static AtomicInteger counter = new AtomicInteger(0);
    protected Geometry geometry;
    protected Map<String, Object> attributes;

}
