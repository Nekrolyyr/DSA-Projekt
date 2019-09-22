package hsr.dsa.core;

public class IllegalMoveException extends Exception {
    @Override
    public String toString(){return "This Move is not legal!\n"+super.toString();}
}
