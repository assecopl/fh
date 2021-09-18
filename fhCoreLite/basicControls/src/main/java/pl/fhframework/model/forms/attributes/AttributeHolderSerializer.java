package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class AttributeHolderSerializer extends StdSerializer<Map<Class<? extends Attribute>, Attribute>> {
    public AttributeHolderSerializer() {
        this(null);
    }

    protected AttributeHolderSerializer(Class<Map<Class<? extends Attribute>, Attribute>> t) {
        super(t);
    }

    @Override
    public void serialize(Map<Class<? extends Attribute>, Attribute> attributes, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        final Collection<Attribute> values = attributes.values();
        for (Attribute value : values) {
            jgen.writeObjectField(value.getXmlValue(), value.getValue());
        }
        jgen.writeEndObject();
    }
}
