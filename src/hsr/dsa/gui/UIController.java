package hsr.dsa.gui;

import hsr.dsa.ethereum.Ethereum;
import hsr.dsa.gui.chatRoom.ChatRoom;
import hsr.dsa.gui.game.BattleField;

public class UIController {

    private BattleField battleField;
    private ChatRoom chatRoom;

    public UIController() {


        Ethereum ethereum = new Ethereum();

        chatRoom = new ChatRoom();


    }

    private void enterPlacingPhase() {
        //battleField.showShipPlacingMessage();

    }

    private void startGame() {
        //battleField.startGameMessage();
    }

}
