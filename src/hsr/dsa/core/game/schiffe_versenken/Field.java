package hsr.dsa.core.game.schiffe_versenken;

import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;

import java.util.ArrayList;
import java.util.List;

import static hsr.dsa.core.game.GameConfiguration.*;

public class Field {

    public interface GameEndListener {
        void onGameEndet();
    }

    public enum Shot {NULL, HIT, MISS}

    public Shot[][] shots = new Shot[FIELD_SIZE][FIELD_SIZE];
    public Ship[][] ships = new Ship[FIELD_SIZE][FIELD_SIZE];
    private List<Ship> addedShips = new ArrayList<>();
    private GameEndListener gameEndListener;

    public Field(GameEndListener gameEndListener) {
        this.gameEndListener = gameEndListener;
    }

    public void addShip(int x, int y) throws IllegalShipCountException, ShipSpotNotFreeException {
        if (addedShips.size() >= NUMBER_OF_SHIPS)
            throw new IllegalShipCountException();
        Ship ship = getNextShip();
        if (!shipSpotFree(ship, x, y)) throw new ShipSpotNotFreeException(x + " | " + y);
        addedShips.add(ship);
        for (int i = 0; i < ship.getSize(); i++) {
            if (ship.getOrientation() == Ship.Orientation.HORIZONTALLY) {
                ships[x + i][y] = ship;
            } else {
                ships[x][y + i] = ship;
            }
        }
    }

    private Ship getNextShip() {
        return new Ship(SHIPS[addedShips.size()],null);
    }

    public boolean allShipsAdded() {
        return addedShips.size() == NUMBER_OF_SHIPS;
    }

    private boolean shipSpotFree(Ship ship, int x, int y) {
        for (int i = 0; i < ship.getSize(); i++) {
            if (ship.getOrientation() == Ship.Orientation.HORIZONTALLY) {
                if (ships[x + i][y] != null) return false;
            } else {
                if (ships[x][y + i] != null) return false;
            }
        }
        return true;
    }

    public Shot shoot(Move move) {
        if (ships[move.getX()][move.getY()] != null) {
            if (shots[move.getX()][move.getY()] != Shot.HIT) ships[move.getX()][move.getY()].hit();
            shots[move.getX()][move.getY()] = Shot.HIT;
            if (addedShips.stream().allMatch(Ship::hasSunken)) gameEndListener.onGameEndet();
        } else {
            shots[move.getX()][move.getY()] = Shot.MISS;
        }
        return shots[move.getX()][move.getY()];
    }

}
