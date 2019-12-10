package hsr.dsa.core.matchmaking;

import hsr.dsa.P2P.P2PClient;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.ethereum.BlockchainHandler;
import hsr.dsa.gui.UIController;
import hsr.dsa.gui.game.BattleField;

public class Matchmaker {

    public Matchmaker() {

        //UIController ui = new UIController();
        BattleField battleField = new BattleField("", "", new P2PClient(), GameChoreographer.Type.ACTIVE, new BlockchainHandler("0x036FBAE35b84e03926Cf466C2Ef19165C66829b2", "0x1cE0089b18c8135B6fff8b10fC43F596A7289D83", "d458a482cb2d7532aab8f76994a32351d5190bc08d661636690fae7272efeaac"));


    }

}
