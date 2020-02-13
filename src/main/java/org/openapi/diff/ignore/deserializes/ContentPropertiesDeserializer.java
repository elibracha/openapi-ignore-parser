package org.openapi.diff.ignore.deserializes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.openapi.diff.ignore.models.ignore.ContentProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContentPropertiesDeserializer extends StdDeserializer<ContentProperties> {

    public ContentPropertiesDeserializer() {
        super(ContentProperties.class);
    }

    @Override
    public ContentProperties deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode contentPropertiesScope = jsonParser.getCodec().readTree(jsonParser);
        ContentProperties contentProperties = new ContentProperties();

        if (!contentPropertiesScope.isContainerNode()) {
            contentProperties.setIgnoreAll(true);
            return contentProperties;
        }

        List<String> props = new ArrayList<>();

        for (Iterator<Map.Entry<String, JsonNode>> it = contentPropertiesScope.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> propertiesScope = it.next();

            for (JsonNode prop : propertiesScope.getValue()) {
                props.add(prop.asText());
            }
        }

        contentProperties.setProperties(props);

        return contentProperties;
    }
}
