package hsr.dsa.gui.chatRoom;

import hsr.dsa.core.Message;
import hsr.dsa.util.IPUtil;
import io.netty.buffer.Unpooled;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.message.Buffer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import static hsr.dsa.gui.UiConfiguration.*;

public class ChatRoom {

    JFrame chatRoom;

    JPanel mainPanel;
    JPanel writePanel;
    JScrollPane chatPanel;
    JScrollPane userPanel;

    JTextArea chatWindow;

    JTextField textInputField;
    PeerDHT peer;
    String username;

    public ChatRoom() {
        chatRoom = new JFrame("Chat Room");

        initializeChatPanel();

        initializeWritePanel();

        initializeUserPanel();

        mainPanel = new JPanel(new BorderLayout(BORDER_GAP, BORDER_GAP));

        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(writePanel, BorderLayout.SOUTH);
        mainPanel.add(userPanel, BorderLayout.EAST);

        chatRoom.add(mainPanel);
        chatRoom.setSize(CHAT_ROOM_WINDOW_SIZE);
        chatRoom.setResizable(false);
        chatRoom.setLocationRelativeTo(null);
        chatRoom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatRoom.setVisible(true);

        //askCredentialsAndTryToConnect();
        broadcastConnect();
    }

    private void broadcastConnect() {
        username = JOptionPane.showInputDialog(null,"Username","Please enter to Connect", JOptionPane.INFORMATION_MESSAGE);
        if(username == null || username.length()<3){
            broadcastConnect();
            return;
        }
        try {
            peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(username)).ports(4000).start()).start();
            FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(4002).start();
            fb.awaitUninterruptibly();
            if (fb.isSuccess()) {
                peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
            }
            peer.peer().objectDataReply((peerAddress, o) -> {
                Message m = (Message) o;
                appendChatMessage(m.getSender(), m.getMessage());
                return "REPLY";
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Collection<PeerAddress> peers, Message message){
        for (PeerAddress p : peers) {
            peer.peer().sendDirect(p).object(message).start();
        }
    }

    public Collection<PeerAddress> discoverPeers(){
        return peer.peerBean().peerMap().all();
    }

    private void askCredentialsAndTryToConnect() {
        JTextField username = new JTextField();
        JTextField knownPeer = new JTextField();
        Object[] message = {
                "Username:", username,
                "Known Peer:", knownPeer
        };
        JOptionPane.showConfirmDialog(null,message,"Please enter to Connect",JOptionPane.OK_CANCEL_OPTION);
        if(!IPUtil.checkIP(knownPeer.getText())){
            JOptionPane.showMessageDialog(null,"IP Address is not Valid!");
            askCredentialsAndTryToConnect();
        }
    }

    private void initializeUserPanel() {
        userPanel = new JScrollPane();
        userPanel.setLayout(new ScrollPaneLayout());
        userPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userPanel.setPreferredSize(new Dimension((int)(0.2 * CHAT_ROOM_WINDOW_SIZE.getWidth()), (int)CHAT_ROOM_WINDOW_SIZE.getHeight()));

        for (int i = 0; i < 5; i++) {
            userPanel.add(new JButton("User" + i));
        }
    }

    private void initializeWritePanel() {
        writePanel = new JPanel(new GridLayout(1, 2));
        writePanel.setBackground(Color.green);
        writePanel.setPreferredSize(new Dimension((int)CHAT_ROOM_WINDOW_SIZE.getWidth(), (int)(0.15 * CHAT_ROOM_WINDOW_SIZE.getHeight())));

        textInputField = new JTextField(3);
        textInputField.setPreferredSize(new Dimension((int)(0.8 * writePanel.getWidth()), writePanel.getHeight()));
        textInputField.setFont(WRITE_FONT);
        textInputField.setText("Write a message!");

        JButton sendButton = new JButton("Send!");
        sendButton.setPreferredSize(new Dimension((int)(0.2 * writePanel.getWidth()), writePanel.getHeight()));
        sendButton.addActionListener(actionEvent -> {
            appendChatMessage("Martin", textInputField.getText());
            send(discoverPeers(), new Message(username, textInputField.getText()));
        });

        writePanel.add(textInputField);
        writePanel.add(sendButton);
    }

    private void initializeChatPanel() {
        initializeChatWindow();

        chatPanel = new JScrollPane(chatWindow);
        chatPanel.setLayout(new ScrollPaneLayout());
        chatPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatPanel.setBackground(Color.red);
    }

    private void initializeChatWindow() {
        chatWindow = new JTextArea(30, 2);
        chatWindow.setFont(CHAT_FONT);
    }

    public void appendChatMessage(String userName, String message) {
        chatWindow.append(userName + " wrote: " + message + '\n');
        chatWindow.append("-----------------------------------------------------------------\n");
    }
}
