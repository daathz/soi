package cinema.messaging;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Map;

public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public String encode(Message message) throws EncodeException {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("type", message.getType());

        for (Map.Entry<String, Object> property : message.getProperties().entrySet()) {
            if (property.getValue() instanceof Integer) {
                Integer value = (Integer) property.getValue();
                jsonObjectBuilder.add(property.getKey(), value);
            } else if (property.getValue() instanceof String) {
                String value = (String) property.getValue();
                jsonObjectBuilder.add(property.getKey(), value);
            }
        }
        return jsonObjectBuilder.build().toString();
    }

    @Override
    public void destroy() {

    }
}
