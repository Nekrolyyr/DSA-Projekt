package hsr.dsa;

import java.util.Collections;

public class Launcher {
    public static void main(String[] args) throws NumberFormatException, Exception {
        ExampleSimple dns1 = new ExampleSimple(1);
        ExampleSimple dns2 = new ExampleSimple(2);
        ExampleSimple dns3 = new ExampleSimple(3);
        dns1.store("David","192.168.1.101");
        dns2.store("Nekrolyyr","192.168.1.102");
        System.out.println(dns3.get("David"));
        System.out.println(dns3.get("Nekrolyyr"));
        dns1.send(dns2.peer.peerAddress(),"Hallo Welt");
        dns1.discoverPeers().forEach(peerAddress -> System.out.println(peerAddress.inetAddress()));
    }
}
