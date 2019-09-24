package hsr.dsa.core.game.schiffe_versenken;

public class Player {
    public Field field;
    public void setup(Field.GameEndListener gameEndListener){
         field = new Field(gameEndListener);
    }
}
