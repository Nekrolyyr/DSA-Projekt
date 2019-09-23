package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.core.IllegalShipCountException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {
    public static final int FIELD_SIZE = 12;
    public static final Map<Ship.Type,Integer> EXPECTED_SHIP_NO = new HashMap<Ship.Type,Integer>(){{
        put(Ship.Type.BATTLESHIP,1);
        put(Ship.Type.DESTROYER,2);
        put(Ship.Type.CORVETTE,4);
    }};
    public boolean allShipsAdded() {
        for (Ship.Type type : Ship.Type.values()){
            if(addedShips.stream().map((ship1 -> ship1.getType().equals(type))).filter(aBoolean -> aBoolean).count()<EXPECTED_SHIP_NO.get(type))return false;
        }
        return true;
    }
    public enum Shot{NULL,HIT,MISS}
    private Shot[][] shots = new Shot[FIELD_SIZE][FIELD_SIZE];
    private Ship[][] ships = new Ship[FIELD_SIZE][FIELD_SIZE];
    private List<Ship> addedShips = new ArrayList<>();

    public void addShip(Ship ship,int x, int y) throws IllegalShipCountException {
        if(addedShips.stream().map((ship1 -> ship1.equals(ship))).filter(aBoolean -> aBoolean).count()>=EXPECTED_SHIP_NO.getOrDefault(ship.getType(),-1)) throw new IllegalShipCountException();
        addedShips.add(ship);
        for(int i = 0;i<ship.getSize();i++){
            if(ship.getOrientation()== Ship.Orientation.HORIZONTALLY){
                ships[x+i][y]=ship;
            }else{
                ships[x][y+i]=ship;
            }
        }
    }

    public Shot shoot(int x, int y){
        if(ships[x][y]!=null){
            if(shots[x][y]!=Shot.HIT)ships[x][y].hit();
            shots[x][y]=Shot.HIT;
        }else{
            shots[x][y]=Shot.MISS;
        }
        return shots[x][y];
    }

}
