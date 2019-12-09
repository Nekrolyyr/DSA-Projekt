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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class P2PClient {
    public OnConnectionNotEstablished onConnectionNotEstablished;

    public String getUsername() {
        return username;
    }

    private String username;

    public PeerDHT getPeerDHT() {
        return peerDHT;
    }

    private PeerDHT peerDHT;
    private OnUsernameNotValidListener onUsernameNotValidListener;
    private OnKnownPeerNotValidListener onKnownPeerNotValidListener;
    private CopyOnWriteArrayList<OnMessageReceivedListener> onMessageReceivedListeners = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<PeerAddress,String> peerMap = new ConcurrentHashMap<>();

    public interface OnPeerMapChangeListener{
        void onCall(ConcurrentHashMap<PeerAddress,String> peerMap);
    }
    private CopyOnWriteArrayList<OnPeerMapChangeListener> onPeerMapChangesListeners = new CopyOnWriteArrayList<>();
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

    public void removeOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListeners.remove(onMessageReceivedListener);
    }

    public void setOnConnectionNotEstablished(OnConnectionNotEstablished onConnectionNotEstablished) {
        this.onConnectionNotEstablished = onConnectionNotEstablished;
    }

    public Map<PeerAddress,String> getPeerMap(){
        return new HashMap<>(peerMap);
    }

    private void fireOnPeerMapChanged(){
        onPeerMapChangesListeners.forEach(onPeerMapChangeListener -> onPeerMapChangeListener.onCall(peerMap));
    }

    public void connect(String Username, String IPPeer) {
        if (!IPUtil.checkIP(IPPeer)) {
            if (onKnownPeerNotValidListener != null) onKnownPeerNotValidListener.onCall();
        }
        if (Username.length() < 2) {
            if (onUsernameNotValidListener != null) onUsernameNotValidListener.onCall();
        }
        // TODO: Ether Account valid check!
        try {
            this.username = Username;
            peerDHT = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(Username)).ports(4000).start()).start();
            FutureBootstrap fb = this.peerDHT.peer().bootstrap().inetAddress(InetAddress.getByName(IPPeer)).ports(4000).start();
            fb.awaitUninterruptibly();
            System.out.println("Bootstrap Success: " + fb.isSuccess());
            if (!fb.isSuccess()) {
                if (onConnectionNotEstablished != null) onConnectionNotEstablished.onCall();
            }
            peerDHT.peer().objectDataReply((peerAddress, o) -> {
                Message m = new Message((String) o);
                if(!m.getReceiver().equals(username)&&!m.getReceiver().equals("")){return "_";}
                if(m.getReceiver().equals("")){
                    m.setReply(true);
                    m.setReceiver(m.getSender());
                    m.setSender(username);
                    send(peerAddress, m);
                }
                if(!peerMap.containsValue(m.getSender())) discoverPeers().forEach(this::getUsernameFromPeer);
                onMessageReceivedListeners.forEach(onMessageReceivedListener -> onMessageReceivedListener.onCall(m));
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
                    System.out.println(peerMap.get(peerAddress)+" Left");
                    peerMap.remove(peerAddress);
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
                            peerMap.put(peerAddress, username);
                            System.out.println("Peername found: " + username);
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
    public void send(PeerAddress peer, Message message) {
        peerDHT.peer().sendDirect(peer).object(message.pack()).start();
    }
    public void send(String username, Message message) {
        if(peerMap.containsValue(username)) {
            PeerAddress peer = peerMap.entrySet().stream().filter(entry -> entry.getValue().equals(username)).findFirst().get().getKey();
            peerDHT.peer().sendDirect(peer).object(message.pack()).start();
        }else{
            System.err.println("Could not find Peer, sending Globally!");
            send(discoverPeers(), message);
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
