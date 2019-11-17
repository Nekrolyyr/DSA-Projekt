package hsr.dsa.core.game.schiffe_versenken;
import static hsr.dsa.core.game.GameConfiguration.FIELD_SIZE;

public class Player {
    public Field field;
    public Field.Shot[][] attackField = new Field.Shot[FIELD_SIZE][FIELD_SIZE];
    private String username;
    public Player(String username,Field.GameEndListener gameEndListener){
         field = new Field(gameEndListener);
         this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
