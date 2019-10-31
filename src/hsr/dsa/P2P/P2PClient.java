package hsr.dsa.P2P;

import hsr.dsa.util.IPUtil;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class P2PClient {
    private PeerDHT peer;
    public interface OnUsernameNotValidListener{
        void onCall();
    }
    private OnUsernameNotValidListener onUsernameNotValidListener;

    public void setOnUsernameNotValidListener(OnUsernameNotValidListener onUsernameNotValidListener) {
        this.onUsernameNotValidListener = onUsernameNotValidListener;
    }

    public interface OnKnownPeerNotValidListener{
        void onCall();
    }
    private OnKnownPeerNotValidListener onKnownPeerNotValidListener;

    public void setOnKnownPeerNotValidListener(OnKnownPeerNotValidListener onKnownPeerNotValidListener) {
        this.onKnownPeerNotValidListener = onKnownPeerNotValidListener;
    }

    public interface OnMessageReceivedListener {
        void onCall(Message message);
    }
    private List<OnMessageReceivedListener> onMessageReceivedListeners = new ArrayList<>();
    public void addOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener){
        this.onMessageReceivedListeners.add(onMessageReceivedListener);
    }

    public interface OnConnectionNotEstablished{
        void onCall();
    }
    public OnConnectionNotEstablished onConnectionNotEstablished;

    public void setOnConnectionNotEstablished(OnConnectionNotEstablished onConnectionNotEstablished) {
        this.onConnectionNotEstablished = onConnectionNotEstablished;
    }

    public void connect(String Username, String IPPeer){
        if(!IPUtil.checkIP(IPPeer)){
            if(onKnownPeerNotValidListener!=null)onKnownPeerNotValidListener.onCall();
        }
        if(Username.length()<2){
            if(onUsernameNotValidListener!=null) onUsernameNotValidListener.onCall();
        }
        try {
            peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(Username)).ports(4000).start()).start();
            FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName(IPPeer)).ports(4000).start();
            fb.awaitUninterruptibly();
            System.out.println("Bootstrap Success: "+fb.isSuccess());
            if(!fb.isSuccess()){
                if(onConnectionNotEstablished!=null)onConnectionNotEstablished.onCall();
            }
            peer.peer().objectDataReply((peerAddress, o) -> {
                Message m = new Message((String) o);
                onMessageReceivedListeners.forEach(onMessageReceivedListener -> onMessageReceivedListener.onCall(m));
                System.out.println(m.getSender()+": "+ m.getMessage());
                return "REPLY";
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Collection<PeerAddress> peers, Message message){
        for (PeerAddress p : peers) {
            peer.peer().sendDirect(p).object(message.pack()).start();
        }
    }

    public Collection<PeerAddress> discoverPeers(){
        return peer.peerBean().peerMap().all();
    }
}
