package hsr.dsa.core.game.schiffe_versenken;

public class Player {
    public Field field;
    private String username;
    public Player(String username,Field.GameEndListener gameEndListener){
         field = new Field(gameEndListener);
         this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
