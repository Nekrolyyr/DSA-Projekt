package gui.chatRoom;

import javax.swing.*;

import java.awt.*;
import java.util.concurrent.Flow;

import static gui.UiConfiguration.*;

public class ChatRoom {

    JFrame chatRoom;

    JPanel mainPanel;
    JPanel writePanel;
    JScrollPane chatPanel;
    JScrollPane userPanel;

    JTextArea chatWindow;

    JTextField textInputField;

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
        sendButton.addActionListener(actionEvent -> appendChatMessage("Martin", textInputField.getText()));

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
