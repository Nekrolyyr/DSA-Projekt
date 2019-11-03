package hsr.dsa.P2P;

import hsr.dsa.util.IPUtil;
import net.tomp2p.connection.PeerConnection;
import net.tomp2p.connection.PeerException;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureAdapter;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerStatusListener;
import net.tomp2p.peers.RTT;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class P2PClient {
    public OnConnectionNotEstablished onConnectionNotEstablished;

    public PeerDHT getPeerDHT() {
        return peerDHT;
    }

    private PeerDHT peerDHT;
    private OnUsernameNotValidListener onUsernameNotValidListener;
    private OnKnownPeerNotValidListener onKnownPeerNotValidListener;
    private List<OnMessageReceivedListener> onMessageReceivedListeners = new ArrayList<>();

    public void setOnUsernameNotValidListener(OnUsernameNotValidListener onUsernameNotValidListener) {
        this.onUsernameNotValidListener = onUsernameNotValidListener;
    }

    public void setOnKnownPeerNotValidListener(OnKnownPeerNotValidListener onKnownPeerNotValidListener) {
        this.onKnownPeerNotValidListener = onKnownPeerNotValidListener;
    }

    public void addOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListeners.add(onMessageReceivedListener);
    }

    public void setOnConnectionNotEstablished(OnConnectionNotEstablished onConnectionNotEstablished) {
        this.onConnectionNotEstablished = onConnectionNotEstablished;
    }

    public void connect(String Username, String IPPeer) {
        if (!IPUtil.checkIP(IPPeer)) {
            if (onKnownPeerNotValidListener != null) onKnownPeerNotValidListener.onCall();
        }
        if (Username.length() < 2) {
            if (onUsernameNotValidListener != null) onUsernameNotValidListener.onCall();
        }
        try {
            peerDHT = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(Username)).ports(4000).start()).start();
            FutureBootstrap fb = this.peerDHT.peer().bootstrap().inetAddress(InetAddress.getByName(IPPeer)).ports(4000).start();
            fb.awaitUninterruptibly();
            System.out.println("Bootstrap Success: " + fb.isSuccess());
            if (!fb.isSuccess()) {
                if (onConnectionNotEstablished != null) onConnectionNotEstablished.onCall();
            }
            peerDHT.peer().objectDataReply((peerAddress, o) -> {
                Message m = new Message((String) o);
                onMessageReceivedListeners.forEach(onMessageReceivedListener -> onMessageReceivedListener.onCall(m));
                System.out.println(m.getSender() + ": " + m.getMessage());
                return "REPLY";
            });
            peerDHT.put(peerDHT.peerID()).data(new Data(Username)).start();
            peerDHT.peerBean().addPeerStatusListener(new PeerStatusListener() {
                @Override
                public boolean peerFailed(PeerAddress peerAddress, PeerException e) {
                    System.err.println(e.toString());
                    return true;
                }

                @Override
                public boolean peerFound(PeerAddress peerAddress, PeerAddress peerAddress1, PeerConnection peerConnection, RTT rtt) {
                    FutureGet futureGet = peerDHT.get(peerAddress.peerId()).start();
                    futureGet.addListener(new BaseFutureAdapter<FutureGet>() {
                        @Override
                        public void operationComplete(FutureGet future) throws Exception {
                            if(future.isSuccess()) {
                                System.out.println(future.digest());
                            }
                        }
                    });
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Collection<PeerAddress> peers, Message message) {
        for (PeerAddress p : peers) {
            peerDHT.peer().sendDirect(p).object(message.pack()).start();
        }
    }

    public Collection<PeerAddress> discoverPeers() {
        return peerDHT.peerBean().peerMap().all();
    }

    public interface OnUsernameNotValidListener {
        void onCall();
    }

    public interface OnKnownPeerNotValidListener {
        void onCall();
    }

    public interface OnMessageReceivedListener {
        void onCall(Message message);
    }

    public interface OnConnectionNotEstablished {
        void onCall();
    }
}
