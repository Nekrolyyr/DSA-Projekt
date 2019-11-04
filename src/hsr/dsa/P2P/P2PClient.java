package hsr.dsa.P2P;

import hsr.dsa.util.IPUtil;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMapChangeListener;
import net.tomp2p.peers.PeerStatistic;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class P2PClient {
    public OnConnectionNotEstablished onConnectionNotEstablished;

    public PeerDHT getPeerDHT() {
        return peerDHT;
    }

    private PeerDHT peerDHT;
    private OnUsernameNotValidListener onUsernameNotValidListener;
    private OnKnownPeerNotValidListener onKnownPeerNotValidListener;
    private List<OnMessageReceivedListener> onMessageReceivedListeners = new ArrayList<>();

    private Map<Number160,String> peerMap = new HashMap<>();

    public interface OnPeerMapChangeListener{
        void onCall(Map<Number160,String> peerMap);
    }
    private List<OnPeerMapChangeListener> onPeerMapChangesListeners = new ArrayList<>();
    public void addOnPeerMapChangeListener(OnPeerMapChangeListener onPeerMapChangeListener){
        onPeerMapChangesListeners.add(onPeerMapChangeListener);
    }

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

    public Map<Number160,String> getPeerMap(){
        Map<Number160,String> map = new HashMap<>(peerMap);
        return map;
    }

    private void fireOnPeerMapChanged(){
        Map<Number160,String> map = new HashMap<>(peerMap);
        onPeerMapChangesListeners.forEach(onPeerMapChangeListener -> onPeerMapChangeListener.onCall(map));
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
            peerDHT.peerBean().peerMap().addPeerMapChangeListener(new PeerMapChangeListener() {
                @Override
                public void peerInserted(PeerAddress peerAddress, boolean b) {
                    getUsernameFromPeer(peerAddress);
                }

                @Override
                public void peerRemoved(PeerAddress peerAddress, PeerStatistic peerStatistic) {
                    peerMap.remove(peerAddress.peerId());
                    fireOnPeerMapChanged();
                }

                @Override
                public void peerUpdated(PeerAddress peerAddress, PeerStatistic peerStatistic) {}
            });
            discoverPeers().forEach(this::getUsernameFromPeer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUsernameFromPeer(PeerAddress peerAddress){
        FutureGet fGet = peerDHT.get(peerAddress.peerId()).start();
        fGet.addListener(new BaseFutureListener<FutureGet>() {
            @Override
            public void operationComplete(FutureGet futureGet){
                new Thread(() -> {
                    String username = null;
                    try {
                        username = futureGet.dataMap().values().iterator().next().object().toString();
                    } catch (ClassNotFoundException | IOException | NoSuchElementException e) {
                        System.out.println("Cannot find Peer Username");
                    } finally {
                        if(username!=null && !username.isEmpty()){
                            peerMap.put(peerAddress.peerId(),username);
                            System.out.println("Peername found: "+username);
                            fireOnPeerMapChanged();
                        }else {
                            System.out.println("Cannot find Peer Username "+username);
                        }
                    }
                }).start();
            }

            @Override
            public void exceptionCaught(Throwable throwable) throws Exception {}
        });
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
