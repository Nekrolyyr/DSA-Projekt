package hsr.dsa.P2P;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Message {
    Sender sender;
    String message;

    public Message(String senderName, String message) {
        this.sender = new Sender(senderName);
        this.message = message;
    }

    public Message(String JSONString) {
        try {
            JsonObject _package = new Gson().fromJson(JSONString, JsonObject.class);
            sender.setName(_package.get("sender").getAsString());
            message = _package.get("message").getAsString();
        } catch (Exception e) {
            System.out.println("Message Illegible: " + JSONString);
        }
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(String senderName) {
        this.sender.setName(senderName);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String pack() {
        JsonObject _package = new JsonObject();
        _package.addProperty("sender", sender.getName());
        _package.addProperty("message", message);
        System.out.println(_package.toString());
        return _package.toString();
    }

}
