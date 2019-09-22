package hsr.dsa.core.game.schiffe_versenken;

public class Field {
    public static final int FIELD_SIZE = 12;
    public enum Shot{NULL,HIT,MISS}
    private Shot[][] shots = new Shot[FIELD_SIZE][FIELD_SIZE];
    private Ship[][] ships = new Ship[FIELD_SIZE][FIELD_SIZE];

    public void addShip(Ship ship,int x, int y){
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
