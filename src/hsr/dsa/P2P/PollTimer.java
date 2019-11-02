package hsr.dsa.P2P;

import hsr.dsa.gui.chatRoom.ChatRoom;
import net.tomp2p.peers.PeerAddress;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import static hsr.dsa.gui.UiStrings.WHO_ARE_YOU_MESSAGE;

public class PollTimer {

    Collection<PeerAddress> peers;


    public PollTimer() {

    }

    public void startUserDiscovery(P2PClient client, String username) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                peers = client.discoverPeers();
                client.send(peers, new Message(username, WHO_ARE_YOU_MESSAGE));
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 1, 5000);
    }

}
