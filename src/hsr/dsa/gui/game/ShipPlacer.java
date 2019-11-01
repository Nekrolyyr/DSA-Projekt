package hsr.dsa.gui.game;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.schiffe_versenken.Ship;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static hsr.dsa.core.game.GameConfiguration.NUMBER_OF_SHIPS;
import static hsr.dsa.core.game.GameConfiguration.SHIPS;
import static hsr.dsa.gui.UiConfiguration.NUMBER_OF_COLUMNS;
import static hsr.dsa.gui.UiConfiguration.NUMBER_OF_ROWS;

public class ShipPlacer {

    BattleField battleField;
    private int shipCounter = 0;
    private Ship.Type actualShip = SHIPS[shipCounter];

    public ShipPlacer(BattleField battleField) {
        this.battleField = battleField;

    }

    public void placeShip(int xPos, int yPos) throws ShipSpotNotFreeException, IllegalShipCountException, GameNotSetupException {
        Ship ship = new Ship(actualShip, ship1 -> {
            System.out.println("What ShipListener????????????????????????????");
        });
        setRightClickListener(ship);

        if (!colorAllShipFields(xPos, yPos, ship)) {
            battleField.getMessageProvider().showShipOutOfFieldMessage();
            return;
        }
        battleField.getGameChoreographer().addShip(ship, xPos, yPos);
        if (shipCounter < (NUMBER_OF_SHIPS - 1)) {
            shipCounter++;
            actualShip = SHIPS[shipCounter];
            battleField.getInfoLabel().setText("Place your " + actualShip.toString());
        }
    }

    public void setRightClickListener(Ship ship) {
        /*
            This Listener must be added earlier, here it will only be added if the
            first ship is placed. The Ship must be generated and hold in the game-
            choreographer and passed through the battlefield and this shipPlacer.
            SAM, 25.10.2019
         */
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                battleField.getYourField(x, y).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Right click!
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            System.out.println("Swap that shit!");
                            ship.swapOrientation();
                        }
                    }
                });
            }
        }
    }

    private boolean checkFieldLimits(int xPos, int yPos, int shipSize, Ship.Orientation orientation) {
        for (int i = 0; i < shipSize; i++) {
            if (orientation == Ship.Orientation.HORIZONTALLY) {
                if (xPos + i >= NUMBER_OF_COLUMNS || battleField.getYourField(xPos + i, yPos).isPartOfShip()) {
                    return false;
                }
            } else {
                if (yPos + i >= NUMBER_OF_ROWS || battleField.getEnemyField(xPos, yPos + i).isPartOfShip()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean colorAllShipFields(int xPos, int yPos, Ship ship) {
        if (!checkFieldLimits(xPos, yPos, ship.getSize(), ship.getOrientation())) {
            return false;
        }
        for (int i = 0; i < ship.getSize(); i++) {
            if (ship.getOrientation() == Ship.Orientation.HORIZONTALLY) {
                battleField.getYourField(xPos + i, yPos).setShipPlacedColor();
                battleField.getYourField(xPos + i, yPos).hasPartOfShip();
            } else {
                battleField.getYourField(xPos, yPos + i).setShipPlacedColor();
                battleField.getYourField(xPos, yPos + i).hasPartOfShip();
            }
        }
        return true;
    }
}
