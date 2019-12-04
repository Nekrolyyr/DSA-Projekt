package hsr.dsa.gui.game;

import hsr.dsa.P2P.P2PClient;
import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.core.game.schiffe_versenken.Move;
import hsr.dsa.ethereum.BlockchainHandler;
import jdk.nashorn.internal.ir.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import static hsr.dsa.core.game.GameConfiguration.*;
import static hsr.dsa.gui.UiConfiguration.*;

public class BattleField {

    private JFrame battleField;

    private JPanel mainPanel;  // Frame that holds all the panels
    private JPanel fieldPanel;  // Holds the two battlefiels
    private JPanel yourFieldPanel;   // Field with your ships
    private JPanel enemyFieldPanel;  // Field with the enemys ships
    private JPanel shipPanel;   // Holds the ships at the bottom of the window

    private FieldButton[][] yourField;
    private FieldButton[][] enemyField;
    private GameChoreographer gameChoreographer;
    private GameMessages messageProvider;
    private JLabel infoLabel; // To show info's and the Timer
    private JLabel corvette1;
    private JLabel corvette2;
    private JLabel destroyer;
    private JLabel battleship;

    public BattleField(String localUser, String remoteUser, P2PClient p2pClient, GameChoreographer.Type initiatedByLocalPlayer, BlockchainHandler blockchainHandler) {
        infoLabel = new JLabel();

        gameChoreographer = new GameChoreographer(initiatedByLocalPlayer,
                () -> {
                    System.out.println("Time ran out!");
                    infoLabel.setText("Time ran out! Noob!");
                },
                remainingSecond -> {
                    System.out.println("You have " + remainingSecond + " seconds to make a move!");
                    infoLabel.setForeground(Color.BLACK);
                    infoLabel.setText(String.valueOf(remainingSecond));
                    if (remainingSecond <= 5) {
                        infoLabel.setForeground(Color.RED);
                    }
                },
                () -> {
                    System.out.println("Game Has ended!");
                    blockchainHandler.startTransaction();
                },
                this::renderField
                , p2pClient, localUser, remoteUser);

        JPanel namePanel = createNamePanel(gameChoreographer); // On top of the Battlefield, to show which field is yours

        fieldPanel = new JPanel(new GridLayout(1, 2));
        fieldPanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, FIELD_PANEL_HEIGHT));
        yourFieldPanel = new JPanel(new GridLayout(FIELD_SIZE, FIELD_SIZE));
        yourFieldPanel.setPreferredSize(new Dimension((int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        yourFieldPanel.setBorder(FIELD_BORDER);
        enemyFieldPanel = new JPanel(new GridLayout(FIELD_SIZE, FIELD_SIZE));
        enemyFieldPanel.setPreferredSize(new Dimension((int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        enemyFieldPanel.setBorder(FIELD_BORDER);

        yourField = generateFields(yourFieldPanel, true);
        enemyField = generateFields(enemyFieldPanel, false);

        createShipPanel(); // Must be called after generateFields();

        fieldPanel.add(yourFieldPanel);
        fieldPanel.add(enemyFieldPanel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(fieldPanel, BorderLayout.CENTER);
        mainPanel.add(shipPanel, BorderLayout.SOUTH);

        battleField = new JFrame("Battleships");
        battleField.add(mainPanel);
        battleField.setSize(BATTLEFIELD_WINDOW_SIZE);
        battleField.setResizable(false);
        battleField.setLocationRelativeTo(null);

        battleField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    System.out.println("Rotating");
                    gameChoreographer.rotateCalled();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        messageProvider = new GameMessages();
        messageProvider.showShipPlacingMessage();

        battleField.setVisible(true);
    }

    public GameChoreographer getGameChoreographer() {
        return gameChoreographer;
    }

    public JPanel getYourFieldPanel() {
        return yourFieldPanel;
    }

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public FieldButton getYourField(int xPos, int yPos) {
        return yourField[yPos][xPos];
    }

    public FieldButton getEnemyField(int xPos, int yPos) {
        return enemyField[yPos][xPos];
    }

    public GameMessages getMessageProvider() {
        return messageProvider;
    }

    private Image scaleShipImages(Image image, int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();

        return resizedImg;
    }

    private void createShipPanel() {
        Dimension corvetteSize = new Dimension((int) (BATTLEFIELD_WINDOW_SIZE.getWidth() / 5), (int) (0.15 * BATTLEFIELD_WINDOW_SIZE.getHeight()));
        Dimension destroyerSize = new Dimension((int) (BATTLEFIELD_WINDOW_SIZE.getWidth() / 5), (int) (0.10 * BATTLEFIELD_WINDOW_SIZE.getHeight()));
        Dimension battleshipSize = new Dimension((int) (BATTLEFIELD_WINDOW_SIZE.getWidth() / 5), (int) (0.075 * BATTLEFIELD_WINDOW_SIZE.getHeight()));

        ImageIcon icon = new ImageIcon("Corvette.png");
        Icon scaledIcon = new ImageIcon(scaleShipImages(icon.getImage(), (int) corvetteSize.getWidth(), (int) corvetteSize.getHeight()));
        corvette1 = new JLabel(scaledIcon);
        corvette2 = new JLabel(scaledIcon);

        icon = new ImageIcon("Destroyer.png");
        scaledIcon = new ImageIcon(scaleShipImages(icon.getImage(), (int) destroyerSize.getWidth(), (int) destroyerSize.getHeight()));
        destroyer = new JLabel(scaledIcon);
        icon = new ImageIcon("Battleship.png");
        scaledIcon = new ImageIcon(scaleShipImages(icon.getImage(), (int) battleshipSize.getWidth(), (int) battleshipSize.getHeight()));
        battleship = new JLabel(scaledIcon);

        shipPanel = new JPanel(new GridLayout(1, NUMBER_OF_SHIPS));
        shipPanel.setPreferredSize(new Dimension((int) BATTLEFIELD_WINDOW_SIZE.getWidth(), (int) (0.15 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        shipPanel.add(corvette1);
        shipPanel.add(corvette2);
        shipPanel.add(destroyer);
        shipPanel.add(battleship);
    }

    private JPanel createNamePanel(GameChoreographer gameChoreographer) {
        JLabel yourLabel = new JLabel("Your field");
        yourLabel.setFont(BATTLEFIELD_FONT);
        yourLabel.setHorizontalAlignment(JLabel.RIGHT);
        yourLabel.setVerticalAlignment(JLabel.CENTER);
        yourLabel.setBorder(TOP_MARGIN);

        infoLabel.setFont(BATTLEFIELD_FONT);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setVerticalAlignment(JLabel.CENTER);
        infoLabel.setBorder(TOP_MARGIN);

        JLabel enemyLabel = new JLabel("Enemys field");
        enemyLabel.setFont(BATTLEFIELD_FONT);
        enemyLabel.setHorizontalAlignment(JLabel.LEFT);
        enemyLabel.setVerticalAlignment(JLabel.CENTER);
        enemyLabel.setBorder(TOP_MARGIN);

        JPanel namePanel = new JPanel(new GridLayout(1, 3));
        namePanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, (int) (0.1 * FIELD_PANEL_HEIGHT)));
        namePanel.add(yourLabel);
        namePanel.add(infoLabel);
        namePanel.add(enemyLabel);
        return namePanel;
    }

    private FieldButton[][] generateFields(JPanel fields, boolean isYourField) {
        infoLabel.setForeground(Color.RED);
        infoLabel.setText("Place your " + SHIPS[0]);

        FieldButton[][] temp = new FieldButton[FIELD_SIZE][FIELD_SIZE];
        for (int y = 0; y < FIELD_SIZE; y++) {
            for (int x = 0; x < FIELD_SIZE; x++) {
                temp[x][y] = new FieldButton(x, y);
                temp[x][y].setFieldButtonClickListener((xPos, yPos) -> {
                    if (isYourField) {
                        try {
                            if (!gameChoreographer.setupComplete()) {
                                gameChoreographer.addShip(xPos, yPos);
                                renderShips();
                                if (gameChoreographer.setupComplete()) {
                                    enableGameField(yourField, false);
                                    enableGameField(enemyField, true);
                                    messageProvider.startGameMessage();
                                    gameChoreographer.start();
                                }
                            }
                        } catch (GameNotSetupException e) {
                            e.printStackTrace();
                        } catch (IllegalShipCountException e) {
                            e.printStackTrace();
                        } catch (ShipSpotNotFreeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            gameChoreographer.localPlayermove(new Move(xPos, yPos));
                        } catch (IllegalMoveException e) {
                            System.out.println("This was a Illegal Move!");
                        }
                    }
                });
                fields.add(temp[x][y]);
            }
        }
        return temp;
    }

    private void enableGameField(FieldButton[][] field, boolean enable) {
        for (int y = 0; y < FIELD_SIZE; y++) {
            for (int x = 0; x < FIELD_SIZE; x++) {
                field[x][y].setEnabled(enable);
            }
        }
    }

    private void renderField() {
        if (gameChoreographer.getActivePlayer().equals(GameChoreographer.PlayerType.LOCAL)) {
            enableGameField(enemyField, true);
        } else {
            enableGameField(enemyField, false);
        }
        for (int y = 0; y < FIELD_SIZE; y++) {
            for (int x = 0; x < FIELD_SIZE; x++) {
                yourField[x][y].setShotColoring(gameChoreographer.getShotMatrix()[x][y]);
                enemyField[x][y].setShotColoring(gameChoreographer.getAttackShotMatrix()[x][y]);
            }
        }
    }

    private void renderShips() {
        for (int y = 0; y < FIELD_SIZE; y++) {
            for (int x = 0; x < FIELD_SIZE; x++) {
                if (gameChoreographer.getShipMatrix()[x][y] != null) yourField[x][y].setShipPlacedColor();
            }
        }
    }


}
