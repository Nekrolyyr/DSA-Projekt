package hasr.dsa.test;

import hsr.dsa.core.game.Timer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    @Test
    public void testTimerRun() throws InterruptedException {
        int time = 3;
        long starTime = System.currentTimeMillis();
        new Timer.Builder().setSeconds(time).setTimerListener(new Timer.TimerListener() {
            @Override
            public void secondUpdate(int remainingSecond) {
                System.out.println(remainingSecond);
                assertEquals(remainingSecond, time-(System.currentTimeMillis()-starTime)/1000);
            }

            @Override
            public void timerRunOut() {
                System.out.println("Finished");
                assertEquals(time, (System.currentTimeMillis()-starTime)/1000);
            }

            @Override
            public void timerInterrupted(int remainingMillisecond) {

            }
        }).build().start();
        Thread.sleep(time*1000+1000);
    }
}
