package hsr.dsa.core.game;

import java.awt.*;

public class Timer {
    private Timer(){}
    private TimerListener tl = null;
    private int time = 0;
    public interface TimerListener{
        void secondUpdate(int remainingSecond);
        void timerRunOut();
        void timerInterrupted(int remainingMillisecond);
    }
    public static class Builder{
        TimerListener tl = null;
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
                    EventQueue.invokeLater(()->tl.secondUpdate(time/1000));
                    Thread.sleep(1000);
                    time -= 1000;
                }
                EventQueue.invokeLater(()->tl.secondUpdate(time/1000));
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
