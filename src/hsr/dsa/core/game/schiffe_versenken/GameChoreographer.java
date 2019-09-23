package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.game.Timer;

public class GameChoreographer {
    private static final int TIME_PER_MOVE = 15;
    public enum Type{ACTIVE,PASSIVE}
    public enum PlayerType{LOCAL,REMOTE}
    public enum PlayStage{SETUP,PLAYING}
    public interface RemotePlayerMoveAnswerListener{void answer(Field.Shot shot);}
    private Player localPlayer = new Player();
    private Player remotePlayer = new Player();
    private Type type;
    private PlayerType activePlayer;
    private PlayStage currentStage;
    private Timer.TimerListener tl;
    private Timer.TimerUpdateListener tul;
    public GameChoreographer(Type type, Timer.TimerListener tl, Timer.TimerUpdateListener tul){
        this.type = type;
        this.tl = tl;
        this.tul = tul;
        currentStage = PlayStage.SETUP;
    }
    public void start() throws GameNotSetupException {
        if(!setupComplete())throw new GameNotSetupException();
        currentStage=PlayStage.PLAYING;
        if(type==Type.ACTIVE){
            //localPayer's Move Start
            activePlayer = PlayerType.LOCAL;
            new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                    .addTimerListener(tl).setTimerUpdateListener(tul).build().start();
        }else{
            //remotePayer's Move Start
            activePlayer = PlayerType.REMOTE;
        }
    }

    private boolean setupComplete() {
        return localPlayer.field.allShipsAdded();
    }

    private void localPlayerFinished(){
        //remotePayer's Move Start
        activePlayer = PlayerType.REMOTE;
    }

    private void remotePlayerFinished(){
        //localPayer's Move Start
        activePlayer = PlayerType.LOCAL;
        new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                .addTimerListener(tl).setTimerUpdateListener(tul).build().start();
    }

    public Field.Shot remotePlayerMove(int x,int y) throws IllegalMoveException {
        if(activePlayer!=PlayerType.REMOTE) throw new IllegalMoveException();
        remotePlayerFinished();
        return localPlayer.field.shoot(x,y);
    }

    public void localPlayermove(int x,int y,RemotePlayerMoveAnswerListener rpmal) throws IllegalMoveException {
        if(activePlayer!=PlayerType.LOCAL) throw new IllegalMoveException();
        //TODO Transmit move

        localPlayerFinished();
    }
}
