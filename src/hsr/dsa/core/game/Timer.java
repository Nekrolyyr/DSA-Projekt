package hsr.dsa.core.game;

import java.awt.*;

public class Timer {
    private Timer(){}
    private TimerListener tl = null;
    TimerUpdateListener tul = null;
    private int time = 0;
    public interface TimerListener{
        void timerRunOut();
        default void timerInterrupted(int remainingMillisecond) {}
    }
    public interface TimerUpdateListener{
        void secondUpdate(int remainingSecond);
    }
    public static class Builder{
        TimerListener tl = null;
        TimerUpdateListener tul = null;
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
        public Builder setTimerListener(TimerListener listener){
            tl=listener;
            return this;
        }
        public Builder setTimerUpdateListener(TimerUpdateListener listener){
            tul=listener;
            return this;
        }
        public Timer build(){
            Timer t = new Timer();
            t.tl = tl;
            t.time=time;
            return t;
        }
    }
    public void start(){
        new Thread(()->{
            try {
                while (time > 1000) {
                    EventQueue.invokeLater(()->tul.secondUpdate(time/1000));
                    Thread.sleep(1000);
                    time -= 1000;
                }
                EventQueue.invokeLater(()->tul.secondUpdate(time/1000));
                while (time > 0) {
                    Thread.sleep(10);
                    time -= 10;
                }
            }catch(InterruptedException ie){
                EventQueue.invokeLater(()->tl.timerInterrupted(time));
            }
            tl.timerRunOut();
        }).start();
    }
}
