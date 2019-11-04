package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;
import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.Timer;

import static hsr.dsa.core.game.GameConfiguration.TIME_PER_MOVE;

public class GameChoreographer {

    public enum Type {ACTIVE, PASSIVE}

    public enum PlayerType {LOCAL, REMOTE}

    public enum PlayStage {SETUP, PLAYING}

    public interface RemotePlayerMoveAnswerListener {
        void answer(Field.Shot shot);
    }

    private RemotePlayerMoveAnswerListener currentMoveAnswerListener;
    private Player localPlayer;
    private Player remotePlayer;
    private Type type;
    private PlayerType activePlayer;
    private PlayStage currentStage;
    private Timer.TimerListener tl;
    private Timer.TimerUpdateListener tul;
    private P2PClient p2pClient;

    public GameChoreographer(Type type, Timer.TimerListener tl, Timer.TimerUpdateListener tul, Field.GameEndListener gel, P2PClient p2pClient,String localuser, String remoteUser) {
        this.type = type;
        this.tl = tl;
        this.tul = tul;
        this.p2pClient = p2pClient;
        currentStage = PlayStage.SETUP;
        localPlayer = new Player(localuser, gel);
        remotePlayer = new Player(remoteUser, gel);
        p2pClient.addOnMessageReceivedListener(message -> {
            if(message.getType() == Message.Type.MOVE) {
                try {
                    Field.Shot result = remotePlayerMove(message.getMove());
                    p2pClient.send(remotePlayer.getUsername(),new Message(localPlayer.getUsername(),result));
                } catch (IllegalMoveException e) {
                    System.err.println("Remote Player made a Illegal Move");
                }
            }else if(message.getType() == Message.Type.SHOT){
                if(currentMoveAnswerListener != null){
                    currentMoveAnswerListener.answer(message.getShot());
                }else{
                    System.err.println("Receieved unwanted Move response");
                }
            }
        });

    }

    public void start() throws GameNotSetupException {
        if (!setupComplete()) throw new GameNotSetupException();
        currentStage = PlayStage.PLAYING;
        if (type == Type.ACTIVE) {
            //localPayer's Move Start
            activePlayer = PlayerType.LOCAL;
            new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                    .addTimerListener(tl).addTimerListener(this::noMove).setTimerUpdateListener(tul).build().start();
        } else {
            //remotePayer's Move Start
            activePlayer = PlayerType.REMOTE;
        }
    }

    private void noMove() {
        p2pClient.send(remotePlayer.getUsername(),new Message(localPlayer.getUsername(),new Move(-1,-1)));
    }

    public boolean setupComplete() {
        return localPlayer.field.allShipsAdded();
    }

    private void localPlayerFinished() {
        //remotePayer's Move Start
        activePlayer = PlayerType.REMOTE;
    }

    private void remotePlayerFinished() {
        //localPayer's Move Start
        activePlayer = PlayerType.LOCAL;
        new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                .addTimerListener(tl).addTimerListener(this::noMove).setTimerUpdateListener(tul).build().start();
    }

    public Field.Shot remotePlayerMove(Move move) throws IllegalMoveException {
        if (activePlayer != PlayerType.REMOTE) throw new IllegalMoveException();
        remotePlayerFinished();
        if(move.getX()+move.getY() <= -1) return Field.Shot.MISS; //Enemys Time Ran out
        return localPlayer.field.shoot(move);
    }

    public void localPlayermove(Move move, RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener) throws IllegalMoveException {
        if (activePlayer != PlayerType.LOCAL) throw new IllegalMoveException();
        p2pClient.send(remotePlayer.getUsername(), new Message(localPlayer.getUsername(), move));
        currentMoveAnswerListener = remotePlayerMoveAnswerListener;
        localPlayerFinished();
    }

    public void addShip(Ship ship, int x, int y) throws IllegalShipCountException, GameNotSetupException, ShipSpotNotFreeException {
        if (currentStage != PlayStage.SETUP) throw new GameNotSetupException();
        localPlayer.field.addShip(ship, x, y);
    }
}
