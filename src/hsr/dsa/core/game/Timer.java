package hsr.dsa.core.game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Timer {
    private Timer(){}
    private List<TimerListener> tl = null;
    TimerUpdateListener tul;
    private int time = 0;
    public interface TimerListener{
        void timerRunOut();
        default void timerInterrupted(int remainingMillisecond) {}
    }
    public interface TimerUpdateListener{
        void secondUpdate(int remainingSecond);
    }
    public static class Builder{
        List<TimerListener> tl = new ArrayList<>();
        TimerUpdateListener tul;
        int time = 0;
        public Builder setSeconds(int seconds){
            time = 1000*seconds;
            return this;
        }
        public Builder setMilliseconds(int milliseconds){
            time = milliseconds;
            return this;
        }
        public Builder setTimer(int seconds, int milliseconds){
            time = milliseconds + 1000*seconds;
            return this;
        }
        public Builder addTimerListener(TimerListener listener){
            tl.add(listener);
            return this;
        }
        public Builder setTimerUpdateListener(TimerUpdateListener listener){
            tul=listener;
            return this;
        }
        public Timer build(){
            Timer t = new Timer();
            t.tl = tl;
            t.tul = tul;
            t.time=time;
            return t;
        }
    }
    public void start(){
        new Thread(()->{
            try {
                while (time > 1000) {
                    SwingUtilities.invokeLater(()->tul.secondUpdate(time/1000));
                    Thread.sleep(1000);
                    time -= 1000;
                }
                SwingUtilities.invokeLater(()->tul.secondUpdate(time/1000));
                while (time > 0) {
                    Thread.sleep(10);
                    time -= 10;
                }
            }catch(InterruptedException ie){
                SwingUtilities.invokeLater(()->{for (TimerListener timerListener : tl) {timerListener.timerInterrupted(time);}});
            }
            SwingUtilities.invokeLater(()->{for (TimerListener timerListener : tl) {timerListener.timerRunOut();}});
        }).start();
    }
}
