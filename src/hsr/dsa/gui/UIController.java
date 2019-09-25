package hsr.dsa.gui;

import hsr.dsa.gui.chatRoom.ChatRoom;
import hsr.dsa.gui.game.BattleField;

public class UIController {

    private BattleField battleField;
    private ChatRoom chatRoom;

    public UIController() {
        chatRoom = new ChatRoom();
        battleField = new BattleField();

    }
}
