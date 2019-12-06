package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;
import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.Timer;
import hsr.dsa.gui.game.GameMessages;

import javax.swing.*;

import static hsr.dsa.core.game.GameConfiguration.TIME_PER_MOVE;

public class GameChoreographer {


    public enum Type {ACTIVE, PASSIVE}

    public enum PlayerType {LOCAL, REMOTE}

    public enum PlayStage {SETUP, PLAYING}

    public interface FieldUpdateListener {
        void onCall();
    }

    private FieldUpdateListener fieldUpdateListener;
    private Player localPlayer;
    private Player remotePlayer;
    private Type type;
    private PlayerType activePlayer;
    private PlayStage currentStage;
    private Timer.TimerListener tl;
    private Timer.TimerUpdateListener tul;
    private Timer timer;
    private P2PClient p2pClient;

    public GameChoreographer(Type type, Timer.TimerListener tl, Timer.TimerUpdateListener tul, Field.GameEndListener gel, FieldUpdateListener fieldUpdateListener, P2PClient p2pClient, String localuser, String remoteUser, GameMessages messageProvider) {
        this.type = type;
        this.tl = tl;
        this.tul = tul;
        this.p2pClient = p2pClient;
        this.fieldUpdateListener = fieldUpdateListener;
        currentStage = PlayStage.SETUP;
        localPlayer = new Player(localuser, gel);
        remotePlayer = new Player(remoteUser, null);
        p2pClient.addOnMessageReceivedListener(message -> {
            if(message.getType() == Message.Type.MOVE) {
                try {
                    Field.Shot result = remotePlayerMove(message.getMove());
                    p2pClient.send(remotePlayer.getUsername(),new Message(localPlayer.getUsername(), result, message.getMove()));
                    fieldUpdateListener.onCall();
                } catch (IllegalMoveException e) {
                    System.err.println("Remote Player made a Illegal Move");
                }
            }else if(message.getType() == Message.Type.SHOT){
                localPlayer.attackField[message.getMove().getX()][message.getMove().getY()] = message.getShot();
                timer.stop();
                if(message.getShot().equals(Field.Shot.HIT)) {
                    messageProvider.youHaveHittedMessage();
                }else{
                    messageProvider.youHaveMissedMessage();
                }
                fieldUpdateListener.onCall();
            } else if (message.getType() == Message.Type.EXCEPTION && message.getEt()== Message.ExceptionType.GAMBLING) {
                JOptionPane.showMessageDialog(null, "Peer had an error. Aborting. Please close the Window.", "!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void start() throws GameNotSetupException {
        if (!setupComplete()) throw new GameNotSetupException();
        currentStage = PlayStage.PLAYING;
        if (type == Type.ACTIVE) {
            //localPayer's Move Start
            activePlayer = PlayerType.LOCAL;
            timer = new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                    .addTimerListener(tl).addTimerListener(this::noMove).setTimerUpdateListener(tul).build();
            timer.start();
        } else {
            //remotePayer's Move Start
            activePlayer = PlayerType.REMOTE;
        }
    }

    public void rotateCalled() {
        localPlayer.field.rotateCalled();
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
        timer.stop();
    }

    private void remotePlayerFinished() {
        //localPayer's Move Start
        activePlayer = PlayerType.LOCAL;
        timer = new Timer.Builder().setSeconds(TIME_PER_MOVE).addTimerListener(this::localPlayerFinished)
                .addTimerListener(tl).addTimerListener(this::noMove).setTimerUpdateListener(tul).build();
        timer.start();
    }

    public Field.Shot remotePlayerMove(Move move) throws IllegalMoveException {
        if (activePlayer != PlayerType.REMOTE) throw new IllegalMoveException();
        remotePlayerFinished();
        if(move.getX()+move.getY() <= -1) return Field.Shot.MISS; //Enemys Time Ran out
        return localPlayer.field.shoot(move);
    }

    public void localPlayermove(Move move) throws IllegalMoveException {
        if (activePlayer != PlayerType.LOCAL) throw new IllegalMoveException();
        p2pClient.send(remotePlayer.getUsername(), new Message(localPlayer.getUsername(), move));
        localPlayerFinished();
    }

    public void addShip(int x, int y) throws IllegalShipCountException, GameNotSetupException, ShipSpotNotFreeException {
        if (currentStage != PlayStage.SETUP) throw new GameNotSetupException();
        localPlayer.field.addShip(x, y);
    }

    public Ship[][] getShipMatrix(){
        return localPlayer.field.ships;
    }

    public Field.Shot[][] getShotMatrix(){
        return localPlayer.field.shots;
    }

    public Field.Shot[][] getAttackShotMatrix(){
        return localPlayer.attackField;
    }

    public PlayerType getActivePlayer(){
        return activePlayer;
    }
}
