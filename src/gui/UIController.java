package gui;

import gui.chatRoom.ChatRoom;
import gui.game.BattleField;

public class UIController {

    private BattleField battleField;
    private ChatRoom chatRoom;

    public UIController() {
        chatRoom = new ChatRoom();
        //battleField = new BattleField();

    }
}
