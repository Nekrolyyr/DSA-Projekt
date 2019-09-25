package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;

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
    public interface GameEndListener{void onGameEndet();}
    public enum Shot{NULL,HIT,MISS}
    private Shot[][] shots = new Shot[FIELD_SIZE][FIELD_SIZE];
    private Ship[][] ships = new Ship[FIELD_SIZE][FIELD_SIZE];
    private List<Ship> addedShips = new ArrayList<>();
    private GameEndListener gameEndListener;

    public Field(GameEndListener gameEndListener){
        this.gameEndListener = gameEndListener;
    }

    public void addShip(Ship ship,int x, int y) throws IllegalShipCountException, ShipSpotNotFreeException {
        if(addedShips.stream().map((ship1 -> ship1.equals(ship))).filter(aBoolean -> aBoolean).count()>=EXPECTED_SHIP_NO.getOrDefault(ship.getType(),-1)) throw new IllegalShipCountException();
        if(!shipSpotFree(ship,x,y))throw new ShipSpotNotFreeException(x+" | "+y);
        addedShips.add(ship);
        for(int i = 0;i<ship.getSize();i++){
            if(ship.getOrientation()== Ship.Orientation.HORIZONTALLY){
                ships[x+i][y]=ship;
            }else{
                ships[x][y+i]=ship;
            }
        }
    }

    public boolean allShipsAdded() {
        for (Ship.Type type : Ship.Type.values()){
            if(addedShips.stream().map((ship1 -> ship1.getType().equals(type))).filter(aBoolean -> aBoolean).count()<EXPECTED_SHIP_NO.get(type))return false;
        }
        return true;
    }

    private boolean shipSpotFree(Ship ship,int x, int y) {
        for(int i=0;i<ship.getSize();i++){
            if(ship.getOrientation()== Ship.Orientation.HORIZONTALLY){
                if(ships[x+i][y] != null)return false;
            }else {
                if (ships[x][y+i] != null) return false;
            }
        }
        return true;
    }

    public Shot shoot(int x, int y){
        if(ships[x][y]!=null){
            if(shots[x][y]!=Shot.HIT)ships[x][y].hit();
            shots[x][y]=Shot.HIT;
            if(addedShips.stream().allMatch(Ship::hasSunken))gameEndListener.onGameEndet();
        }else{
            shots[x][y]=Shot.MISS;
        }
        return shots[x][y];
    }

}
