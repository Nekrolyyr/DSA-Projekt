package hsr.dsa;

import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.message.Message;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;

public class Launcher {
    public static void main(String[] args) {
        Peer peer = null;
        try {
            peer = new PeerMaker(new Number160(0xffaa)).setPorts(43001).makeAndListen();
            //peer.discover().setInetAddress(InetAddress.getLocalHost()).start();
            peer.put(Number160.createHash("David")).setData(new Data("Student")).start();
            FutureDHT f = peer.get(Number160.createHash("David")).start();
            f.await();
            System.out.println(f.getData());
        }catch(Exception e){
            e.printStackTrace();
            if(peer!=null)peer.shutdown();
        }
    }
}
