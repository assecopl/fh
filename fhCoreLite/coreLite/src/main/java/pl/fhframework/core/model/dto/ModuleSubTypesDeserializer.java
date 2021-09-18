package pl.fhframework.core.model.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase;
import pl.fhframework.core.util.DynamicTypeDeserializerUtils;
import pl.fhframework.ReflectionUtils;

import java.io.IOException;

/**
 * Created by pawel.ruta on 2018-11-15.
 */
public class ModuleSubTypesDeserializer extends StdDeserializer {
    protected ModuleSubTypesDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        throw new UnsupportedOperationException("Only subtypes deserialization is possible with ModuleSubTypesDeserializer");
    }

    @Override
    public Object deserializeWithType(JsonParser jsonParser, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        return mapper.treeToValue(node, DynamicTypeDeserializerUtils.getSubType(ReflectionUtils.getClassForName(((TypeDeserializerBase)typeDeserializer).baseTypeName()), node.get(typeDeserializer.getPropertyName()).asText()));
    }
}
