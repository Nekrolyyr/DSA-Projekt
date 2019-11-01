package hsr.dsa.gui.chatRoom;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static hsr.dsa.gui.UiConfiguration.*;
import static hsr.dsa.gui.UiStrings.HAS_JOINED_MESSAGE;

public class ChatRoom {
    JFrame chatRoom;
    JPanel mainPanel;
    JPanel writePanel;
    JScrollPane chatPanel;
    JScrollPane userPanel;
    JTextArea chatWindow;
    JTextField textInputField;
    String username;
    P2PClient p2pClient;

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
                if (m.getMessage().equals(HAS_JOINED_MESSAGE)) {
                    chatWindow.append(m.getSender() + HAS_JOINED_MESSAGE);
                } else {
                    appendChatMessage(m.getSender(), m.getMessage());
                }
            });
        });
        p2pClient.setOnConnectionNotEstablished(() -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "No one seems to be here... \n Waiting for someone to connect.");
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
        this.username = username.getText();
        p2pClient.connect(username.getText(), knownPeer.getText());
        newBoyJoinedChatRoom(username.getText());
    }

    private void initializeUserPanel() {
        userPanel = new JScrollPane();
        userPanel.setLayout(new ScrollPaneLayout());
        userPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userPanel.setPreferredSize(new Dimension((int) (0.2 * CHAT_ROOM_WINDOW_SIZE.getWidth()), (int) CHAT_ROOM_WINDOW_SIZE.getHeight()));

        for (int i = 0; i < 5; i++) {
            userPanel.add(new JButton("User" + i));
        }
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
        });
        textInputField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendMessage();
            }
        });

        writePanel.add(textInputField);
        writePanel.add(sendButton);
    }

    private void sendMessage() {
        appendChatMessage(username, textInputField.getText());
        p2pClient.send(p2pClient.discoverPeers(), new Message(username, textInputField.getText()));
    }

    private void newBoyJoinedChatRoom(String senderName) {
        System.out.println("New Boy Joined! " + senderName);
        p2pClient.send(p2pClient.discoverPeers(), new Message(senderName, HAS_JOINED_MESSAGE));
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
