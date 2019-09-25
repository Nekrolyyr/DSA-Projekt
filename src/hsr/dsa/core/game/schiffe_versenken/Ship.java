package hsr.dsa.core.game.schiffe_versenken;

import java.util.HashMap;

public class Ship {

    public enum Type{ BATTLESHIP, DESTROYER, CORVETTE}
    public enum Orientation{HORIZONTALLY,VERTICALLY}
    public interface ShipListener{void onSinking(Ship ship);}
    private static final HashMap<Type, Integer> SIZE = new HashMap<Type, Integer>(){{
        put(Type.BATTLESHIP,4);
        put(Type.DESTROYER,3);
        put(Type.CORVETTE,2);
    }};

    private Type type;
    private Orientation orientation;
    private int hits = 0;
    private ShipListener shipListener;


    public Ship(Type type,ShipListener shipListener){
        this.type = type;
        this.shipListener = shipListener;
        orientation = Orientation.HORIZONTALLY;
    }
    public void rotate(){
        if(orientation== Ship.Orientation.HORIZONTALLY){
            orientation = Ship.Orientation.VERTICALLY;
        }else{
            orientation = Ship.Orientation.HORIZONTALLY;
        }
    }
    public int getSize(){
        return SIZE.get(type);
    }
    public void hit(){
        hits++;
        if(hits>=getSize())shipListener.onSinking(this);
    }
    public Orientation getOrientation(){return orientation;}

    @Override
    public boolean equals(Object obj) {
        return this.type.equals(((Ship)obj).type);
    }

    public Ship.Type getType() {
        return type;
    }
    public boolean hasSunken(){
        return hits>=getSize();
    }

}
