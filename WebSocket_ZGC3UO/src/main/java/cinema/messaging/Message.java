package cinema.messaging;

import java.util.HashMap;

public class Message {

    private String type;
    private HashMap<String, Object> properties;

    public Message() {
        properties = new HashMap<String, Object>();
    }

    public void setType(String newType) {
        this.type = newType;
    }

    public String getType() {
        return this.type;
    }

    public void addProperty(String propertyName, Object propertyValue) {
        this.properties.put(propertyName, propertyValue);
    }

    public HashMap<String, Object> getProperties() {
        return this.properties;
    }
}
