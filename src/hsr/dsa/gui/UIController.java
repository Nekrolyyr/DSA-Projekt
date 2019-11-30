package hsr.dsa.gui;

import hsr.dsa.P2P.P2PClient;
import hsr.dsa.gui.chatRoom.ChatRoom;
import hsr.dsa.gui.chatRoom.GamblingWindow;
import hsr.dsa.gui.game.BattleField;

public class UIController {

    private BattleField battleField;
    private ChatRoom chatRoom;

    public UIController() {


        //Ethereum ethereum = new Ethereum();

        chatRoom = new ChatRoom();
        //GamblingWindow gamblingWindow = new GamblingWindow("Martin", "david", "0x036FBAE35b84e03926Cf466C2Ef19165C66829b2", "0x1cE0089b18c8135B6fff8b10fC43F596A7289D83", 1, new P2PClient(), null);


    }

    private void enterPlacingPhase() {
        //battleField.showShipPlacingMessage();

    }

    private void startGame() {
        //battleField.startGameMessage();
    }

}
