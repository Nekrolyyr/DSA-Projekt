package hsr.dsa.gui.game;

import hasr.dsa.test.GameTests;
import hsr.dsa.core.GameNotSetupException;
import hsr.dsa.core.IllegalMoveException;
import hsr.dsa.core.game.schiffe_versenken.GameChoreographer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static hsr.dsa.gui.UiConfiguration.*;

public class BattleField {


    private JFrame battleField;


    private JPanel framePanel;  // Frame that holds all the panels
    private JPanel fieldPanel;  // Holds the two battlefiels
    private JPanel yourField;   // Field with your ships
    private JPanel enemyField;  // Field with the enemys ships
    private JPanel shipPanel;   // Holds the ships at the bottom of the window

    private FieldButton[][] fields;

    private GameChoreographer gameChoreographer;

    private JLabel timerLabel;
    private JLabel corvette1;
    private JLabel corvette2;
    private JLabel destroyer;
    private JLabel battleship;

    public BattleField() {

        timerLabel = new JLabel();

        gameChoreographer = new GameChoreographer(GameChoreographer.Type.ACTIVE,
                () -> {
                    System.out.println("Time ran out!");
                    timerLabel.setText("Time ran out! Noob!");
                },
                remainingSecond -> {
                    System.out.println("You have "+remainingSecond+" seconds to make a move!");
                    timerLabel.setText(String.valueOf(remainingSecond));
                    if (remainingSecond <= 5) {
                        timerLabel.setForeground(Color.RED);
                    }
                },
                () -> System.out.println("Game Has ended!"));
        GameTests.testSetup(gameChoreographer);
        try {
            gameChoreographer.start();
        }catch (GameNotSetupException e){
            System.out.println("Game was not Setup Correctly");
        }

        JPanel namePanel = createNamePanel(gameChoreographer); // On top of the Battlefield, to show which field is yours

        fieldPanel = new JPanel(new GridLayout(1, 2));
        fieldPanel.setPreferredSize(new Dimension(FIELD_PANEL_WIDTH, FIELD_PANEL_HEIGHT));
        yourField = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        yourField.setPreferredSize(new Dimension((int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        yourField.setBorder(FIELD_BORDER);
        enemyField = new JPanel(new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS));
        enemyField.setPreferredSize(new Dimension((int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight()), (int)(0.8 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
        enemyField.setBorder(FIELD_BORDER);


        createShipPanel();


        GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener = shot -> {

        };
        fields = new FieldButton[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        generateFields(yourField, remotePlayerMoveAnswerListener);
        generateFields(enemyField, remotePlayerMoveAnswerListener);

        fieldPanel.add(yourField);
        fieldPanel.add(enemyField);

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

    private void createShipPanel() {
        Icon icon = new ImageIcon("Corvette.png");
        corvette1 = new JLabel(icon);
        corvette2 = new JLabel(icon);
        icon = new ImageIcon("Destroyer.png");
        destroyer = new JLabel(icon);
        icon = new ImageIcon("Battleship.png");
        battleship = new JLabel(icon);


        shipPanel = new JPanel(new GridLayout(1, NUMBER_OF_SHIPS));
        shipPanel.setPreferredSize(new Dimension((int)BATTLEFIELD_WINDOW_SIZE.getWidth(), (int)(0.15 * BATTLEFIELD_WINDOW_SIZE.getHeight())));
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

    private void generateFields(JPanel field, GameChoreographer.RemotePlayerMoveAnswerListener remotePlayerMoveAnswerListener) {
        for (int y = 0; y < NUMBER_OF_ROWS; y++) {
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                fields[y][x] = new FieldButton(x,y);
                fields[y][x].setFieldButtonClickListener((xPos,yPos)->{
                    try{
                        gameChoreographer.localPlayermove(xPos,yPos, remotePlayerMoveAnswerListener);
                    }catch(IllegalMoveException e){
                        System.out.println("This was a Illegal Move!");
                    }});
                field.add(fields[y][x]);
            }
        }
    }


}
