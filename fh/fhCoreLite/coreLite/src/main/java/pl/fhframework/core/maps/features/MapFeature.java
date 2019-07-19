package pl.fhframework.core.maps.features;

import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.geometry.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MapFeature implements ISnapshotEnabled, IFeature {
    protected String clientId;

    private static AtomicInteger counter = new AtomicInteger(0);

    public MapFeature() {
        this.clientId = String.valueOf(counter.incrementAndGet());
    }

    @Override
    @ModelElement(type = ModelElementType.HIDDEN)
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public List<Class<? extends IGeometry>> allowedGeometries() {
        return Arrays.asList(IPoint.class, ILineString.class, IPolygon.class, IMultiPoint.class, IMultiLineString.class, IMultiPolygon.class);
    }
}
