package hasr.dsa.test;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.game.Timer;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    @Test
    public void testTimerRun() throws InterruptedException {
        int time = 3;
        long starTime = System.currentTimeMillis();
        new Timer.Builder().setSeconds(time).addTimerListener(() -> {assertEquals(time, (System.currentTimeMillis()-starTime)/1000);})
                .setTimerUpdateListener((i)->{assertEquals(i, time-(System.currentTimeMillis()-starTime)/1000);})
                .build().start();
        Thread.sleep(time*1000+1000);
    }
    @Test
    public void testGameChoreographer() throws IllegalMoveException, InterruptedException, GameNotSetupException {
        Timer.TimerListener localTL = ()-> System.out.println("Timer for LocalPayer run out!");
        Timer.TimerListener remoteTL = ()-> System.out.println("Timer for RemotePayer run out!");
        Timer.TimerUpdateListener localTUL = remainingSecond -> System.out.println("LocalPlayer Timer: "+remainingSecond);
        Timer.TimerUpdateListener remoteTUL = remainingSecond -> System.out.println("RemotePlayer Timer: "+remainingSecond);
        GameChoreographer local = new GameChoreographer(GameChoreographer.Type.ACTIVE,localTL,localTUL);
        GameChoreographer remote = new GameChoreographer(GameChoreographer.Type.PASSIVE,remoteTL,remoteTUL);
        local.start();
        remote.start();
        //Thread.sleep(2000);
        local.localPlayermove(0,0,shot -> System.out.println(shot.toString()));
        remote.remotePlayerMove(0,0);
        //Thread.sleep(2000);
        remote.localPlayermove(1,1,shot->System.out.println(shot.toString()));
        local.remotePlayerMove(1,1);
        //Thread.sleep(16000);
    }
}
