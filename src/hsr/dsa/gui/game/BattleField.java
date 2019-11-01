package hsr.dsa.gui.game;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;

import javax.swing.*;
import java.awt.*;

import static hsr.dsa.core.game.GameConfiguration.NUMBER_OF_SHIPS;
import static hsr.dsa.core.game.GameConfiguration.SHIPS;
import static hsr.dsa.gui.UiConfiguration.*;

public class BattleField {

    private JFrame battleField;

    private JPanel framePanel;  // Frame that holds all the panels
    private JPanel fieldPanel;  // Holds the two battlefiels
    private JPanel yourFieldPanel;   // Field with your ships
    private JPanel enemyFieldPanel;  // Field with the enemys ships
    private JPanel shipPanel;   // Holds the ships at the bottom of the window

    private FieldButton[][] yourField;

    private ShipPlacer shipPlacer;

    private FieldButton[][] enemyField;
    private GameChoreographer gameChoreographer;
    private GameMessages messageProvider;
    private JLabel infoLabel; // To show info's and the Timer
    private JLabel corvette1;
    private JLabel corvette2;
    private JLabel destroyer;
    private JLabel battleship;

    public BattleField() {
        infoLabel = new JLabel();
        shipPlacer = new ShipPlacer(this);

        gameChoreographer = new GameChoreographer(GameChoreographer.Type.ACTIVE,
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
                () -> System.out.println("Game Has ended!"));
        //GameTests.testSetup(gameChoreographer);

        JPanel namePanel = createNamePanel(gameChoreographer); // On top of the Battlefield, to show which field is yours

        fieldPanel = new JPanel(new GridLayout(1, 2));
        fieldPanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, FIELD_PANEL_HEIGHT));
        yourFieldPanel = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        yourFieldPanel.setPreferredSize(new Dimension((int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        yourFieldPanel.setBorder(FIELD_BORDER);
        enemyFieldPanel = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        enemyFieldPanel.setPreferredSize(new Dimension((int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int) (0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        enemyFieldPanel.setBorder(FIELD_BORDER);


        GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener = shot -> {

        };

        yourField = generateFields(yourFieldPanel, remotePlayerMoveAnswerListener, true);
        enemyField = generateFields(enemyFieldPanel, remotePlayerMoveAnswerListener, false);

        createShipPanel(); // Must be called after generateFields();

        fieldPanel.add(yourFieldPanel);
        fieldPanel.add(enemyFieldPanel);

        framePanel = new JPanel(new BorderLayout());
        framePanel.add(namePanel, BorderLayout.NORTH);
        framePanel.add(fieldPanel, BorderLayout.CENTER);
        framePanel.add(shipPanel, BorderLayout.SOUTH);

        battleField = new JFrame("Battleships");
        battleField.add(framePanel);
        battleField.setSize(BATTLEFIELD_WINDOW_SIZE);
        battleField.setResizable(false);
        battleField.setLocationRelativeTo(null);
        battleField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        battleField.setVisible(true);

        messageProvider = new GameMessages();
        messageProvider.showShipPlacingMessage();
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

    private void createShipPanel() {
        Icon icon = new ImageIcon("Corvette.png");
        corvette1 = new JLabel(icon);
        corvette2 = new JLabel(icon);
        icon = new ImageIcon("Destroyer.png");
        destroyer = new JLabel(icon);
        icon = new ImageIcon("Battleship.png");
        battleship = new JLabel(icon);

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

    private FieldButton[][] generateFields(JPanel fields, GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener, boolean isYourField) {
        infoLabel.setForeground(Color.RED);
        infoLabel.setText("Place your " + SHIPS[0]);

        FieldButton[][] temp = new FieldButton[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                temp[y][x] = new FieldButton(x, y, gameChoreographer);
                if (isYourField) {
                    temp[y][x].setFieldButtonClickListener((xPos, yPos) -> {
                        try {
                            if (!gameChoreographer.setupComplete()) {
                                shipPlacer.placeShip(xPos, yPos);
                                if (gameChoreographer.setupComplete()) {
                                    enableGameField(yourField, false);
                                    enableGameField(enemyField, true);
                                    messageProvider.startGameMessage();
                                    gameChoreographer.start();
                                }
                            } else {
                                gameChoreographer.localPlayermove(xPos, yPos, remotePlayerMoveAnswerListener);
                            }
                        } catch (IllegalMoveException e) {
                            System.out.println("This was a Illegal Move!");
                        } catch (IllegalShipCountException e) {
                            e.printStackTrace();
                        } catch (GameNotSetupException e) {
                            e.printStackTrace();
                        } catch (ShipSpotNotFreeException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    temp[y][x].setEnabled(false);
                }
                fields.add(temp[y][x]);
            }
        }
        return temp;
    }

    private void enableGameField(FieldButton[][] field, boolean enable) {
        // Make your field unclickable
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                field[y][x].setEnabled(enable);
            }
        }
    }


}
