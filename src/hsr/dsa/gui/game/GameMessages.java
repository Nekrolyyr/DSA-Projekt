package hsr.dsa.gui.game;

import java.awt.*;

import static hsr.dsa.gui.game.UiStrings.*;
import static hsr.dsa.gui.game.UiStrings.ENEMYS_TURN_MESSAGE;

public class GameMessages {


    public void startGameMessage() {
        InfoScreen message = new InfoScreen(START_GAME_MESSAGE);
        message.showInfoScreen();
    }

    public void youHaveMissedMessage() {
        InfoScreen message = new InfoScreen(HIT_MESSAGE);
        message.showInfoScreen();
    }

    public void youHaveHittedMessage() {
        InfoScreen message = new InfoScreen(MISSED_MESSAGE);
        message.showInfoScreen();
    }

    public void showShipPlacingMessage() {
        InfoScreen placeShips = new InfoScreen(PLACE_YOUR_SHIPS_MESSAGE);
        placeShips.showInfoScreen();
    }

    public void showYourTurnMessage() {
        InfoScreen infoScreen = new InfoScreen(YOUR_TURN_MESSAGE);
        infoScreen.showInfoScreen();
    }

    public void showEnemysTurnMessage() {
        InfoScreen infoScreen = new InfoScreen(ENEMYS_TURN_MESSAGE);
        infoScreen.showInfoScreen();
    }

    public void showShipOutOfFieldMessage() {
        InfoScreen infoScreen = new InfoScreen(SHIP_OUT_OF_FIELD_MESSAGE);
        infoScreen.showInfoScreen();
    }
}
