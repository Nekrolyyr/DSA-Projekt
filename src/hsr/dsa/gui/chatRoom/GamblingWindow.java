package hsr.dsa.gui.chatRoom;

import hsr.dsa.P2P.Message;
import hsr.dsa.P2P.P2PClient;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.ethereum.BlockchainHandler;
import hsr.dsa.gui.game.BattleField;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import static hsr.dsa.gui.UiConfiguration.*;
import static hsr.dsa.gui.UiStrings.*;

public class GamblingWindow {

    private JFrame gamblingWindow;

    private JPanel mainPanel;
    private JPanel nameHolderPanel; // On top, holds the names
    private JPanel gamblePanel; // In the middle, for the gambling amount
    private JPanel buttonPanel; // Bottom, for the ok button etc

    private JTextField gambleAmountInput;
    private JLabel enemysGambleOffer; // From enemy

    private JLabel localUserLabel;
    private JLabel remoteUserLabel;
    private JLabel labelBetweenUsers; // Really bad name and usage, i know...

    private JButton offerButton;
    private JButton enemysOfferButton;
    private JButton abortButton;
    private Dimension gambleFieldSize;

    private P2PClient p2pClient;
    private BlockchainHandler blockchainHandler;
    private BattleField battleField;
    private String localUser;
    private String remoteUser;
    private String localPrivateKey;
    private P2PClient.OnMessageReceivedListener mrl;

