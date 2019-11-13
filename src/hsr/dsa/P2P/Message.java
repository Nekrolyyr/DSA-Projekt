package hsr.dsa.P2P;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hsr.dsa.core.game.schiffe_versenken.Field;
import hsr.dsa.core.game.schiffe_versenken.Move;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Message{
    private String sender;
    private String message;
    private Type type;
    private Move move;
    private Field.Shot shot;
    private String pk;
    private double gambleamount;

    public Field.Shot getShot() {
        return shot;
    }

    public enum Type{CHAT,CHALLENGE,MOVE,SHOT,PK_EXCHANGE}

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.type = Type.CHAT;
    }

    public Message(String sender, double gambleamount){
        this.sender = sender;
        this.gambleamount = gambleamount;
        this.type = Type.CHALLENGE;
    }

    public Message(String sender, Move move){
        this.sender = sender;
        this.move = move;
        this.type = Type.MOVE;
    }
    public Message(String sender, Field.Shot shot){
        this.sender = sender;
        this.shot = shot;
        this.type = Type.MOVE;
    }
    public Message(String sender, String pk, Type pk_Exchange){
        this.sender = sender;
        this.pk = pk;
        this.type = Type.PK_EXCHANGE;
    }

    public Message(String JSONString){
        try{
            JsonObject json = new Gson().fromJson(JSONString, JsonObject.class);
            type = Type.valueOf(json.get("type").getAsString());
            switch (type){
                case CHAT:
                    parseChat(json);
                    break;
                case CHALLENGE:
                    parseChallenge(json);
                    break;
                case MOVE:
                    parseMove(json);
                    break;
                case SHOT:
                    parseShot(json);
                    break;
                case PK_EXCHANGE:
                    parsePK(json);
                    break;
                default:
                    throw new NotImplementedException();
            }
        }catch (Exception e){
            System.out.println("Message Illegible: "+JSONString);
        }
    }
    private void parsePK(JsonObject json) throws  Exception{
        sender = json.get("sender").getAsString();
        pk = json.get("pk").getAsString();
    }

    private void parseChat(JsonObject json) throws Exception{
        sender = json.get("sender").getAsString();
        message = json.get("message").getAsString();
    }
    private void parseChallenge(JsonObject json) throws Exception{
        sender = json.get("sender").getAsString();
        gambleamount = json.get("gambleamount").getAsDouble();
    }
    private void parseMove(JsonObject json) throws Exception{
        sender = json.get("sender").getAsString();
        move = new Move(json.get("x").getAsInt(),json.get("y").getAsInt());
    }
    private void parseShot(JsonObject json) throws Exception{
        sender = json.get("sender").getAsString();
        shot = Field.Shot.valueOf(json.get("shot").getAsString());
    }
    public String pack(){
        JsonObject _package = new JsonObject();
        _package.addProperty("type",type.toString());
        switch (type){
            case CHAT:
                _package.addProperty("sender",sender);
                _package.addProperty("message",message);
                break;
            case CHALLENGE:
                _package.addProperty("sender",sender);
                _package.addProperty("gambleamount",gambleamount);
                break;
            case MOVE:
                _package.addProperty("sender",sender);
                _package.addProperty("x",move.getX());
                _package.addProperty("y",move.getY());
                break;
            case SHOT:
                _package.addProperty("sender",sender);
                _package.addProperty("shot",shot.toString());
            case PK_EXCHANGE:
                _package.addProperty("sender",sender);
                _package.addProperty("pk",pk);
            default:
                throw new NotImplementedException();
        }
        return _package.toString();
    }

    public void setShot(Field.Shot shot) {
        this.shot = shot;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public double getGambleamount() {
        return gambleamount;
    }

    public void setGambleamount(long gambleamount) {
        this.gambleamount = gambleamount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
