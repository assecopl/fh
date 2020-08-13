package pl.fhframework.core.maps.features.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.GenericMapFeature;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.helper.AutowireHelper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by pawel.ruta on 2019-01-15.
 */
public class FeatureDeserializer extends StdDeserializer<IFeature> implements ContextualDeserializer {
    private AtomicReference<FeatureClassRegistry> classRegistry = new AtomicReference<>();

    public FeatureDeserializer() {
        super((Class<IFeature>) null);
    }

    protected FeatureDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IFeature deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        Class<? extends IFeature> featureType = (Class<? extends IFeature>) handledType();

        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ObjectNode node = mapper.readTree(jsonParser);

        JsonNode geometry = node.get("geometry");
        GeometryType geometryType = null;
        if (!isNull(geometry)) {
            JsonNode type = geometry.get("type");
            if (!isNull(type)) {
                geometryType = GeometryType.valueOfType(type.textValue());
            }
        }
        if (geometryType == null) {
            throw new FhException("Unknown Geometry type");
        }
        if (featureType == null) {
            if (geometryType != null) {
                featureType = getClassRegistry().getFeatureClass(geometryType.getType());
            }
            if (featureType == null) {
                throw new FhException("Unknown Feature class");
            }
        }

        JsonNode properties = node.get("properties");

        IFeature feature;
        if (GenericMapFeature.class.isAssignableFrom(featureType)) {
             feature = JsonUtil.readTree(mapper.createObjectNode(), featureType);
             if (properties != null && !(properties instanceof NullNode)) {
                 ((GenericMapFeature) feature).getProperties().putAll(JsonUtil.readTree(properties, Map.class));
             }
        }
        else {
            feature = JsonUtil.readTree((properties != null && !(properties instanceof NullNode)) ? properties : mapper.createObjectNode(), featureType);
        }

        if (!isNull(geometry)) {
            JsonNode coordinates = geometry.get("coordinates");
            if (feature.getGeometry() == null) {
                Optional<Method> geometrySetter = ReflectionUtils.findSetter(featureType, "geometry", Optional.empty());
                if (!geometrySetter.isPresent()) {
                    throw new FhException("Can't access geometry property");
                }
                ReflectionUtils.run(geometrySetter.get(), feature, ReflectionUtils.newInstance(ReflectionUtils.getClassForName(geometryType.getImplClass())));

            }
            feature.setCoordinates(JsonUtil.readTree(coordinates, List.class));
        }
        return feature;
    }

    private FeatureClassRegistry getClassRegistry() {
        if (classRegistry.get() == null) {
            classRegistry.set(AutowireHelper.getBean(FeatureClassRegistry.class));
        }

        return classRegistry.get();
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            return this;
        }
        return new FeatureDeserializer(ctxt.getContextualType().getRawClass());
    }

    private boolean isNull(JsonNode node) {
        return  node == null || node instanceof NullNode;
    }
}