    public GamblingWindow(String localUser, String remoteUser, String localEtherAccount, String remoteEtherAccount, String localPrivateKey, double gambleamount, P2PClient p2pClient, BlockchainHandler blockchainHandler) {
        this.p2pClient = p2pClient;
        this.blockchainHandler = blockchainHandler;
        this.localUser = localUser;
        this.remoteUser = remoteUser;
        this.localPrivateKey = localPrivateKey;

        localUserLabel = generateUserLabel(localUser);
        remoteUserLabel = generateUserLabel(remoteUser);
        labelBetweenUsers = generateUserLabel("vs.");

        generateNameHolderPanel(localUser, remoteUser);

        generateGamblePanel(gambleamount);

        generateButtonPanel();

        mainPanel = new JPanel(new GridLayout(3, 1));
        mainPanel.add(nameHolderPanel);
        mainPanel.add(gamblePanel);
        mainPanel.add(buttonPanel);

        gamblingWindow = new JFrame("GamblingWindow");
        gamblingWindow.add(mainPanel);
        gamblingWindow.setSize(GAMBLING_WINDOW_SIZE);
        gamblingWindow.setResizable(false);
        gamblingWindow.setLocationRelativeTo(null);
        gamblingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        this.blockchainHandler = new BlockchainHandler(localEtherAccount, remoteEtherAccount, localPrivateKey);
        this.blockchainHandler.loadDeployedSmartContract();
        gamblingWindow.setVisible(true);
        mrl = message -> {
            try {
                if (message.getType() == Message.Type.CHALLENGE) {
                    double amount = -1;
                    try {
                        amount = Double.parseDouble(gambleAmountInput.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(gamblingWindow, "Please use only decimal numbers without floating point!");
                    }
                    if (message.isReply()) {
                        //Accepted
                        JOptionPane.showMessageDialog(null, "Your Offer was Accepted!");

                        try {
                            this.blockchainHandler.storeAmountInBlockchain(new BigDecimal(String.valueOf(amount)));
                        } catch (Exception e) {
                            System.out.println("Error occured while saving the gamble amount in the blockchain!! Stop current game!");
                            p2pClient.send(remoteUser, new Message(localUser,remoteUser, Message.ExceptionType.GAMBLING));
                            return;
                        }

                        battleField = new BattleField(localUser, remoteUser, p2pClient, GameChoreographer.Type.PASSIVE, this.blockchainHandler);
                        p2pClient.removeOnMessageReceivedListener(mrl);
                        gamblingWindow.dispose();
                    } else {
                        setGamblingAmountFromEnemy(String.valueOf(message.getGambleamount()));
                    }
                } else if (message.getType() == Message.Type.EXCEPTION && message.getEt() == Message.ExceptionType.GAMBLING) {
                    JOptionPane.showMessageDialog(null, "Peer had an error. Aborting.", "!", JOptionPane.ERROR_MESSAGE);
                    p2pClient.removeOnMessageReceivedListener(mrl);
                    gamblingWindow.dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
                p2pClient.send(remoteUser, new Message(localUser,remoteUser, Message.ExceptionType.GAMBLING));
            }
        };
        p2pClient.addOnMessageReceivedListener(mrl);
    }

    private void generateButtonPanel() {
        offerButton = new JButton(MAKE_AN_OFFER);
        offerButton.setPreferredSize(BUTTON_SIZE);
        offerButton.setBackground(GENERAL_BUTTON_COLOR);
        offerButton.addActionListener(actionEvent -> {
            try {
                double amount = Double.parseDouble(gambleAmountInput.getText());
                p2pClient.send(remoteUser, new Message(p2pClient.getUsername(),remoteUser, amount));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(gamblingWindow, "Value not valid!", "!", JOptionPane.ERROR_MESSAGE);
            }
        });
        enemysOfferButton = new JButton(ACCEPT_ENEMYS_OFFER);
        enemysOfferButton.setPreferredSize(BUTTON_SIZE);
        enemysOfferButton.setBackground(GENERAL_BUTTON_COLOR);
        enemysOfferButton.addActionListener(actionEvent -> {
            double amount = Double.parseDouble(enemysGambleOffer.getText());
            Message m = new Message(localUser, remoteUser,amount);
            m.setReply(true);
            p2pClient.send(remoteUser, m);

            try {
                this.blockchainHandler.storeAmountInBlockchain(new BigDecimal(String.valueOf(amount)));
            } catch (Exception e) {
                System.out.println("Error occured while saving the gamble amount in the blockchain!! Stop current game!");
                p2pClient.send(remoteUser, new Message(localUser, remoteUser,Message.ExceptionType.GAMBLING));
                return;
            }

            battleField = new BattleField(localUser, remoteUser, p2pClient, GameChoreographer.Type.ACTIVE, blockchainHandler);
            p2pClient.removeOnMessageReceivedListener(mrl);
            gamblingWindow.dispose();
        });

        abortButton = new JButton(ABORT);
        abortButton.setPreferredSize(BUTTON_SIZE);
        abortButton.setBackground(GENERAL_BUTTON_COLOR);
        abortButton.addActionListener(actionEvent -> {
            p2pClient.send(remoteUser, new Message(localUser,remoteUser, Message.ExceptionType.GAMBLING));
            p2pClient.removeOnMessageReceivedListener(mrl);
            gamblingWindow.dispose();
        });


        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(offerButton);
        buttonPanel.add(enemysOfferButton);
        buttonPanel.add(abortButton);
    }

    public void setGamblingAmountFromEnemy(String amount) {
        this.enemysGambleOffer.setText(amount);
    }

    private void generateGamblePanel(double initialOffer) {
        gambleFieldSize = new Dimension((int) (0.2 * GAMBLING_WINDOW_SIZE.getWidth()), 2 * WRITE_FONT.getSize());

        gambleAmountInput = new JTextField();
        gambleAmountInput.setText(initialOffer + "");
        gambleAmountInput.setPreferredSize(gambleFieldSize);
        gambleAmountInput.setFont(WRITE_FONT);
        gambleAmountInput.setHorizontalAlignment(SwingUtilities.CENTER);
        gambleAmountInput.setBorder(BorderFactory.createEmptyBorder());
        gambleAmountInput.addActionListener(actionEvent -> {
            try {
                double amount = Double.parseDouble(gambleAmountInput.getText());
                p2pClient.send(remoteUser, new Message(p2pClient.getUsername(),remoteUser, amount));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(gamblingWindow, "Value not valid!", "!", JOptionPane.ERROR_MESSAGE);
            }
        });

        enemysGambleOffer = new JLabel(initialOffer + "");
        enemysGambleOffer.setPreferredSize(gambleFieldSize);
        enemysGambleOffer.setFont(WRITE_FONT);
        enemysGambleOffer.setHorizontalAlignment(SwingUtilities.CENTER);
        enemysGambleOffer.setBorder(BorderFactory.createEmptyBorder());

        JLabel currency1 = createJLabel(CURRENCY);
        JLabel currency2 = createJLabel(CURRENCY);

        JLabel yourAmount = createJLabel(YOUR_AMOUNT);
        JLabel enemyAmount = createJLabel(ENEMY_AMOUNT);

        gamblePanel = new JPanel(new GridLayout(2, 3));
        gamblePanel.setBackground(Color.white);

        gamblePanel.add(yourAmount);
        gamblePanel.add(gambleAmountInput);
        gamblePanel.add(currency1);


        gamblePanel.add(enemyAmount);
        gamblePanel.add(enemysGambleOffer);
        gamblePanel.add(currency2);
    }

    private JLabel createJLabel(String value) {
        JLabel temp = new JLabel(value);
        temp.setPreferredSize(new Dimension((int) (0.1 * GAMBLING_WINDOW_SIZE.getWidth()), 2 * WRITE_FONT.getSize()));
        temp.setFont(WRITE_FONT);
        temp.setHorizontalAlignment(SwingConstants.CENTER);
        return temp;
    }

    private void generateNameHolderPanel(String localUser, String remoteUser) {
        localUserLabel = generateUserLabel(localUser);
        remoteUserLabel = generateUserLabel(remoteUser);
        labelBetweenUsers = generateUserLabel("vs.");

        nameHolderPanel = new JPanel(new GridLayout(1, 3));
        nameHolderPanel.setBackground(Color.WHITE);
        nameHolderPanel.add(localUserLabel);
        nameHolderPanel.add(labelBetweenUsers);
        nameHolderPanel.add(remoteUserLabel);
    }

    private JLabel generateUserLabel(String name) {
        JLabel temp = new JLabel(name);
        temp.setFont(INFO_SCREEN_FONT);
        temp.setHorizontalAlignment(SwingConstants.CENTER);
        temp.setVerticalAlignment(SwingConstants.CENTER);
        temp.setForeground(Color.red);
        return temp;
    }

    public boolean isShowing() {
        return mainPanel.isShowing();
    }
}
