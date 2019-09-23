package hsr.dsa.core;

public class GameNotSetupException extends Exception {
    @Override
    public String toString(){return "Game has not been setup correctly!\n"+super.toString();}
}
