package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class RestObjectDeserializer extends StdDeserializer<RestObject> {

    public RestObjectDeserializer() {
        super(EntityRestRequest.class);
    }

    @Override
    public RestObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        RestObject restObject = new RestObject();
        if(node != null && node.get("className")!=null)  {
            String clazz = jsonParser.getCodec().treeToValue(node.get("className"), String.class);
            if(clazz != null) {
                try {
                    Class<?> aClass = Class.forName(clazz);
                    if(node.get("data")!=null) {
                        restObject.setData(jsonParser.getCodec().treeToValue(node.get("data"), aClass));
                    }
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return restObject;
    }
}
