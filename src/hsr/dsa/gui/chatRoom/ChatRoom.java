package hsr.dsa.gui.chatRoom;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static hsr.dsa.gui.UiConfiguration.*;
import static hsr.dsa.gui.UiStrings.CHAT_SEPARATOR;
import static hsr.dsa.gui.UiStrings.WELCOME_MESSAGE;

public class ChatRoom {

    private JFrame chatRoom;
    private JPanel mainPanel;
    private JPanel writePanel;
    private JScrollPane chatPanel;
    private JPanel userPanel;
    private JTextArea chatWindow;
    private JTextArea userWindow;
    private JTextField textInputField;
    private P2PClient p2pClient;
    private GamblingWindow gamblingWindow;
    private Object globalLock = new Object();
    private Map<String,JButton> userButtons = new HashMap<>();

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

        p2pClient = new P2PClient();
        p2pClient.setOnKnownPeerNotValidListener(() -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "IP Address is not Valid!");
                askCredentialsAndTryToConnect();
            });
        });
        p2pClient.setOnUsernameNotValidListener(() -> {
            SwingUtilities.invokeLater(() -> {
                askCredentialsAndTryToConnect();
                JOptionPane.showMessageDialog(null, "Username too short!");
            });
        });
        p2pClient.addOnMessageReceivedListener(m -> {
            SwingUtilities.invokeLater(() -> {
                synchronized (globalLock) {
                    if(m.getType() == Message.Type.CHAT) {
                        appendChatMessage(m.getSender(), m.getMessage());
                    }else if(m.getType() == Message.Type.CHALLENGE && (gamblingWindow == null || !gamblingWindow.isShowing())){
                        gamblingWindow = new GamblingWindow(p2pClient.getUsername(), m.getSender(),m.getGambleamount(), p2pClient);
                    }
                }
            });
        });
        p2pClient.setOnConnectionNotEstablished(() -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "No one seems to be here... \n Waiting for someone to connect.");
            });
        });
        p2pClient.addOnPeerMapChangeListener(peerMap -> {
            peerMap.forEach((number160,s) -> {
                if (!userButtons.containsKey(s)) {
                    JButton temp = generateUserForUserPanel(s);
                    userButtons.put(s, temp);
                    SwingUtilities.invokeLater(() -> {
                        userPanel.add(temp);
                        userPanel.updateUI();
                    });
                }
            });
            userButtons.forEach((s, jButton) -> {
                if(!peerMap.containsValue(s)){
                    userButtons.remove(s);
                    SwingUtilities.invokeLater(() -> {
                        userPanel.remove(jButton);
                        userPanel.updateUI();
                    });
                }
            });
        });
        askCredentialsAndTryToConnect();
    }

    private void askCredentialsAndTryToConnect() {
        JTextField username = new JTextField();
        JTextField knownPeer = new JTextField();
        Object[] message = {
                "Username:", username,
                "Known Peer:", knownPeer
        };
        JOptionPane.showConfirmDialog(null, message, "Please enter to Connect", JOptionPane.OK_CANCEL_OPTION);
        p2pClient.connect(username.getText(), knownPeer.getText());
        p2pClient.getPeerMap().forEach((number160, s) -> {
            JButton temp = generateUserForUserPanel(s);
            userButtons.put(s,temp);
            userPanel.add(temp);
        });
        chatWindow.append(WELCOME_MESSAGE + '\n');
        chatWindow.append(CHAT_SEPARATOR);
    }

    private void initializeWritePanel() {
        writePanel = new JPanel(new GridLayout(1, 2));
        writePanel.setBackground(Color.green);
        writePanel.setPreferredSize(new Dimension((int) CHAT_ROOM_WINDOW_SIZE.getWidth(), (int) (0.15 * CHAT_ROOM_WINDOW_SIZE.getHeight())));

        textInputField = new JTextField(3);
        textInputField.setPreferredSize(new Dimension((int) (0.8 * writePanel.getWidth()), writePanel.getHeight()));
        textInputField.setFont(WRITE_FONT);
        textInputField.setText("Write a message!");

        JButton sendButton = new JButton("Send!");
        sendButton.setPreferredSize(new Dimension((int) (0.2 * writePanel.getWidth()), writePanel.getHeight()));
        sendButton.addActionListener(actionEvent -> {
            sendMessage();
            textInputField.setText("");
        });
        textInputField.addActionListener(e -> sendMessage());

        writePanel.add(textInputField);
        writePanel.add(sendButton);
    }

    private void sendMessage() {
        appendChatMessage(p2pClient.getUsername(), textInputField.getText());
        p2pClient.send(p2pClient.discoverPeers(), new Message(p2pClient.getUsername(), new String(textInputField.getText())));
    }

    private void initializeChatPanel() {
        initializeChatWindow();
        chatPanel = new JScrollPane(chatWindow);
        chatPanel.setLayout(new ScrollPaneLayout());
        chatPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatPanel.setPreferredSize(new Dimension((int) (0.8 * CHAT_ROOM_WINDOW_SIZE.getWidth()), (int) (0.85 * CHAT_ROOM_WINDOW_SIZE.getHeight())));
    }

    private void initializeChatWindow() {
        chatWindow = new JTextArea(30, 2);
        chatWindow.setFont(CHAT_FONT);
    }

    private void initializeUserPanel() {
        userPanel = new JPanel(new FlowLayout());
        userPanel.setPreferredSize(new Dimension((int) (0.2 * CHAT_ROOM_WINDOW_SIZE.getWidth()), (int) (0.85 * CHAT_ROOM_WINDOW_SIZE.getHeight())));
        userPanel.setBackground(Color.white);
        userPanel.setEnabled(true);
        userPanel.setVisible(true);
    }

    private JButton generateUserForUserPanel(String userName) {
        JButton temp = new JButton(userName);
        temp.setPreferredSize(new Dimension((int) (0.18 * CHAT_ROOM_WINDOW_SIZE.getWidth()), (int) (0.10 * CHAT_ROOM_WINDOW_SIZE.getHeight())));
        temp.setFont(USER_WINDOW_FONT);
        temp.setBackground(Color.lightGray);
        temp.setForeground(Color.red);
        temp.setBorder(BorderFactory.createLineBorder(Color.black));
        temp.addActionListener(actionEvent -> {
            System.out.println("I challenge you, " + userName);
            p2pClient.send(userName,new Message(p2pClient.getUsername(),1));
            gamblingWindow = new GamblingWindow(p2pClient.getUsername(), userName, 1, p2pClient);
        });
        return temp;
    }


    public void appendChatMessage(String userName, String message) {
        chatWindow.append(userName + " wrote: " + message + '\n');
        chatWindow.append(CHAT_SEPARATOR);
    }
}
