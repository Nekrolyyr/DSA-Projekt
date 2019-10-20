package hsr.dsa;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.tomp2p.dht.FutureDigest;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFutureAdapter;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.message.Buffer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;
public class ExampleSimple {

    public final PeerDHT peer;

    public ExampleSimple(int peerId) throws Exception {

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(4000 + peerId).start()).start();
        FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(4001).start();
        fb.awaitUninterruptibly();
        if(fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
        peer.peer().objectDataReply(new ObjectDataReply() {
            @Override
            public Object reply(PeerAddress peerAddress, Object o) throws Exception {
                System.out.println("received: " + (String) o);
                return "REPLY";
            }
        });
    }

    public String get(String name) throws ClassNotFoundException, IOException {
        FutureGet futureGet = peer.get(Number160.createHash(name)).start();
        futureGet.awaitUninterruptibly();
        if (futureGet.isSuccess()) {
            return futureGet.dataMap().values().iterator().next().object().toString();
        }
        return "not found";
    }

    public void store(String name, String ip) throws IOException {
        peer.put(Number160.createHash(name)).data(new Data(ip)).start().awaitUninterruptibly();
    }

    public void send(PeerAddress addr,String message){
        Buffer buffer = new Buffer(Unpooled.buffer(message.length()).writeBytes(message.getBytes()));
        peer.peer().sendDirect(addr).object(message).start();
    }

    public Collection<PeerAddress> discoverPeers(){
        return peer.peerBean().peerMap().all();
    }
}
