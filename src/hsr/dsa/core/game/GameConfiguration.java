package hsr.dsa.core.game;

import hsr.dsa.core.game.schiffe_versenken.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameConfiguration {

    public static final int FIELD_SIZE = 10;

    public static final int TIME_PER_MOVE = 15;

    public static final Ship.Type[] SHIPS = {Ship.Type.CORVETTE, Ship.Type.CORVETTE, Ship.Type.DESTROYER, Ship.Type.BATTLESHIP};
    public static final int NUMBER_OF_SHIPS = SHIPS.length;

}
