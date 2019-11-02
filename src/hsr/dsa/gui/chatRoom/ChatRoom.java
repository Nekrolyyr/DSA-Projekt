package hsr.dsa.gui.chatRoom;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;
import hsr.dsa.P2P.PollTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

import static hsr.dsa.gui.UiConfiguration.*;
import static hsr.dsa.gui.UiStrings.*;

public class ChatRoom {

    final Random randomGenerator = new Random();

    JFrame chatRoom;
    JPanel mainPanel;
    JPanel writePanel;
    JScrollPane chatPanel;
    JPanel userPanel;
    JTextArea chatWindow;
    JTextArea userWindow;
    JTextField textInputField;
    String username;
    P2PClient p2pClient;

    GamblingWindow gamblingWindow;

    ArrayList<JButton> usersInChatRoom = new ArrayList<>();

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
                    chatWindow.append(m.getSender() + HAS_JOINED_MESSAGE + '\n');
                    chatWindow.append(CHAT_SEPARATOR);
                    newBoyJoinedChatRoom(m.getSender());
                    p2pClient.send(p2pClient.discoverPeers(), new Message(username, HAS_JOINED_RESPONSE_MESSAGE));
                } else if (m.getMessage().equals(I_WANNA_PLAY_MESSAGE)) {
                    gamblingWindow = new GamblingWindow("You", m.getSender(), p2pClient);
                } else if (m.getMessage().equals(HAS_JOINED_RESPONSE_MESSAGE)) {
                    newBoyJoinedChatRoom(m.getSender());
                }  else {
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

        for (int i = 0; i < 3; i++) {
            JButton temp = generateUserForUserPanel("User " + i);
            usersInChatRoom.add(temp);
            userPanel.add(temp);
        }
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
        p2pClient.send(p2pClient.discoverPeers(), new Message(username.getText(), HAS_JOINED_MESSAGE));

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

    private synchronized void newBoyJoinedChatRoom(String senderName) {
        for (JButton b : usersInChatRoom) {
            if (!b.getText().equals(senderName)) { // Exists in chat
                JButton temp = generateUserForUserPanel(senderName);
                usersInChatRoom.add(temp);
                userPanel.add(temp);
            }
        }
        if (usersInChatRoom.size() == 0) {
            JButton temp = generateUserForUserPanel(senderName);
            usersInChatRoom.add(temp);
            userPanel.add(temp);
        }
        for (JButton b : usersInChatRoom) {
            System.out.println("User : " + b.getText());
        }
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
            gamblingWindow = new GamblingWindow("You", userName, p2pClient);
        });
        return temp;
    }


    public void appendChatMessage(String userName, String message) {
        chatWindow.append(userName + " wrote: " + message + '\n');
        chatWindow.append(CHAT_SEPARATOR);
    }
}
