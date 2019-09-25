package hsr.dsa.core;

public class IllegalShipCountException extends Exception {
    @Override
    public String toString(){return "Too many ships of this type!\n"+super.toString();}
}
