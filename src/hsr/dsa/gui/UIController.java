package hsr.dsa.gui;

import hsr.dsa.gui.chatRoom.ChatRoom;
import hsr.dsa.gui.game.BattleField;

public class UIController {

    private BattleField battleField;
    private ChatRoom chatRoom;

    public UIController() {
        //battleField = new BattleField();
        //enterPlacingPhase();
        chatRoom = new ChatRoom();

   //     battleField.showYourTurnMessage();

     //   battleField.showEnemysTurnMessage();
    }

    private void enterPlacingPhase() {
        //battleField.showShipPlacingMessage();

    }

    private void startGame() {
        //battleField.startGameMessage();
    }

}
