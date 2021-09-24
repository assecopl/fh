package pl.fhframework.core.maps.features.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.IFeatureCollection;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.helper.AutowireHelper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by pawel.ruta on 2019-01-15.
 */
public class FeatureCollectionDeserializer extends StdDeserializer<IFeatureCollection> implements ContextualDeserializer {
    private AtomicReference<FeatureClassRegistry> classRegistry = new AtomicReference<>();

    public FeatureCollectionDeserializer() {
        super(IFeatureCollection.class);
    }

    protected FeatureCollectionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IFeatureCollection deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        if (handledType() == null) {
            throw new FhException("Unknown CollectionFeature class");
        }
        ObjectNode node = (ObjectNode) JsonUtil.readNode(jsonParser);

        ArrayNode features = (ArrayNode) node.remove("features");

        IFeatureCollection featureCollection = JsonUtil.readTree(node, (Class<? extends IFeatureCollection>) handledType());
        FeatureClassDiscriminator classDiscriminator = handledType().getAnnotation(FeatureClassDiscriminator.class);

        if (features != null) {
            deserializeFeatures(featureCollection, features, classDiscriminator, jsonParser.getCodec());
        }

        return featureCollection;
    }

    private void deserializeFeatures(IFeatureCollection featureCollection, ArrayNode features, FeatureClassDiscriminator classDiscriminator, ObjectCodec objectMapper) throws JsonProcessingException {
        for (int i = 0; i < features.size(); i++) {
            JsonNode featureNode = features.get(i);
            Class<? extends IFeature> featureClass = getFeatureClass(featureNode, classDiscriminator);
            if (featureClass == null) {
                throw new FhException(String.format("Unknown Feature class for: %s", featureNode.toString()));
            }
            IFeature feature = objectMapper.treeToValue(featureNode, featureClass);
            featureCollection.addFeature(feature);
        }
    }

    private Class<? extends IFeature> getFeatureClass(JsonNode featureNode, FeatureClassDiscriminator classDiscriminator) {
        String geometryType = null;
        JsonNode geometry = featureNode.get("geometry");
        if (!isNull(geometry)) {
            JsonNode type = geometry.get("type");
            if (!isNull(type)) {
                geometryType = type.textValue();
            }
        }
        if (StringUtils.isNullOrEmpty(geometryType)) {
            throw new FhException("Unknown geometry type for Feature class");
        }
        if (classDiscriminator != null) {
            if (!StringUtils.isNullOrEmpty(classDiscriminator.featureClass())) {
                return getClassRegistry().getFeatureClass(classDiscriminator.featureClass());
            }
            if (!StringUtils.isNullOrEmpty(classDiscriminator.classDiscriminatorKey())) {
                JsonNode properties = featureNode.get("properties");
                if (!isNull(properties)) {
                    JsonNode discriminator = properties.get(classDiscriminator.classDiscriminatorKey());
                    if (!isNull(discriminator)) {
                        Class<? extends IFeature> clazz = getClassRegistry().getFeatureClass(discriminator.textValue());
                        if (clazz != null) {
                            return clazz;
                        }
                    }
                }
            }
        }
        return getClassRegistry().getFeatureClass(geometryType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            return this;
        }
        return new FeatureCollectionDeserializer(ctxt.getContextualType().getRawClass());
    }

    private FeatureClassRegistry getClassRegistry() {
        if (classRegistry.get() == null) {
            classRegistry.set(AutowireHelper.getBean(FeatureClassRegistry.class));
        }

        return classRegistry.get();
    }

    private boolean isNull(JsonNode node) {
        return node == null || node instanceof NullNode;
    }
}
