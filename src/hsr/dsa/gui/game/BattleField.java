package hsr.dsa.gui.game;

import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.IllegalShipCountException;
import hsr.dsa.core.ShipSpotNotFreeException;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;
import hsr.dsa.core.game.schiffe_versenken.Ship;

import javax.swing.*;
import java.awt.*;

import static hsr.dsa.core.game.GameConfiguration.NUMBER_OF_SHIPS;
import static hsr.dsa.core.game.GameConfiguration.SHIPS;
import static hsr.dsa.gui.UiConfiguration.*;
import static hsr.dsa.gui.game.UiStrings.*;

public class BattleField {

    private int shipCounter = 0;

    private JFrame battleField;


    private JPanel framePanel;  // Frame that holds all the panels
    private JPanel fieldPanel;  // Holds the two battlefiels
    private JPanel yourFieldPanel;   // Field with your ships
    private JPanel enemyFieldPanel;  // Field with the enemys ships
    private JPanel shipPanel;   // Holds the ships at the bottom of the window

    private FieldButton[][] yourField;
    private FieldButton[][] enemyField;

    public GameChoreographer getGameChoreographer() {
        return gameChoreographer;
    }

    private GameChoreographer gameChoreographer;

    private JLabel timerLabel;
    private JLabel corvette1;
    private JLabel corvette2;
    private JLabel destroyer;
    private JLabel battleship;

    private Ship.Type actualShip = SHIPS[shipCounter];

    public BattleField() {

        timerLabel = new JLabel();

        gameChoreographer = new GameChoreographer(GameChoreographer.Type.ACTIVE,
                () -> {
                    System.out.println("Time ran out!");
                    timerLabel.setText("Time ran out! Noob!");
                },
                remainingSecond -> {
                    System.out.println("You have " + remainingSecond + " seconds to make a move!");
                    timerLabel.setText(String.valueOf(remainingSecond));
                    if (remainingSecond <= 5) {
                        timerLabel.setForeground(Color.RED);
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
    }

    public void startGame() {
        try {
            gameChoreographer.start();
        } catch (GameNotSetupException e) {
            System.out.println("Game was not Setup Correctly");
        }
    }


    private void createShipPanel() {
        /*Icon icon = new ImageIcon("Corvette.png");
        corvette1 = new ShipButton(Ship.Type.CORVETTE, icon);
        corvette2 = new ShipButton(Ship.Type.CORVETTE, icon);
        icon = new ImageIcon("Destroyer.png");
        destroyer = new ShipButton(Ship.Type.DESTROYER, icon);
        icon = new ImageIcon("Battleship.png");
        battleship = new ShipButton(Ship.Type.BATTLESHIP, icon);

        corvette1.setShipButtonClickListener(() -> {
            InfoScreen infoScreen = new InfoScreen("Place your " + corvette1.getName());
            infoScreen.showInfoScreen();
            actualShip = Ship.Type.CORVETTE;
        });
        corvette2.setShipButtonClickListener(() -> {
            InfoScreen infoScreen = new InfoScreen("Place your " + corvette2.getName());
            infoScreen.showInfoScreen();
            actualShip = Ship.Type.CORVETTE;
        });
        destroyer.setShipButtonClickListener(() -> {
            InfoScreen infoScreen = new InfoScreen("Place your " + destroyer.getName());
            infoScreen.showInfoScreen();
            actualShip = Ship.Type.DESTROYER;
        });
        battleship.setShipButtonClickListener(() -> {
            InfoScreen infoScreen = new InfoScreen("Place your " + battleship.getName());
            infoScreen.showInfoScreen();
            actualShip = Ship.Type.BATTLESHIP;
        });
*/
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

        timerLabel.setFont(BATTLEFIELD_FONT);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setVerticalAlignment(JLabel.CENTER);
        timerLabel.setBorder(TOP_MARGIN);

        JLabel enemyLabel = new JLabel("Enemys field");
        enemyLabel.setFont(BATTLEFIELD_FONT);
        enemyLabel.setHorizontalAlignment(JLabel.LEFT);
        enemyLabel.setVerticalAlignment(JLabel.CENTER);
        enemyLabel.setBorder(TOP_MARGIN);

        JPanel namePanel = new JPanel(new GridLayout(1, 3));
        namePanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, (int) (0.1 * FIELD_PANEL_HEIGHT)));
        namePanel.add(yourLabel);
        namePanel.add(timerLabel);
        namePanel.add(enemyLabel);
        return namePanel;
    }

    private FieldButton[][] generateFields(JPanel fields, GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener, boolean isYourField) {
        FieldButton[][] temp = new FieldButton[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                temp[y][x] = new FieldButton(x, y);
                if (isYourField) {
                    temp[y][x].setFieldButtonClickListener((xPos, yPos) -> {
                        try {
                            if (!gameChoreographer.setupComplete()) {
                                placeShip(xPos, yPos);
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


    private void placeShip(int xPos, int yPos) throws ShipSpotNotFreeException, IllegalShipCountException, GameNotSetupException {
        Ship ship = new Ship(actualShip, ship1 -> {
            System.out.println("What ShipListener????????????????????????????");
        });
        yourField[yPos][xPos].setShipPlacedColor();
        gameChoreographer.addShip(ship, xPos, yPos);
        if (shipCounter < (NUMBER_OF_SHIPS - 1)) {
            shipCounter++;
            actualShip = SHIPS[shipCounter];
            InfoScreen infoScreen1 = new InfoScreen("Place your " + actualShip.toString());
            infoScreen1.showInfoScreen();
        }
    }
    public void showShipPlacingMessage() {
        InfoScreen infoScreen = new InfoScreen("Place your " + actualShip.toString());
        infoScreen.showInfoScreen();
    }

    public void showYourTurnMessage() {
        InfoScreen infoScreen = new InfoScreen(YOUR_TURN);
        infoScreen.showInfoScreen();
    }

    public void showEnemysTurnMessage() {
        InfoScreen infoScreen = new InfoScreen(ENEMYS_TURN);
        infoScreen.showInfoScreen();
    }

}
