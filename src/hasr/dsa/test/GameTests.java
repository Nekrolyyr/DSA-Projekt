package hasr.dsa.test;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.Timer;
import hsr.dsa.core.game.schiffe_versenken.Field;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.core.game.schiffe_versenken.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
/*
    public void testTimerRun() throws InterruptedException {
        int time = 3;
        long starTime = System.currentTimeMillis();
        new Timer.Builder().setSeconds(time).addTimerListener(() -> {assertEquals(time, (System.currentTimeMillis()-starTime)/1000);})
                .setTimerUpdateListener((i)->{assertEquals(i, time-(System.currentTimeMillis()-starTime)/1000);})
                .build().start();
        Thread.sleep(time*1000+1000);
    }
    public static void testSetup(GameChoreographer gc){
        try {
            Ship.ShipListener shipListener = ship -> System.out.println("A " + ship.getType().toString() + " has Sunken!");
            Ship s1 = new Ship(Ship.Type.BATTLESHIP, shipListener);
            gc.addShip(s1, 0, 0);

            Ship s2 = new Ship(Ship.Type.DESTROYER, shipListener);
            gc.addShip(s2, 0, 2);

            Ship s3 = new Ship(Ship.Type.DESTROYER, shipListener);
            s3.rotate();
            gc.addShip(s3, 6, 0);

            Ship s4 = new Ship(Ship.Type.CORVETTE, shipListener);
            gc.addShip(s4, 8, 4);

            Ship s5 = new Ship(Ship.Type.CORVETTE, shipListener);
            gc.addShip(s5, 8, 7);

            Ship s6 = new Ship(Ship.Type.CORVETTE, shipListener);
            s6.rotate();
            gc.addShip(s6, 10, 8);

            Ship s7 = new Ship(Ship.Type.CORVETTE, shipListener);
            s7.rotate();
            gc.addShip(s7, 11, 10);

        }catch (IllegalShipCountException e){
            e.printStackTrace();
            fail();
        }catch(ShipSpotNotFreeException e){
            e.printStackTrace();
            fail();
        }catch(GameNotSetupException e){
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void testGameChoreographer() throws IllegalMoveException, InterruptedException, GameNotSetupException {
        Timer.TimerListener localTL = ()-> System.out.println("Timer for LocalPayer run out!");
        Timer.TimerListener remoteTL = ()-> System.out.println("Timer for RemotePayer run out!");
        Timer.TimerUpdateListener localTUL = remainingSecond -> System.out.println("LocalPlayer Timer: "+remainingSecond);
        Timer.TimerUpdateListener remoteTUL = remainingSecond -> System.out.println("RemotePlayer Timer: "+remainingSecond);
        Field.GameEndListener gel = ()->System.out.println("Game has ended!");

        GameChoreographer local = new GameChoreographer(GameChoreographer.Type.ACTIVE,localTL,localTUL,gel);
        GameChoreographer remote = new GameChoreographer(GameChoreographer.Type.PASSIVE,remoteTL,remoteTUL,gel);

        testSetup(local);
        testSetup(remote);

        local.start();
        remote.start();

        local.localPlayermove(0,0,shot -> System.out.println(shot.toString()));
        System.out.println(remote.remotePlayerMove(0,0));
        remote.localPlayermove(1,0,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,0);

        local.localPlayermove(1,0,shot -> System.out.println(shot.toString()));
        System.out.println(remote.remotePlayerMove(1,0));
        remote.localPlayermove(1,0,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,0);

        local.localPlayermove(1,1,shot -> System.out.println(shot.toString()));
        System.out.println(remote.remotePlayerMove(1,1));
        remote.localPlayermove(1,0,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,0);

        local.localPlayermove(2,0,shot -> System.out.println(shot.toString()));
        System.out.println(remote.remotePlayerMove(2,0));
        remote.localPlayermove(1,0,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,0);

        local.localPlayermove(3,0,shot -> System.out.println(shot.toString()));
        System.out.println(remote.remotePlayerMove(3,0));
        remote.localPlayermove(1,0,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,0);
    }

 */
}
