package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestListOfObjectDeserializer extends StdDeserializer<RestListOfObjects> {

    public RestListOfObjectDeserializer() {
        super(EntityRestRequest.class);
    }

    @Override
    public RestListOfObjects deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        RestListOfObjects restListObject = new RestListOfObjects();
        if (node != null && node.get("className") != null) {
            String[] aClass = jsonParser.getCodec().treeToValue(node.get("className"), String[].class);
            if (aClass != null && aClass.length > 0) {
                try {

                    JsonNode nodeList = node.get("list");

                    if (nodeList != null && nodeList.isArray()) {
                        List list = new ArrayList();
                        restListObject.setList(list);
                        Iterator<JsonNode> iterator = nodeList.iterator();
                        int i = 0;
                        while (iterator.hasNext()) {
                            String className = aClass.length == 1 ? aClass[0] : aClass[i++];
                            list.add(jsonParser.getCodec().treeToValue(iterator.next(), Class.forName(className)));
                        }
/*
                        final int i=0;
                        node.iterator().forEachRemaining(x -> {
                            try {
                                list.add(jsonParser.getCodec().treeToValue(x, aClass[i++]));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });


                        Class<?> aClass = Class.forName(aClass);
                        restListObject.setData(jsonParser.getCodec().treeToValue(nodeList, aClass));*/
                    }
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        }
        return restListObject;
    }
}
