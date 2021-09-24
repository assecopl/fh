package pl.fhframework.core.maps.features.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.IFeatureCollection;
import pl.fhframework.core.maps.features.IGeographical;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pawel.ruta on 2019-01-14.
 */
public class FeatureSerializer extends StdSerializer<IGeographical> {
    private static Map<Class<?>, Boolean> classHasClientId = new ConcurrentHashMap<>();

    private Map<Integer, String> discriminatorContext = new ConcurrentHashMap<>();

    public FeatureSerializer() {
        super((Class<IGeographical>) null);
    }

    protected FeatureSerializer(Class<IGeographical> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(IGeographical value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        ObjectNode objectNode = JsonUtil.readObjectNode(value);

        if (value instanceof IFeatureCollection) {
            gen.writeStringField("type", value.getType());
            objectNode.remove("type");

            try {
                String discriminatorKey = FeatureClassRegistry.getFeatureClassDiscriminatorKey(value.getClass());
                if (!StringUtils.isNullOrEmpty(discriminatorKey)) {
                    discriminatorContext.put(System.identityHashCode(gen), discriminatorKey);
                }
                gen.writeObjectField("features", ((IFeatureCollection) value).allFeatures());
            } finally {
                discriminatorContext.remove(System.identityHashCode(gen));
            }

            objectNode.remove("features");
        } else {
            IFeature feature = (IFeature) value;
            gen.writeStringField("type", value.getType());
            objectNode.remove("type");

            objectNode.remove("geometry");

            writeGeometry(feature, gen);

            if (!hasProperty(value.getClass(), "clientId", classHasClientId)) {
                objectNode.remove("clientId");
            }
            objectNode.remove("properties");

            writeProperties(feature, objectNode, gen);
        }

        gen.writeEndObject();
    }

    private void writeGeometry(IFeature value, JsonGenerator gen) throws IOException {
        if (value.getGeometry() != null) {
            gen.writeFieldName("geometry");
            gen.writeStartObject();

            gen.writeStringField("type", value.getGeometry().getType());
            gen.writeObjectField("coordinates", value.getGeometry().getCoordinates());

            gen.writeEndObject();
        }
    }

    private void writeProperties(IFeature value, ObjectNode objectNode, JsonGenerator gen) throws IOException {
        gen.writeFieldName("properties");

        gen.writeStartObject();

        if (objectNode.size() > 0) {
            String discriminatorKey = discriminatorContext.get(System.identityHashCode(gen));
            if (StringUtils.isNullOrEmpty(discriminatorKey)) {
                discriminatorKey = FeatureClassRegistry.getFeatureClassDiscriminatorKey(value.getClass());
            }
            if (!StringUtils.isNullOrEmpty(discriminatorKey)) {
                gen.writeStringField(discriminatorKey, FeatureClassRegistry.getFeatureClassName(value.getClass()));
            }
            objectNode.fields().forEachRemaining(entry -> {
                if (!isNull(entry.getValue())) {
                    try {
                        gen.writeFieldName(entry.getKey());
                        gen.writeRawValue(entry.getValue().toString());
                    } catch (IOException e) {
                        throw new FhException(e);
                    }
                }
            });
        }
        gen.writeEndObject();
    }

    private boolean isNull(JsonNode node) {
        return node == null || node instanceof NullNode;
    }

    private boolean hasProperty(Class<? extends IGeographical> aClass, String name, Map<Class<?>, Boolean> map) {
        return map.computeIfAbsent(aClass, key -> ReflectionUtils.getFields(key, JsonProperty.class).stream().filter(field -> Objects.equals(field.getAnnotation(JsonProperty.class).value(), name)).findFirst().isPresent());
    }
}
