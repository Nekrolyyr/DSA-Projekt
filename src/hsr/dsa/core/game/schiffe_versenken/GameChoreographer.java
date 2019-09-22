package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.game.Timer;

public class GameChoreographer {
    private static final int TIME_PER_MOVE = 15;
    public enum Type{ACTIVE,PASSIVE}
    public enum PlayerType{LOCAL,REMOTE}
    public interface RemotePlayerMoveAnswerListener{void answer(Field.Shot shot);}
    private Player localPlayer = new Player();
    private Player remotePlayer = new Player();
    private Type type;
    private PlayerType activePlayer;
    public GameChoreographer(Type type){
        this.type = type;
    }
    public void start(){
        setup();
        if(type==Type.ACTIVE){
            //localPayer's Move Start
            activePlayer = PlayerType.LOCAL;
            new Timer.Builder().setSeconds(TIME_PER_MOVE).setTimerListener(this::localPlayerFinished).build().start();
        }else{
            //remotePayer's Move Start
            activePlayer = PlayerType.REMOTE;
        }
    }

    private void localPlayerFinished(){
        //remotePayer's Move Start
        activePlayer = PlayerType.REMOTE;
    }

    private void remotePlayerFinished(){
        //localPayer's Move Start
        activePlayer = PlayerType.LOCAL;
        new Timer.Builder().setSeconds(TIME_PER_MOVE).setTimerListener(this::localPlayerFinished).build().start();
    }

    public Field.Shot remotePlayerMove(int x,int y) throws IllegalMoveException {
        if(activePlayer!=PlayerType.REMOTE) throw new IllegalMoveException();
        return localPlayer.field.shoot(x,y);
        //remotePlayerFinished();
    }

    public void localPlayermove(int x,int y,RemotePlayerMoveAnswerListener rpmal) throws IllegalMoveException {
        if(activePlayer!=PlayerType.LOCAL) throw new IllegalMoveException();
        //TODO Transmit move

        localPlayerFinished();
    }

    private void setup() {

    }
}
