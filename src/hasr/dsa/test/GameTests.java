package hasr.dsa.test;

import hsr.dsa.core.game.Timer;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    @Test
    public void testTimerRun() throws InterruptedException {
        int time = 3;
        long starTime = System.currentTimeMillis();
        new Timer.Builder().setSeconds(time).setTimerListener(() -> {assertEquals(time, (System.currentTimeMillis()-starTime)/1000);})
                .setTimerUpdateListener((i)->{assertEquals(i, time-(System.currentTimeMillis()-starTime)/1000);})
                .build().start();
        Thread.sleep(time*1000+1000);
    }
    @Test
    public void testGameChoreographer(){
        GameChoreographer local = new GameChoreographer(GameChoreographer.Type.ACTIVE);
        GameChoreographer remote = new GameChoreographer(GameChoreographer.Type.PASSIVE);
        local.start();
        remote.start();
    }
}
