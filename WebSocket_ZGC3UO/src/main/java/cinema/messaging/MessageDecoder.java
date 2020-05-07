package cinema.messaging;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;
import java.util.Map;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public Message decode(String message) throws DecodeException {
        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        Message result = new Message();
        result.setType(jsonObject.getString("type"));

        for (Map.Entry<String, JsonValue> property : jsonObject.entrySet()) {
            if (!property.getKey().equals("type")) {
                result.addProperty(property.getKey(), property.getValue());
            }
        }
        return result;
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void destroy() {
    }
}
