package pl.fhframework.core.model.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import pl.fhframework.core.util.DynamicTypeDeserializerUtils;
import pl.fhframework.ReflectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel.ruta on 2018-11-15.
 */
public class MessageEventParamsDeserializer extends StdDeserializer {
    protected MessageEventParamsDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        if (node instanceof ValueNode || node instanceof ArrayNode && (node.size() == 0 || node.get(0) instanceof ValueNode)) {
            return mapper.treeToValue(node, Object.class);
        }

        if (node instanceof ObjectNode) {
            return mapper.treeToValue(node, DynamicTypeDeserializerUtils.getSubType(InMessageEventParam.class, node.get("type").asText()));
        }

        if (node instanceof ArrayNode) {
            List<InMessageEventParam> list = new ArrayList<>(node.size());
            for (int i = 0; i < node.size(); i++) {
                list.add(mapper.treeToValue(node.get(i), DynamicTypeDeserializerUtils.getSubType(InMessageEventParam.class, node.get(i).get("type").asText())));
            }
            return list;
        }

        throw new UnsupportedOperationException("Only primitives or InMessageEventParam are supported");
    }
}
