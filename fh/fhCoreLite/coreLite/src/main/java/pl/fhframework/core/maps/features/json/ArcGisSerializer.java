package pl.fhframework.core.maps.features.json;

import com.esri.core.geometry.OperatorExportToJson;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.ArcGisFeature;
import pl.fhframework.core.util.JsonUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ArcGisSerializer extends StdSerializer<ArcGisFeature> {

    private static Map<Class<?>, Boolean> classHasClientId = new ConcurrentHashMap<>();

    protected ArcGisSerializer() {
        super((Class<ArcGisFeature>) null);
    }

    protected ArcGisSerializer(Class<ArcGisFeature> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(ArcGisFeature value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        ObjectNode objectNode = JsonUtil.readObjectNode(value);

        if (value.getGeometry() != null) {
            objectNode.remove("geometry");
            gen.writeFieldName("geometry");
            String geometryRawValue = OperatorExportToJson.local().execute(null, value.getGeometry());
            gen.writeRawValue(geometryRawValue);
        }

        if (!hasProperty(value.getClass(), classHasClientId)) {
            objectNode.remove("clientId");
        }
        writeAttributes((ObjectNode) objectNode.remove("attributes"), gen);
        gen.writeEndObject();
    }

    private void writeAttributes(ObjectNode node, JsonGenerator gen) throws IOException {
        gen.writeFieldName("attributes");
        gen.writeStartObject();
        if (node.size() > 0) {
            node.fields().forEachRemaining(val -> {
                if (!isNull(val.getValue())) {
                    try {
                        gen.writeFieldName(val.getKey());
                        gen.writeRawValue(val.getValue().toString());
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

    private boolean hasProperty(Class<? extends ArcGisFeature> aClass, Map<Class<?>, Boolean> map) {
        return map.computeIfAbsent(aClass, key -> ReflectionUtils.getFields(key, JsonProperty.class).stream().anyMatch(field -> Objects.equals(field.getAnnotation(JsonProperty.class).value(), "clientId")));
    }
}
